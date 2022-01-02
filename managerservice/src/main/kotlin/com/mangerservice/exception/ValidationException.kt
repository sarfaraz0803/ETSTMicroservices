package com.mangerservice.exception

data class ValidationException(
    var myMessage :MutableList<String?>
):RuntimeException()
