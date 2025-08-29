package com.back.domain.schedule.schedule.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@JvmRecord
data class ScheduleUpdateReqBody(
    @Schema(description = "일정 제목")
    @field:NotEmpty
    val title: String,

    @Schema(description = "일정 내용")
    @field:NotEmpty
    val content: String,

    @Schema(description = "일정 시작일")
    @field:NotNull
    val startDate: LocalDateTime,

    @Schema(description = "일정 종료일")
    @field:NotNull
    val endDate: LocalDateTime,

    @Schema(description = "일정 장소")
    @field:NotNull
    val spot: String
)
