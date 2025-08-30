package com.back.standard.util

import com.back.global.exception.ServiceException

fun <T> T?.orServiceThrow(message: String): T =
    this ?: throw ServiceException(400, message)