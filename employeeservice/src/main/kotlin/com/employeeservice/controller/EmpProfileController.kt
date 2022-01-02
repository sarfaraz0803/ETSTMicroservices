package com.employeeservice.controller

import com.employeeservice.client.EmployeeClient
import com.employeeservice.dao.IJwtCreDao
import com.employeeservice.dtos.AccountResponseDto
import com.employeeservice.dtos.SetTaskStatusDto
import com.employeeservice.modal.*
import com.employeeservice.service.EmpCreServiceImpl
import com.employeeservice.jwtutils.JwtTokenValidation
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import java.net.http.HttpResponse
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.MultivaluedMap


@RestController
@RequestMapping("/employeeProfile")
@CrossOrigin
class EmpProfileController {

    @Autowired
    private lateinit var empCreServiceImpl: EmpCreServiceImpl
    @Autowired
    private lateinit var employeeClient: EmployeeClient
    @Autowired
    private lateinit var jwtTokenValidation: JwtTokenValidation
    @Autowired
    private lateinit var iJwtCreDao:IJwtCreDao
    private val passwordEncoder = BCryptPasswordEncoder()


    //==============================Employee_Credentials_Function================================

    @PostMapping("/login")
    fun empAuthentication(@RequestBody empCre: EmpCre, response: HttpServletResponse): ResponseEntity<Any>{
        val empLog = this.empCreServiceImpl.empLogIn(empCre)
        if(empLog != null){
            if(passwordEncoder.matches(empCre.password,empLog.password)){

                val token = jwtTokenValidation.generateToken(empLog)
                response.addHeader("UserId",empLog._id.toString())
                response.addHeader("Authorization", token)

                employeeClient.sendLoginTime(
                    empLog._id,
                    LocalDate.now().toString(),
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                )

                val account:Account = employeeClient.getAccountById(empLog._id)
                val accountResponse = AccountResponseDto(account,token)

                return ResponseEntity(accountResponse,HttpStatus.OK)
            }
            return ResponseEntity("Invalid Password",HttpStatus.OK)
        }else{
            return ResponseEntity("Profile Not Exist",HttpStatus.OK)
        }
    }

    /*@GetMapping("/loggedIn")
    fun loggedEmployee(request: HttpServletRequest):ResponseEntity<Any>{
        var userId = request.getHeader("UserId")
        var empToken = request.getHeader("Authorization")
        if(empToken != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, empToken)
                    if( result == true){
                        val account:Account = employeeClient.getAccountById(Integer.parseInt(userId))
                        return ResponseEntity(account,HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(empToken)
                    return ResponseEntity("Token Expire",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.OK)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Please provide token & userId",HttpStatus.OK)
    }*/

    @PostMapping("/logout")
    fun logOutEmployee(request: HttpServletRequest):ResponseEntity<Any> {
        var userId = request.getHeader("UserId")
        var empToken = request.getHeader("Authorization")
        if(empToken != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, empToken)
                    if( result == true){
                        employeeClient.sendLogOutTime(
                            Integer.parseInt(userId),
                            LocalDate.now().toString(),
                            LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                        )
                        iJwtCreDao.deleteById(userId)
                        return ResponseEntity("Successfully LoggedOut",HttpStatus.OK)
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(empToken)
                    return ResponseEntity("Token Expire",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.OK)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Please provide token & userId",HttpStatus.OK)
    }

    // Employee Can Update his Profile
    @PutMapping("/updateAccount")
    fun updateAccountById(request: HttpServletRequest, @RequestBody account: Account): ResponseEntity<Any> {
        var userId = request.getHeader("UserId")
        var empToken = request.getHeader("Authorization")
        if(empToken != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, empToken)
                    if( result == true){
                        empCreServiceImpl.update(Integer.parseInt(userId),account.username,account.password)
                        return ResponseEntity(employeeClient.updateAccountById(Integer.parseInt(userId),account), HttpStatus.OK) // update account through Client
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException){
                    jwtTokenValidation.deleteJwtCre(empToken)
                    return ResponseEntity("Token Expire!!! Please Login Again",HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.OK)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Please provide token & userId",HttpStatus.BAD_REQUEST)

    }

    @PutMapping("/updateTaskFields")
    fun updateTaskFields(request: HttpServletRequest, @RequestBody setTaskStatusDto: SetTaskStatusDto): ResponseEntity<Any> {
        var userId = request.getHeader("UserId")
        var empToken = request.getHeader("Authorization")
        if(empToken != null && userId != null){
            if(iJwtCreDao.existsById(userId)){
                try {
                    val result = jwtTokenValidation.validateUserToken(userId, empToken)
                    if( result == true){
                        return ResponseEntity(employeeClient.updateTaskFields(setTaskStatusDto), HttpStatus.OK) // update task Status through Client
                    }
                    return ResponseEntity("Credentials Not Matching",HttpStatus.OK)
                }catch (e:ExpiredJwtException) {
                    jwtTokenValidation.deleteJwtCre(empToken)
                    return ResponseEntity("Token Expire!!! Please Login Again", HttpStatus.OK)
                }catch (e:Exception){
                    return ResponseEntity(e.message,HttpStatus.OK)
                }
            }
            return ResponseEntity("Need To Login!! UserId Not Exist",HttpStatus.OK)
        }
        return ResponseEntity("Please provide token & userId",HttpStatus.BAD_REQUEST)

    }



    //=================These Functions are accessed by AccountMicroservice=================

    @PostMapping("/registerCre/{id}/{username}/{pass}")
    fun registerEmployee(@PathVariable id:Int, @PathVariable username:String, @PathVariable pass:String): ResponseEntity<Any>{
        empCreServiceImpl.registerEmployee(id,username,pass)
        return ResponseEntity(HttpStatus.OK)
    }

    @GetMapping("/checkEmp/{id}/{username}")
    fun employeeCheck(@PathVariable id:Int,@PathVariable username:String):ResponseEntity<Boolean>{
        return ResponseEntity(empCreServiceImpl.employeeCheck(id,username),HttpStatus.OK)
    }

    @DeleteMapping("/deleteEmpCre/{id}")
    fun deleteEmployeeCredentials(@PathVariable id:Int):ResponseEntity<Any>{
        return ResponseEntity(empCreServiceImpl.deleteEmpCre(id),HttpStatus.OK)
    }

    @PutMapping("/updateEmpCre/{id}/{username}/{pass}")
    fun updateEmpCre(@PathVariable id:Int, @PathVariable username:String, @PathVariable pass:String):ResponseEntity<Any>{
        empCreServiceImpl.update(id,username,pass)
        return ResponseEntity(HttpStatus.OK)
    }
}