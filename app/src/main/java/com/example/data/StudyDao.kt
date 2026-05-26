package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    // User Stats query
    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    fun getUserStatsFlow(): Flow<UserStats?>

    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    suspend fun getUserStats(): UserStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStats(stats: UserStats)

    @Update
    suspend fun updateUserStats(stats: UserStats)


    // Focus Session queries
    @Query("SELECT * FROM focus_sessions ORDER BY timestamp DESC")
    fun getAllSessionsFlow(): Flow<List<FocusSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSession)


    // Friend Room queries
    @Query("SELECT * FROM friend_rooms ORDER BY id ASC")
    fun getAllFriendRoomsFlow(): Flow<List<FriendRoom>>

    @Query("SELECT * FROM friend_rooms ORDER BY id ASC")
    suspend fun getAllFriendRooms(): List<FriendRoom>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendRoom(room: FriendRoom)

    @Query("DELETE FROM friend_rooms")
    suspend fun deleteAllFriendRooms()


    // App Blocking queries
    @Query("SELECT * FROM app_usage_blocks ORDER BY appName ASC")
    fun getAllAppBlocksFlow(): Flow<List<AppUsageBlock>>

    @Query("SELECT * FROM app_usage_blocks ORDER BY appName ASC")
    suspend fun getAllAppBlocks(): List<AppUsageBlock>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppBlock(block: AppUsageBlock)

    @Update
    suspend fun updateAppBlock(block: AppUsageBlock)
}
