package com.mgacreative.mgaglobal.ui.education

import com.mgacreative.mgaglobal.core.util.IntToBooleanSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Education(
    val id: String = "",
    val title: String = "",
    val topic: String = "",
    val instructor: String = "",
    @SerialName("content_text")
    val contentText: String = "",
    @SerialName("video_url")
    val videoUrl: String = "",
    @SerialName("exam_link")
    val examLink: String = "",
    @SerialName("content_url")
    val contentUrl: String? = null,
    @SerialName("created_at")
    val createdAt: Long = 0L
)
