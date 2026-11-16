package amirparsa.shk.omnispace.feature.qa.domain.model

/**
 * Domain enumeration for Test Case Status in Zephyr QA.
 */
enum class TestStatus(val titleFa: String, val colorHex: Long) {
    PASS("پاس‌شده", 0xFF10B981L),
    FAIL("شکست‌خورده", 0xFFEF4444L),
    BLOCKED("مسدود", 0xFFF59E0BL),
    UNEXECUTED("نیازمند اجرا", 0xFF38BDF8L)
}
