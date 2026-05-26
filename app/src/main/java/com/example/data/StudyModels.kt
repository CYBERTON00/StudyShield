package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val coins: Int = 100,
    val xp: Int = 50,
    val streak: Int = 3,
    val lastActiveDate: String = "2026-05-26",
    val plantLevel: Float = 0.2f, // 0.0 to 1.0
    val plantType: String = "Bonsai",
    val plantHealth: Float = 0.8f, // 0.0 to 1.0
    val parentCode: String = "SHIELD-2026",
    val nightModeEnabled: Boolean = false,
    val eyeCareEnabled: Boolean = true,
    val language: String = "en" // "en" for English, "hi" for Hindi
)

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String, // "Study", "Short Break", "Long Break"
    val isCompleted: Boolean,
    val category: String // "Math", "Science", "General Study", "Coding"
)

@Entity(tableName = "friend_rooms")
data class FriendRoom(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roomName: String,
    val memberName: String,
    val status: String, // "Studying ⏱️", "On Break ☕", "AFK 💤"
    val streakDecimal: Int
)

@Entity(tableName = "app_usage_blocks")
data class AppUsageBlock(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appName: String,
    val packageName: String,
    val isBlocked: Boolean,
    val category: String
)
