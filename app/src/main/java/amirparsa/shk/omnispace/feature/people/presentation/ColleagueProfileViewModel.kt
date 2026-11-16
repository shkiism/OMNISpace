package amirparsa.shk.omnispace.feature.people.presentation

import androidx.lifecycle.viewModelScope
import amirparsa.shk.omnispace.core.base.mvi.MviViewModel
import amirparsa.shk.omnispace.core.database.dao.PersonDao
import amirparsa.shk.omnispace.core.database.dao.TaskDao
import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.connect.domain.repository.ConnectRepository
import amirparsa.shk.omnispace.feature.people.domain.model.SharedTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * MVI ViewModel managing colleague profile presentation state.
 */
@HiltViewModel
class ColleagueProfileViewModel @Inject constructor(
    private val personDao: PersonDao,
    private val connectRepository: ConnectRepository,
    private val taskDao: TaskDao
) : MviViewModel<ColleagueProfileUiState, ColleagueProfileIntent, ColleagueProfileEffect>(
    initialState = ColleagueProfileUiState(isLoading = true)
) {

    fun initData(
        colleagueName: String,
        jobTitle: String = "",
        department: String = "",
        employeeCode: String = "",
        isOnline: Boolean = false,
        email: String = ""
    ) {
        setState {
            copy(
                colleagueName = colleagueName,
                jobTitle = jobTitle,
                department = department,
                employeeCode = employeeCode,
                isOnline = isOnline,
                email = email,
                isLoading = true
            )
        }

        viewModelScope.launch {
            // 1. Look up cached person from Room DB
            val cachedPeople = withContext(Dispatchers.IO) { personDao.getAllPeople() }
            val matchedPerson = cachedPeople.firstOrNull {
                it.displayName.equals(colleagueName, ignoreCase = true) ||
                (it.handle != null && it.handle.equals(colleagueName, ignoreCase = true))
            }

            if (matchedPerson != null) {
                setState {
                    copy(
                        jobTitle = matchedPerson.orgRole ?: jobTitle.ifBlank { "همکار سازمانی" },
                        department = department.ifBlank { "تیم محصول و مهندسی" },
                        employeeCode = matchedPerson.id.take(8).uppercase(),
                        email = matchedPerson.email ?: email,
                        isOnline = isOnline,
                        isLoading = false
                    )
                }
            } else {
                // Fetch from network
                when (val result = connectRepository.getPeople()) {
                    is NetworkResult.Success -> {
                        val person = result.data.firstOrNull {
                            it.displayName.equals(colleagueName, ignoreCase = true) ||
                            (it.handle != null && it.handle.equals(colleagueName, ignoreCase = true))
                        }
                        if (person != null) {
                            setState {
                                copy(
                                    jobTitle = person.orgRole ?: "همکار سازمانی",
                                    department = "تیم محصول و مهندسی",
                                    employeeCode = person.id.take(8).uppercase(),
                                    email = person.email ?: "",
                                    isOnline = true,
                                    isLoading = false
                                )
                            }
                        } else {
                            setState { copy(isLoading = false) }
                        }
                    }
                    else -> {
                        setState { copy(isLoading = false) }
                    }
                }
            }

            // Load any shared tasks from TaskDao
            val tasks = withContext(Dispatchers.IO) { taskDao.getAllTasks() }
            val shared = tasks.filter {
                it.assigneeId?.contains(colleagueName, ignoreCase = true) == true ||
                it.title.contains(colleagueName, ignoreCase = true)
            }.map {
                val isDone = it.statusId?.contains("done", ignoreCase = true) == true
                SharedTask(it.key, it.title, if (isDone) "تکمیل شده" else "در حال انجام")
            }
            setState { copy(sharedTasks = shared) }
        }
    }

    override fun processIntent(intent: ColleagueProfileIntent) {
        when (intent) {
            is ColleagueProfileIntent.SelectTab -> setState { copy(selectedTab = intent.index) }
            ColleagueProfileIntent.BackClicked -> sendEffect(ColleagueProfileEffect.NavigateBack)
            ColleagueProfileIntent.StartChatClicked -> sendEffect(ColleagueProfileEffect.StartChat(uiState.value.colleagueName))
            ColleagueProfileIntent.StartVideoCallClicked -> sendEffect(ColleagueProfileEffect.StartVideoCall(uiState.value.colleagueName))
            ColleagueProfileIntent.SendEmailClicked -> sendEffect(ColleagueProfileEffect.SendEmail(uiState.value.email))
            is ColleagueProfileIntent.OpenTaskClicked -> sendEffect(ColleagueProfileEffect.OpenTask(intent.issueKey))
        }
    }
}
