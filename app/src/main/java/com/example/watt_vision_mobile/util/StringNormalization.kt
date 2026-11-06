package com.example.watt_vision_mobile.util

import java.text.Normalizer

object StringNormalization {
    fun normalize(input: String?): String {
        if (input.isNullOrBlank()) return ""
        val nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD)
        val pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(nfdNormalizedString).replaceAll("").lowercase()
    }
}
