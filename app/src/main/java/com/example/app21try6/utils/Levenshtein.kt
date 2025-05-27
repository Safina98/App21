package com.example.app21try6.utils

class Levenshtein {
    fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[a.length][b.length]
    }

    fun similarity(s1: String, s2: String): Double {
        val maxLen = maxOf(s1.length, s2.length)
        return if (maxLen == 0) 1.0 else (maxLen - levenshtein(s1, s2)) / maxLen.toDouble()
    }

    fun filterSuggestions(input: String, list: List<String>): List<String> {
        val normalizedInput = input.lowercase().trim()
        val inputWords = normalizedInput.split(" ")


        return list.filter { item ->
            val itemWords = item.lowercase().split(" ")

            // If input looks like multiple words, do fuzzy matching on each word
            if (inputWords.size > 1) {
                // Match all input words somewhere in item words (fuzzy)
                inputWords.all { inputWord ->
                    itemWords.any { itemWord ->
                        (0.. itemWord.length - input.length).any { i ->
                            val substring =  itemWord.substring(i, i + input.length)
                            levenshtein(input, substring) <= 1  // Allow 1 typo/missing char
                        } ||
                                itemWord.contains(inputWord) ||
                                similarity(inputWord, itemWord) >= 0.6 ||
                                levenshtein(inputWord, itemWord) <= 2
                    }
                }
            } else {
                // Single word or number input
                // If input is numeric and item contains numbers, match substring anywhere
                if (normalizedInput.all { it.isDigit() }) {
                    item.contains(normalizedInput)
                } else {
                    // Text input, do fuzzy word matching on any word
                    itemWords.any { word ->
                        word.contains(normalizedInput) ||
                                similarity(normalizedInput, word) >= 0.6 ||
                                levenshtein(normalizedInput, word) <= 2
                    }
                }
            }
        }
    }
}