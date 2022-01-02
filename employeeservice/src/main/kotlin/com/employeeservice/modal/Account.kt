package com.employeeservice.modal
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.DocumentReference
import javax.validation.constraints.NotBlank


@Document("Account")
data class Account(
    @Id
    var _id: Int,        // employeeAccount number
    var username: String,
    @field:NotBlank
    var password: String,
    var employee: Employee,
    var timeSheet: MutableList<TimeSheet> = mutableListOf(),
)
