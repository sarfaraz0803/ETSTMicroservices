package com.accountservice.client


import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*


@FeignClient(url="http://localhost:8763/employeeProfile",name="accountClient")
interface AccountClient {

    @GetMapping("/checkEmp/{id}/{username}")
    fun empCre(@PathVariable id:Int, @PathVariable username:String):Boolean

    @PostMapping("/registerCre/{id}/{username}/{pass}")
    fun registerCre(@PathVariable id:Int, @PathVariable username:String, @PathVariable pass:String):Any

    @DeleteMapping("/deleteEmpCre/{id}")
    fun deleteEmpCre(@PathVariable id:Int)

    @PutMapping("/updateEmpCre/{id}/{username}/{pass}")
    fun updateEmpCre(@PathVariable id:Int, @PathVariable username:String, @PathVariable pass:String):Any
}