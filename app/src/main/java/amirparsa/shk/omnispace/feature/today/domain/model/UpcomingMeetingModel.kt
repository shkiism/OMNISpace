package amirparsa.shk.omnispace.feature.today.domain.model

data class UpcomingMeetingModel(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val timeFormatted: String = "",
    val durationText: String = "",
    val platformName: String = "LiveKit Meet",
    val meetUrl: String = "",
    val startTimeMillis: Long = 0L,
    val endTimeMillis: Long = 0L,
    val location: String = "",
    val description: String = "",
    val attendeeInitials: List<String> = emptyList(),
    val agendaPoints: List<String> = emptyList()
)
