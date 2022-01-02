package com.mangerservice.modal


import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.types.ObjectId
import org.hibernate.validator.constraints.UniqueElements
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size
import kotlin.math.min


@Document("manager")
data class ManagerAuth (
    @Id
    var _id : ObjectId? = null,
    @field:Email(message = "username should be same as email (example@something.com)")
    @field:NotBlank(message = "username should not be blank")
    @field:Size(min = 8, max = 30, message = "username should be in range of 6-30 chars ")
    var username: String,
    @field:NotBlank(message = "Name should not be blank")
    var name:String?,
    @field:Email(message = "Email should be in proper format (example@something.com)")
    @field:NotBlank(message = "Email should not be blank")
    @field:Size(min = 8, max = 30, message = "Email should have length greater than 6")
    var email:String,
    @field:NotBlank(message = "Password should not be blank")
    @field:Size(min = 6, max = 12, message = "Password length must be between 6-12")
    //@JsonIgnore(value = true)
    var password: String
)