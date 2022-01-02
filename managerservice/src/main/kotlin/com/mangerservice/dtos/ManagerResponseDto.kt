package com.mangerservice.dtos

data class ManagerResponseDto(
    var userId:String,
    var username:String,
    var name:String?,
    var email:String,
    var password:String,
    var token:String
)