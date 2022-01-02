package com.mangerservice.service

import com.mangerservice.dtos.ManagerAuthDto
import com.mangerservice.modal.ManagerAuth

interface IManagerService {

    fun registerManager(managerAuth: ManagerAuth):ManagerAuth
    fun loginManager(managerAuthDto: ManagerAuthDto): ManagerAuth?
//    fun registerEmployee(empCre: EmpCre): EmpCre

    // Employee operation
   /* fun addEmployee()
    fun viewByEmpId()
    fun viewAllEmployee()
    fun updateEmployee()
    fun deleteEmployee()*/
}