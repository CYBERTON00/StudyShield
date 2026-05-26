package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class StudyRepository(private val studyDao: StudyDao) {

    val userStats: Flow<UserStats?> = studyDao.getUserStatsFlow()
    val allSessions: Flow<List<FocusSession>> = studyDao.getAllSessionsFlow()
    val friendRooms: Flow<List<FriendRoom>> = studyDao.getAllFriendRoomsFlow()
    val appBlocks: Flow<List<AppUsageBlock>> = studyDao.getAllAppBlocksFlow()

    suspend fun updateUserStats(stats: UserStats) = withContext(Dispatchers.IO) {
        studyDao.updateUserStats(stats)
    }

    suspend fun insertUserStats(stats: UserStats) = withContext(Dispatchers.IO) {
        studyDao.insertUserStats(stats)
    }

    suspend fun insertSession(session: FocusSession) = withContext(Dispatchers.IO) {
        studyDao.insertSession(session)
    }

    suspend fun updateAppBlock(block: AppUsageBlock) = withContext(Dispatchers.IO) {
        studyDao.updateAppBlock(block)
    }

    suspend fun insertAppBlock(block: AppUsageBlock) = withContext(Dispatchers.IO) {
        studyDao.insertAppBlock(block)
    }

    suspend fun insertFriendRoom(room: FriendRoom) = withContext(Dispatchers.IO) {
        studyDao.insertFriendRoom(room)
    }

    suspend fun insertInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val currentStats = studyDao.getUserStats()
        if (currentStats == null) {
            studyDao.insertUserStats(UserStats())
        }

        // Initialize App Blocks if empty
        val blocks = studyDao.getAllAppBlocks()
        if (blocks.isEmpty()) {
            val defaults = listOf(
                AppUsageBlock(appName = "Instagram", packageName = "com.instagram.android", isBlocked = true, category = "Social Media"),
                AppUsageBlock(appName = "YouTube Shorts", packageName = "com.google.android.youtube", isBlocked = true, category = "Video Streaming"),
                AppUsageBlock(appName = "TikTok Lite", packageName = "com.zhiliaoapp.musically", isBlocked = true, category = "Short Videos"),
                AppUsageBlock(appName = "Brawl Stars", packageName = "com.supercell.brawlstars", isBlocked = false, category = "Mobile Games"),
                AppUsageBlock(appName = "Instagram Reels (Stand-alone)", packageName = "com.instagram.reels", isBlocked = true, category = "Social Media")
            )
            for (block in defaults) {
                studyDao.insertAppBlock(block)
            }
        }

        // Initialize Friend Rooms if empty
        val rooms = studyDao.getAllFriendRooms()
        if (rooms.isEmpty()) {
            studyDao.deleteAllFriendRooms()
            val defaultRooms = listOf(
                FriendRoom(roomName = "IIT Aspirants 2026", memberName = "Aarav Sharma", status = "Studying ⏱️", streakDecimal = 12),
                FriendRoom(roomName = "IIT Aspirants 2026", memberName = "Priya Patel", status = "On Break ☕", streakDecimal = 8),
                FriendRoom(roomName = "Koto Club Coding", memberName = "Rajesh Kumar", status = "Studying ⏱️", streakDecimal = 15),
                FriendRoom(roomName = "Med School Prep", memberName = "Sneha Reddy", status = "AFK 💤", streakDecimal = 4)
            )
            for (room in defaultRooms) {
                studyDao.insertFriendRoom(room)
            }
        }
    }
}
