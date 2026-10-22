package com.project.cruise.android.data.dto.chat

// Request gửi lên Dialogflow Cloud
data class DialogflowDetectIntentRequest(
    val queryInput: QueryInput
)

data class QueryInput(
    val event: EventInput? = null,
    val text: TextInput? = null
)

data class EventInput(
    val name: String,
    val languageCode: String = "vi"
)

data class TextInput(
    val text: String,
    val languageCode: String = "vi"
)

// Response nhận về từ Dialogflow Cloud (bạn có thể mở rộng sau nếu cần)
data class DialogflowDetectIntentResponse(
    val responseId: String?,
    val queryResult: QueryResult?
)

data class QueryResult(
    val queryText: String?,
    val fulfillmentText: String?,
    val intent: IntentInfo?
)

data class IntentInfo(
    val displayName: String?
)