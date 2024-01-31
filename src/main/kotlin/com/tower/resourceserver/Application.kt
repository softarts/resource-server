package com.tower.resourceserver

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
//@EnableJpaRepositories
class Application


fun main(args:Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
