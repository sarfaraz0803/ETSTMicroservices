package com.mangerservice.dtos

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class ManagerAuthDto(
    @field:Email(message = "username should be same as email (example@something.com)")
    @field:NotBlank(message = "username should not be blank")
    @field:Size(min = 8, max = 30, message = "username should be in range of 6-30 chars ")
    var username: String,
    @field:NotBlank(message = "Password should not be blank")
    @field:Size(min = 6, max = 12, message = "Password length must be between 6-12")
    //@JsonIgnore(value = true)
    var password: String
)
