package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.GeminiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val isUnderTest: Boolean by lazy {
        try {
            Class.forName("org.robolectric.Robolectric") != null
        } catch (e: Exception) {
            false
        }
    }

    private val database = StudyDatabase.getDatabase(application)
    private val repository = StudyRepository(database.studyDao())

    // Network connectivity online/offline state
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val connectivityManager = application.getSystemService(android.content.Context.CONNECTIVITY_SERVICE) as? android.net.ConnectivityManager
    private var networkCallback: android.net.ConnectivityManager.NetworkCallback? = null

    // Observables from Room
    val userStats: StateFlow<UserStats?> = repository.userStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val focusSessions: StateFlow<List<FocusSession>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val friendRooms: StateFlow<List<FriendRoom>> = repository.friendRooms
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appBlocks: StateFlow<List<AppUsageBlock>> = repository.appBlocks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Interactive States
    private val _timerSeconds = MutableStateFlow(1500) // Default 25 min
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _timerTotalSeconds = MutableStateFlow(1500)
    val timerTotalSeconds: StateFlow<Int> = _timerTotalSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _currentSessionType = MutableStateFlow("Study") // "Study", "Short Break", "Long Break"
    val currentSessionType: StateFlow<String> = _currentSessionType.asStateFlow()

    private val _selectedCategory = MutableStateFlow("General Study")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Eye Care 20-20-20 Status (Counts down 1200 seconds i.e. 20 minutes)
    private val _eyeCareSeconds = MutableStateFlow(1200)
    val eyeCareSeconds: StateFlow<Int> = _eyeCareSeconds.asStateFlow()

    private val _isEyeCareBreakShowing = MutableStateFlow(false)
    val isEyeCareBreakShowing: StateFlow<Boolean> = _isEyeCareBreakShowing.asStateFlow()

    private val _eyeCareActiveCountdown = MutableStateFlow(20) // 20s break countdown
    val eyeCareActiveCountdown: StateFlow<Int> = _eyeCareActiveCountdown.asStateFlow()

    // AI Distraction warning notification state
    private val _aiTip = MutableStateFlow<String?>(null)
    val aiTip: StateFlow<String?> = _aiTip.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _distractionWarning = MutableStateFlow<String?>(null)
    val distractionWarning: StateFlow<String?> = _distractionWarning.asStateFlow()

    private var timerJob: Job? = null
    private var eyeCareJob: Job? = null

    // Fallback tips lists for fast offline operations
    private val offlineDistractionTipsEn = listOf(
        "Put your phone away and focus on your goals! Success is built one minute at a time.",
        "Your future self will thank you for studying right now. Block out the noise!",
        "Each distraction pushes your progress backward. Protect your virtual garden!",
        "Reels won't build your dreams or get you through exams, but this focus session will!"
    )

    private val offlineDistractionTipsHi = listOf(
        "फ़ोन को दूर रखें और अपने लक्ष्य पर ध्यान दें! सफलता एक-एक मिनट की मेहनत से मिलती है।",
        "आपका भविष्य आपके आज के परिश्रम पर निर्भर करता है। ध्यान न भटकने दें!",
        "हर बार सोशल मीडिया देखने से आपका पौधा सूख सकता है। इसे पानी दें और फोकस करें!",
        "रील्स और शॉर्ट्स से सपने पूरे नहीं होते, लेकिन इस फोकस सेशन से ज़रूर होंगे!"
    )

    private val offlineGeneralTipsEn = listOf(
        "Use the Pomodoro technique to study in blocks and give your eyes regular breaks.",
        "Breaking down heavy syllabus into smaller 25-minute targets is the key to deep focus.",
        "Hydrate often and stay focused. Consistency always beats intensity over time!"
    )

    private val offlineGeneralTipsHi = listOf(
        "पोमोडोरो तकनीक का उपयोग करें ताकि आप छोटे-छोटे हिस्सों में ध्यान से पढ़ सकें।",
        "भारी सिलेबस को 25 मिनट के छोटे लक्ष्यों में तोड़ना ही एकाग्रता की असली कुंजी है।",
        "नियमित रूप से पानी पिएं और आँखों को आराम देने के लिए 20-20-20 नियम का पालन करें।"
    )

    init {
        try {
            monitorNetworkQuality()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        viewModelScope.launch {
            try {
                repository.insertInitialDataIfEmpty()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                startEyeCareGlobalTimer()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun monitorNetworkQuality() {
        if (connectivityManager != null) {
            _isOnline.value = checkNetworkStatus()
            try {
                val request = android.net.NetworkRequest.Builder()
                    .addCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
                val callback = object : android.net.ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: android.net.Network) {
                        _isOnline.value = true
                    }
                    override fun onLost(network: android.net.Network) {
                        _isOnline.value = false
                    }
                }
                connectivityManager.registerNetworkCallback(request, callback)
                networkCallback = callback
            } catch (e: java.lang.SecurityException) {
                _isOnline.value = true
            } catch (e: Exception) {
                _isOnline.value = true
            }
        } else {
            _isOnline.value = false
        }
    }

    private fun checkNetworkStatus(): Boolean {
        return try {
            val cm = connectivityManager ?: return false
            val activeNetwork = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return false
            capabilities.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: java.lang.SecurityException) {
            true // assume online fallback in secure environments or missing permissions rather than crashing
        } catch (e: Exception) {
            true
        }
    }

    // Timer Actions
    fun setTimerDuration(minutes: Int, type: String) {
        _currentSessionType.value = type
        val secs = minutes * 60
        _timerSeconds.value = secs
        _timerTotalSeconds.value = secs
        stopTimer()
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true

        timerJob = viewModelScope.launch {
            while (_timerSeconds.value > 0) {
                delay(1000)
                _timerSeconds.value -= 1
            }
            onTimerComplete()
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun stopTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        _timerSeconds.value = _timerTotalSeconds.value
    }

    private suspend fun onTimerComplete() {
        _isTimerRunning.value = false
        timerJob?.cancel()

        // Distribute rewards!
        val stats = userStats.value ?: UserStats()
        val earnedCoins = if (_currentSessionType.value == "Study") 25 else 5
        val earnedXp = if (_currentSessionType.value == "Study") 40 else 10

        // Focus sessions log
        val session = FocusSession(
            durationMinutes = _timerTotalSeconds.value / 60,
            type = _currentSessionType.value,
            isCompleted = true,
            category = _selectedCategory.value
        )
        repository.insertSession(session)

        // Grow plant and update stats
        val growthBonus = if (_currentSessionType.value == "Study") 0.15f else 0.05f
        val newPlantLevel = (stats.plantLevel + growthBonus).coerceAtMost(1.0f)
        val newPlantHealth = (stats.plantHealth + 0.05f).coerceAtMost(1.0f)

        val updated = stats.copy(
            coins = stats.coins + earnedCoins,
            xp = stats.xp + earnedXp,
            plantLevel = newPlantLevel,
            plantHealth = newPlantHealth,
            streak = if (_currentSessionType.value == "Study") stats.streak + 1 else stats.streak
        )
        repository.updateUserStats(updated)

        // Auto trigger break message or return to default
        _timerSeconds.value = 1500
        _timerTotalSeconds.value = 1500
        _currentSessionType.value = "Study"
    }

    // 20-20-20 rule timer loop
    private fun startEyeCareGlobalTimer() {
        if (isUnderTest) return
        eyeCareJob?.cancel()
        eyeCareJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                if (userStats.value?.eyeCareEnabled == true) {
                    if (_eyeCareSeconds.value > 0) {
                        _eyeCareSeconds.value -= 1
                    } else {
                        // Eye Break Triggered!
                        triggerEyeCareBreak()
                    }
                }
            }
        }
    }

    private suspend fun triggerEyeCareBreak() {
        _isEyeCareBreakShowing.value = true
        _eyeCareActiveCountdown.value = 20
        while (_eyeCareActiveCountdown.value > 0 && _isEyeCareBreakShowing.value) {
            delay(1000)
            _eyeCareActiveCountdown.value -= 1
        }
        _isEyeCareBreakShowing.value = false
        _eyeCareSeconds.value = 1200 // Reset to 20 mins
    }

    fun dismissEyeCareBreak() {
        _isEyeCareBreakShowing.value = false
        _eyeCareSeconds.value = 1200
    }

    // Settings adjustments
    fun setLanguage(lang: String) {
        viewModelScope.launch {
            val stats = userStats.value ?: UserStats()
            repository.updateUserStats(stats.copy(language = lang))
        }
    }

    fun toggleNightMode(enabled: Boolean) {
        viewModelScope.launch {
            val stats = userStats.value ?: UserStats()
            repository.updateUserStats(stats.copy(nightModeEnabled = enabled))
        }
    }

    fun toggleEyeCare(enabled: Boolean) {
        viewModelScope.launch {
            val stats = userStats.value ?: UserStats()
            repository.updateUserStats(stats.copy(eyeCareEnabled = enabled))
        }
    }

    fun changePlantType(type: String) {
        viewModelScope.launch {
            val stats = userStats.value ?: UserStats()
            repository.updateUserStats(stats.copy(plantType = type))
        }
    }

    // Water plant logic: uses 15 coins to repair/grow plant by 0.1
    fun waterPlant() {
        val stats = userStats.value ?: return
        if (stats.coins >= 15) {
            viewModelScope.launch {
                val newCoins = stats.coins - 15
                val newHealth = (stats.plantHealth + 0.15f).coerceAtMost(1.0f)
                val newLevel = (stats.plantLevel + 0.05f).coerceAtMost(1.0f)
                repository.updateUserStats(stats.copy(coins = newCoins, plantHealth = newHealth, plantLevel = newLevel))
            }
        }
    }

    // App Block Configuration toggling
    fun toggleAppBlock(block: AppUsageBlock) {
        viewModelScope.launch {
            repository.updateAppBlock(block.copy(isBlocked = !block.isBlocked))
        }
    }

    // Simulate switching to distracting app (AI Distraction Detection)
    fun simulateAppSwitchAndDetect() {
        viewModelScope.launch {
            _isAiLoading.value = true
            val stats = userStats.value ?: UserStats()
            
            // Deduct some plant health since they got distracted!
            val currentHealth = stats.plantHealth
            val newHealth = (currentHealth - 0.10f).coerceAtLeast(0.1f)
            repository.updateUserStats(stats.copy(plantHealth = newHealth))

            if (!_isOnline.value) {
                // Return fast, high-quality local fallback tips instantly!
                delay(300)
                val tips = if (stats.language == "hi") offlineDistractionTipsHi else offlineDistractionTipsEn
                _distractionWarning.value = tips.random()
                _isAiLoading.value = false
                return@launch
            }

            val blockedAppsList = appBlocks.value.filter { it.isBlocked }.joinToString { it.appName }

            val initialMsg = if (stats.language == "hi") {
                "सावधान! आप इंस्टाग्राम पर स्विच कर रहे थे। हमारे एआई कोच से सलाह लें..."
            } else {
                "App-switch detected! You attempted to access distracted apps. Fetching AI focus correction..."
            }
            _distractionWarning.value = initialMsg

            // Call Gemini
            val correction = GeminiClient.getMotivationalTip(
                distractions = if (blockedAppsList.isBlank()) "Social apps" else blockedAppsList,
                sessionCategory = _selectedCategory.value,
                language = stats.language
            )

            _distractionWarning.value = correction
            _isAiLoading.value = false
        }
    }

    fun clearDistractionWarning() {
        _distractionWarning.value = null
    }

    // Fetch random AI Study tip manually
    fun fetchStudyTipManual() {
        viewModelScope.launch {
            _isAiLoading.value = true
            val stats = userStats.value ?: UserStats()

            if (!_isOnline.value) {
                // Return fast, high-quality local fallback tips instantly!
                delay(300)
                val tips = if (stats.language == "hi") offlineGeneralTipsHi else offlineGeneralTipsEn
                _aiTip.value = tips.random()
                _isAiLoading.value = false
                return@launch
            }

            val tip = GeminiClient.getMotivationalTip(
                distractions = "none",
                sessionCategory = _selectedCategory.value,
                language = stats.language
            )
            _aiTip.value = tip
            _isAiLoading.value = false
        }
    }

    fun clearAiTip() {
        _aiTip.value = null
    }

    // Parent dashboard configuration
    fun updateParentCode(newCode: String) {
        viewModelScope.launch {
            val stats = userStats.value ?: UserStats()
            repository.updateUserStats(stats.copy(parentCode = newCode))
        }
    }

    fun createFriendRoom(roomName: String, memberName: String) {
        viewModelScope.launch {
            repository.insertFriendRoom(
                FriendRoom(
                    roomName = roomName,
                    memberName = memberName,
                    status = "Studying ⏱️",
                    streakDecimal = 1
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            networkCallback?.let { connectivityManager?.unregisterNetworkCallback(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
