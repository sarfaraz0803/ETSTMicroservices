package com.employeeservice.client

import com.employeeservice.dtos.SetLogTimeDto
import com.employeeservice.dtos.SetTaskStatusDto
import com.employeeservice.modal.Account
import com.employeeservice.modal.TimeSheet
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import javax.validation.Valid


@FeignClient(url="http://employeeaccountservice-env.eba-gwtwmq6p.us-east-1.elasticbeanstalk.com/account", name="employeeClient")
interface EmployeeClient {

    @GetMapping("/getAccountById/{id}")
    fun getAccountById(@PathVariable id:Int):Account

    @PutMapping("/updateAccountById/{id}")
    fun updateAccountById(@PathVariable id: Int, @Valid @RequestBody account: Account): Any

    @PutMapping("/setTaskFields")
    fun updateTaskFields(@RequestBody setTaskStatusDto: SetTaskStatusDto):String

    @PutMapping("/loginTime/{id}/{date}/{time}")
    fun sendLoginTime(@PathVariable id: Int, @PathVariable date:String, @PathVariable time:String)

    @PutMapping("/logoutTime/{id}/{date}/{time}")
    fun sendLogOutTime(@PathVariable id: Int, @PathVariable date:String, @PathVariable time:String)




}