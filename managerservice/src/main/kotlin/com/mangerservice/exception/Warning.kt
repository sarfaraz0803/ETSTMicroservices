package com.mangerservice.exception


data class Warning (
    val msg: String?
    ): RuntimeException()