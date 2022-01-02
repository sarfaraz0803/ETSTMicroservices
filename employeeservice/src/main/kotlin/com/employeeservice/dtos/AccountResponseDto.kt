package com.employeeservice.dtos

import com.employeeservice.modal.Account
import com.employeeservice.modal.Employee
import com.employeeservice.modal.TimeSheet

data class AccountResponseDto (
        var account: Account,
        var token: String
)