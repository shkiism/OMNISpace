package amirparsa.shk.omnispace.feature.qa.domain.model

/**
 * Domain entity representing a test case in Zephyr QA cycle.
 */
data class ZephyrTestCase(
    val id: String,
    val title: String,
    val module: String,
    val status: TestStatus,
    val stepsSummary: String,
    val executedBy: String? = null,
    val executionTime: String? = null,
    val errorMessage: String? = null,
    val bugIssueKey: String? = null
)

