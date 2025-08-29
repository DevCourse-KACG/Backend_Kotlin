package com.back.domain.schedule.schedule.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import java.time.LocalDateTime

@JvmRecord
data class ScheduleCreateReqBody(
    @Schema(description = "모임 ID")
    val clubId: Long,

    @Schema(description = "일정 제목")
    @field:NotEmpty
    val title: String,

    @Schema(description = "일정 내용")
    @field:NotEmpty
    val content: String,

    @Schema(description = "일정 시작일")
    val startDate: LocalDateTime,

    @Schema(description = "일정 종료일")
    val endDate: LocalDateTime,

    @Schema(description = "일정 장소")
    val spot: String
)
