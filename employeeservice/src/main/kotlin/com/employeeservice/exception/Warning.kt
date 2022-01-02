package com.employeeservice.exception

data class Warning (
    val msg: String?
    ): RuntimeException()