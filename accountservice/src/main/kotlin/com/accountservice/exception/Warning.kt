package com.accountservice.exception

data class Warning (
    val msg: String?
    ): RuntimeException()