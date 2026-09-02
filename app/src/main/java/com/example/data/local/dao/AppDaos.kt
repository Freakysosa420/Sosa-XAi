package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ApiEndpointEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ContainerConfigEntity
import com.example.data.local.entity.GeneratedImageEntity
import com.example.data.local.entity.PromptFrameworkEntity
import com.example.data.local.entity.SearchReportEntity
import com.example.data.local.entity.TelemetryLogEntity
import com.example.data.local.entity.UserAccountEntity
import com.example.data.local.entity.VeoVideoEntity
import com.example.data.local.entity.VoiceSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ApiEndpointDao {
    @Query("SELECT * FROM api_endpoints ORDER BY id DESC")
    fun getAllEndpoints(): Flow<List<ApiEndpointEntity>>

    @Query("SELECT * FROM api_endpoints WHERE id = :id")
    suspend fun getEndpointById(id: Long): ApiEndpointEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEndpoint(endpoint: ApiEndpointEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(endpoints: List<ApiEndpointEntity>)

    @Update
    suspend fun updateEndpoint(endpoint: ApiEndpointEntity)

    @Delete
    suspend fun deleteEndpoint(endpoint: ApiEndpointEntity)

    @Query("SELECT COUNT(*) FROM api_endpoints")
    suspend fun getCount(): Int
}

@Dao
interface ContainerConfigDao {
    @Query("SELECT * FROM container_configs ORDER BY id DESC")
    fun getAllContainers(): Flow<List<ContainerConfigEntity>>

    @Query("SELECT * FROM container_configs WHERE id = :id")
    suspend fun getContainerById(id: Long): ContainerConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContainer(container: ContainerConfigEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(containers: List<ContainerConfigEntity>)

    @Update
    suspend fun updateContainer(container: ContainerConfigEntity)

    @Delete
    suspend fun deleteContainer(container: ContainerConfigEntity)

    @Query("SELECT COUNT(*) FROM container_configs")
    suspend fun getCount(): Int
}

@Dao
interface PromptFrameworkDao {
    @Query("SELECT * FROM prompt_frameworks ORDER BY id ASC")
    fun getAllFrameworks(): Flow<List<PromptFrameworkEntity>>

    @Query("SELECT * FROM prompt_frameworks WHERE id = :id")
    suspend fun getFrameworkById(id: Long): PromptFrameworkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFramework(framework: PromptFrameworkEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(frameworks: List<PromptFrameworkEntity>)

    @Update
    suspend fun updateFramework(framework: PromptFrameworkEntity)

    @Delete
    suspend fun deleteFramework(framework: PromptFrameworkEntity)

    @Query("SELECT COUNT(*) FROM prompt_frameworks")
    suspend fun getCount(): Int
}

@Dao
interface TelemetryLogDao {
    @Query("SELECT * FROM telemetry_logs ORDER BY timestamp DESC LIMIT 100")
    fun getRecentLogs(): Flow<List<TelemetryLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: TelemetryLogEntity): Long

    @Query("DELETE FROM telemetry_logs")
    suspend fun clearLogs()

    @Query("SELECT COUNT(*) FROM telemetry_logs")
    suspend fun getCount(): Int
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<ChatMessageEntity>)

    @Delete
    suspend fun deleteMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()

    @Query("SELECT COUNT(*) FROM chat_messages")
    suspend fun getCount(): Int
}

@Dao
interface VeoVideoDao {
    @Query("SELECT * FROM veo_videos ORDER BY timestamp DESC")
    fun getAllVideos(): Flow<List<VeoVideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VeoVideoEntity): Long

    @Delete
    suspend fun deleteVideo(video: VeoVideoEntity)

    @Query("DELETE FROM veo_videos")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM veo_videos")
    suspend fun getCount(): Int
}

@Dao
interface GeneratedImageDao {
    @Query("SELECT * FROM generated_images ORDER BY timestamp DESC")
    fun getAllImages(): Flow<List<GeneratedImageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: GeneratedImageEntity): Long

    @Delete
    suspend fun deleteImage(image: GeneratedImageEntity)

    @Query("DELETE FROM generated_images")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM generated_images")
    suspend fun getCount(): Int
}

@Dao
interface SearchReportDao {
    @Query("SELECT * FROM search_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<SearchReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: SearchReportEntity): Long

    @Delete
    suspend fun deleteReport(report: SearchReportEntity)

    @Query("DELETE FROM search_reports")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM search_reports")
    suspend fun getCount(): Int
}

@Dao
interface VoiceSessionDao {
    @Query("SELECT * FROM voice_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<VoiceSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: VoiceSessionEntity): Long

    @Delete
    suspend fun deleteSession(session: VoiceSessionEntity)

    @Query("DELETE FROM voice_sessions")
    suspend fun clearAll()
}

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_account LIMIT 1")
    fun getUserAccount(): Flow<UserAccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserAccount(user: UserAccountEntity)

    @Query("DELETE FROM user_account")
    suspend fun clearUserAccount()
}


