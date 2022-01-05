package com.mangerservice.client

import com.mangerservice.dtos.TaskDto
import com.mangerservice.dtos.UpdateTaskDto

import com.mangerservice.modal.EmpTask
import com.mangerservice.modal.TimeSheet
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*
import javax.validation.Valid



@FeignClient(url="http://employeeaccountservice-env.eba-gwtwmq6p.us-east-1.elasticbeanstalk.com/account", name="managerClient")
interface ManagerClient {

    @PostMapping("/addAccount")
    fun addAccount(@Valid @RequestBody account:Any):Any

    @GetMapping("/getAllAccounts")
    fun getAllEmpAccounts():Any

    @GetMapping("/getAccountById/{id}")
    fun getAccountById(@PathVariable id:Int):Any

    @PutMapping("/updateAccountById/{id}")
    fun updateAccountById(@PathVariable id: Int, @Valid @RequestBody account: Any): Any

    @DeleteMapping("/deleteAccountById/{id}")
    fun deleteAccountById(@PathVariable id: Int): String

    /*@PutMapping("/assignTaskById/{id}")
    fun assignTaskById(@PathVariable id:Int, @RequestBody account: Account):Any*/



    // --------------------------TimeSheetOperations--------------------------------

    @PostMapping("/createSheet")
    fun createTimeSheet(@RequestBody empTimeSheet: TimeSheet): Any?

    @PutMapping("/assignTask/{id}/{date}")
    fun assignTask(@PathVariable id:Int, @PathVariable date:String, @RequestBody empTask: EmpTask):Any

    @GetMapping("/getTimeSheetById/{id}")
    fun getAllTimeSheets(@PathVariable id:Int):MutableList<TimeSheet>

    @GetMapping("/getOneSheet/{id}/{date}")
    fun getOneSheet(@PathVariable id:Int, @PathVariable date:String):TimeSheet?

    @GetMapping("/getOneEmpTask")
    fun getOneEmpTask(@RequestBody taskDto: TaskDto):Any

    @PutMapping("/updateTask")
    fun updateTask(@RequestBody updateTaskDto: UpdateTaskDto):Any

    @DeleteMapping("/deleteTask/{id}/{date}/{taskId}")
    fun deleteTask(@PathVariable id:Int, @PathVariable date:String, @PathVariable taskId:String):Any


}