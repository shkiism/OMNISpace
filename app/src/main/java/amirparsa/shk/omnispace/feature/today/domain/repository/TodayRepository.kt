package amirparsa.shk.omnispace.feature.today.domain.repository

import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.today.data.remote.dto.FocusSessionDto
import amirparsa.shk.omnispace.feature.today.data.remote.dto.FocusStatusDto
import amirparsa.shk.omnispace.feature.today.data.remote.dto.SyncResultDto
import amirparsa.shk.omnispace.feature.today.domain.model.HabitItem
import amirparsa.shk.omnispace.feature.today.domain.model.NoteModel
import amirparsa.shk.omnispace.feature.today.domain.model.UpcomingMeetingModel

interface TodayRepository {
    suspend fun getFocusStatus(): NetworkResult<FocusStatusDto>
    suspend fun startFocus(minutes: Int, taskId: String? = null): NetworkResult<FocusSessionDto>
    suspend fun stopFocus(): NetworkResult<FocusSessionDto>
    suspend fun getHabits(): NetworkResult<List<HabitItem>>
    suspend fun toggleHabit(habitId: String, isDone: Boolean): NetworkResult<Boolean>
    suspend fun createHabit(title: String, schedule: String = "daily"): NetworkResult<String>
    suspend fun getNotes(): NetworkResult<List<NoteModel>>
    suspend fun createNote(title: String, content: String): NetworkResult<NoteModel>
    suspend fun updateNote(id: String, title: String, content: String, isPinned: Boolean): NetworkResult<Boolean>
    suspend fun togglePinNote(id: String, isPinned: Boolean): NetworkResult<Boolean>
    suspend fun deleteNote(id: String): NetworkResult<Boolean>
    suspend fun getUpcomingMeeting(): UpcomingMeetingModel?
    suspend fun syncPrivateZone(): NetworkResult<SyncResultDto>
    suspend fun toggleTask(taskId: String, isDone: Boolean): NetworkResult<Boolean>
}

