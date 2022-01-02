package com.mangerservice.dao

import com.mangerservice.modal.ManagerAuth
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository



interface IManagerDao: MongoRepository<ManagerAuth, ObjectId> {
    fun findByUsername(userName:String):ManagerAuth?
    fun existsByUsername(userName:String):Boolean
    /*@Query(value = "{'_id' : ?0}", fields = "{'password' : 0}")
    fun getUsernameById(id:Int):String*/
}