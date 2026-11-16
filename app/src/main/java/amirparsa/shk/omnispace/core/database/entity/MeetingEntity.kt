package amirparsa.shk.omnispace.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import amirparsa.shk.omnispace.feature.today.domain.model.UpcomingMeetingModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "meetings")
data class MeetingEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val subtitle: String,
    val timeFormatted: String,
    val durationText: String,
    val platformName: String,
    val meetUrl: String,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val location: String,
    val description: String,
    val attendeesJson: String,
    val agendaPointsJson: String
)

fun MeetingEntity.toDomainModel(gson: Gson = Gson()): UpcomingMeetingModel {
    val listType = object : TypeToken<List<String>>() {}.type
    val attendees: List<String> = try {
        gson.fromJson(attendeesJson, listType) ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }
    val agenda: List<String> = try {
        gson.fromJson(agendaPointsJson, listType) ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }

    return UpcomingMeetingModel(
        id = id,
        title = title,
        subtitle = subtitle,
        timeFormatted = timeFormatted,
        durationText = durationText,
        platformName = platformName,
        meetUrl = meetUrl,
        startTimeMillis = startTimeMillis,
        endTimeMillis = endTimeMillis,
        location = location,
        description = description,
        attendeeInitials = attendees,
        agendaPoints = agenda
    )
}

fun UpcomingMeetingModel.toEntity(gson: Gson = Gson()): MeetingEntity {
    return MeetingEntity(
        id = id,
        title = title,
        subtitle = subtitle,
        timeFormatted = timeFormatted,
        durationText = durationText,
        platformName = platformName,
        meetUrl = meetUrl,
        startTimeMillis = startTimeMillis,
        endTimeMillis = endTimeMillis,
        location = location,
        description = description,
        attendeesJson = gson.toJson(attendeeInitials),
        agendaPointsJson = gson.toJson(agendaPoints)
    )
}

