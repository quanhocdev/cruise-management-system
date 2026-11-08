package com.project.cruise.android.data.dto.chat

data class ChatPageResponse<T>(
    val content: List<T> = emptyList(),
    val totalPages: Int = 0,
    val totalElements: Long = 0,
    val number: Int = 0,
    val size: Int = 0,
    val first: Boolean = false,
    val last: Boolean = false,
    val empty: Boolean = true
)