package com.example.network

import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .writeTimeout(6, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: GeminiApiService by lazy {
        retrofit.create(GeminiApiService::class.java)
    }

    suspend fun getMotivationalTip(distractions: String, sessionCategory: String, language: String): String {
        val key = BuildConfig.GEMINI_API_KEY
        if (key.isBlank() || key == "MY_GEMINI_API_KEY") {
            return if (language == "hi") {
                "ध्यान केंद्रित रखें! आप यह कर सकते हैं 💪"
            } else {
                "Stay focused! You've got this 💪"
            }
        }

        val prompt = """
            You are StudyShield AI Coach. The student is doing a focus session for target track "$sessionCategory".
            However, student is prone to checking distracting apps: "$distractions".
            Generate a short, powerful, highly motivating 1-to-2 sentence study tip or encouragement.
            Provide response in ${if (language == "hi") "Hindi (using friendly Hindi Devanagari script paired with a touch of English encouragement or emojis)" else "English"}.
            Keep it actionable and warm! Avoid long explanations, structure, or hashtags.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = prompt)))
            ),
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = "You are StudyShield, an encouraging AI wellness coach for students. Be concise, warm, helpful, Hindi and English speaking."))
            ),
            generationConfig = GeminiGenerationConfig(temperature = 0.7f, maxOutputTokens = 150)
        )

        return try {
            val response = service.generateContent(key, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
                ?: "Believe in yourself, every minute of focus brings you closer to your dreams!"
        } catch (e: Exception) {
            e.printStackTrace()
            if (language == "hi") {
                "आगे बढ़ें और ध्यान केंद्रित रखें! 📚🛡️"
            } else {
                "Keep going, protect your focus to grow your virtual garden! 📚🛡️"
            }
        }
    }
}
