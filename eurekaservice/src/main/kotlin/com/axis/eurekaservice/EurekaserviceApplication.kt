package com.axis.eurekaservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@EnableEurekaServer
@SpringBootApplication
class EurekaserviceApplication

fun main(args: Array<String>) {
	runApplication<EurekaserviceApplication>(*args)
}
