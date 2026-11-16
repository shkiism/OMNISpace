package amirparsa.shk.omnispace.feature.work.domain.repository

import amirparsa.shk.omnispace.core.utils.NetworkResult
import amirparsa.shk.omnispace.feature.work.data.remote.dto.ConfigDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.CreateIssuePayload
import amirparsa.shk.omnispace.feature.work.data.remote.dto.CreateIssueResponseDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.IssueCommentDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.IssueDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.IssueTransitionDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.ProjectDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.TestCaseDto
import amirparsa.shk.omnispace.feature.work.data.remote.dto.WorkflowDto

interface WorkRepository {
    suspend fun getProjects(): NetworkResult<List<ProjectDto>>
    suspend fun getConfig(): NetworkResult<ConfigDto>
    suspend fun getProjectWorkflow(projectId: String): NetworkResult<WorkflowDto>
    suspend fun getIssues(projectId: String): NetworkResult<List<IssueDto>>
    suspend fun getIssueByKey(key: String): NetworkResult<IssueDto>
    suspend fun createIssue(payload: CreateIssuePayload): NetworkResult<CreateIssueResponseDto>

    // Issue transitions
    suspend fun getIssueTransitions(issueId: String): NetworkResult<List<IssueTransitionDto>>
    suspend fun transitionIssue(issueId: String, transitionId: String): NetworkResult<Boolean>

    // Issue comments
    suspend fun getIssueComments(issueId: String): NetworkResult<List<IssueCommentDto>>
    suspend fun addIssueComment(issueId: String, body: String): NetworkResult<IssueCommentDto>

    // Backward compatibility
    suspend fun getComments(issueId: String): NetworkResult<List<IssueCommentDto>>
    suspend fun addComment(issueId: String, body: String): NetworkResult<Unit>

    // Time tracking / Work logging
    suspend fun logWorkTime(issueId: String, timeSpentMinutes: Int, comment: String? = null): NetworkResult<Boolean>

    suspend fun getTests(projectId: String? = null): NetworkResult<List<TestCaseDto>>
}

