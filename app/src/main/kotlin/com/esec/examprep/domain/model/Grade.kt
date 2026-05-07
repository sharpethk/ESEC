package com.esec.examprep.domain.model

enum class Grade(val letter: String, val gpa: Float, val minPercent: Float) {
    A_PLUS("A+", 4.0f, 90f),
    A("A", 4.0f, 85f),
    A_MINUS("A-", 3.75f, 80f),
    B_PLUS("B+", 3.5f, 75f),
    B("B", 3.0f, 70f),
    B_MINUS("B-", 2.75f, 65f),
    C_PLUS("C+", 2.5f, 60f),
    C("C", 2.0f, 50f),
    D("D", 1.0f, 40f),
    F("F", 0.0f, 0f);

    val isPassing: Boolean get() = this != F

    companion object {
        fun fromPercent(percent: Float): Grade =
            entries.firstOrNull { percent >= it.minPercent } ?: F

        fun gpaFromPercent(percent: Float): Float = fromPercent(percent).gpa

        fun weightedGpa(items: List<Pair<Float, Int>>): Float {
            val totalWeight = items.sumOf { it.second }
            if (totalWeight == 0) return 0f
            val sum = items.sumOf { (percent, weight) ->
                (fromPercent(percent).gpa * weight).toDouble()
            }
            return (sum / totalWeight).toFloat()
        }
    }
}
