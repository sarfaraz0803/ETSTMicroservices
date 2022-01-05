package com.employeeservice.client

import com.employeeservice.dtos.SetTaskStatusDto
import com.employeeservice.modal.Account
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@FeignClient(url="http://localhost:8762/account", name="employeeClient")
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