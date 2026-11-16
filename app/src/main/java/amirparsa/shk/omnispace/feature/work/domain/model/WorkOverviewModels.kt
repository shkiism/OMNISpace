package amirparsa.shk.omnispace.feature.work.domain.model

data class KanbanColumnOverviewItem(
    val columnTitle: String,
    val items: List<String> = emptyList()
)

data class WikiSpaceOverviewItem(
    val title: String,
    val subtitle: String,
    val colorHex: Long
)

data class ReportsQaOverviewItem(
    val velocitySp: String = "۰ SP",
    val velocityDiff: String = "",
    val passRate: String = "۰٪",
    val passRateDiff: String = ""
)
