package com.tower.resourceserver.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("private")
class PrivateController {
    @GetMapping("/api1")
    fun secure(): String  {
        return "return private api1"
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/admin")
    fun admin(): String  {
        return "return admin"
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('admin','user')")
    fun user(): String  {
        return "return user"
    }
}

