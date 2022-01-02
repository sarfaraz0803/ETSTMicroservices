package com.employeeservice.dao

import com.employeeservice.modal.LogTime
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ILogTimeDetailsDao : MongoRepository<LogTime, ObjectId> {
}