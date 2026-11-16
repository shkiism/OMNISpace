package amirparsa.shk.omnispace.feature.issue.domain.model

enum class IssueStatusType(val labelFa: String, val colorHex: Long) {
    TODO("برای انجام", 0xFF64748BL),
    IN_PROGRESS("در حال انجام", 0xFF7C3AEDL),
    QA_REVIEW("بررسی کیفیت (QA)", 0xFF38BDF8L),
    DONE("تکمیل شده", 0xFF10B981L)
}
