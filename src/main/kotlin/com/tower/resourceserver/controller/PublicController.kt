package com.tower.resourceserver.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("public")
class PublicController {
    @GetMapping("/api1")
    fun api(): String  {
        return "public api1"
    }

}