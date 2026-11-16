package amirparsa.shk.omnispace.feature.work.domain.model

enum class KanbanColumnStatus(val id: String, val titleFa: String, val colorHex: Long) {
    TODO("todo", "برای انجام", 0xFF94A3B8L),
    IN_PROGRESS("in_progress", "در حال انجام", 0xFF38BDF8L),
    QA_REVIEW("qa_review", "بررسی و تست QA", 0xFFF59E0BL),
    DONE("done", "تکمیل شده", 0xFF10B981L)
}
