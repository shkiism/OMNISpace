package amirparsa.shk.omnispace.feature.connect.presentation

import androidx.lifecycle.viewModelScope
import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.connect.domain.model.ChatItemUi
import amirparsa.shk.omnispace.feature.connect.domain.repository.ConnectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConnectViewModel @Inject constructor(
    private val connectRepo: ConnectRepository
) : MviViewModel<ConnectUiState, ConnectIntent, ConnectEffect>(
    initialState = ConnectUiState()
) {

    init {
        loadConnectData()
    }

    private fun loadConnectData() {
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }

            val peopleResult = connectRepo.getPeople()
            val teamsResult = connectRepo.getTeams()

            val livePeople = if (peopleResult is NetworkResult.Success) peopleResult.data else emptyList()
            val liveTeams = if (teamsResult is NetworkResult.Success) teamsResult.data else emptyList()

            val errorMessage = when {
                peopleResult is NetworkResult.Error && livePeople.isEmpty() -> peopleResult.message
                teamsResult is NetworkResult.Error && liveTeams.isEmpty() -> teamsResult.message
                else -> null
            }

            val generatedChats = mutableListOf<ChatItemUi>()

            // 1. Team Channels
            liveTeams.forEach { team ->
                generatedChats.add(
                    ChatItemUi(
                        id = team.id,
                        title = "#${team.name} (${team.key.uppercase()})",
                        subtitle = "مسئول تیم: ${team.leadName ?: "سازمان"} • ${team.members} عضو",
                        time = "امروز",
                        unreadCount = 0,
                        isChannel = true
                    )
                )
            }

            // 2. Direct Colleague Chats
            livePeople.forEach { person ->
                generatedChats.add(
                    ChatItemUi(
                        id = person.id,
                        title = person.displayName,
                        subtitle = if (!person.orgRole.isNullOrBlank()) person.orgRole else (person.email ?: "عضو تیم"),
                        time = "",
                        unreadCount = 0,
                        onlineStatusColor = null
                    )
                )
            }

            setState {
                copy(
                    peopleList = livePeople,
                    teamsList = liveTeams,
                    chatList = generatedChats,
                    liveRooms = emptyList(),
                    isLoading = false,
                    errorMessage = errorMessage
                )
            }
        }
    }

    override fun processIntent(intent: ConnectIntent) {
        when (intent) {
            is ConnectIntent.SelectSubTab -> {
                setState { copy(selectedSubTab = intent.tab) }
            }
            is ConnectIntent.ChatClicked -> {
                sendEffect(ConnectEffect.OpenChat(intent.chat))
            }
            ConnectIntent.JoinLiveKitClicked -> {
                sendEffect(ConnectEffect.JoinLiveKit)
            }
            ConnectIntent.NewInstantMeetingClicked -> {
                sendEffect(ConnectEffect.NewInstantMeeting)
            }
            ConnectIntent.ScheduleMeetingClicked -> {
                sendEffect(ConnectEffect.ScheduleMeeting)
            }
            is ConnectIntent.ColleagueClicked -> {
                sendEffect(ConnectEffect.OpenColleague(intent.name))
            }
            ConnectIntent.Refresh -> {
                loadConnectData()
            }
        }
    }
}
