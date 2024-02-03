package com.tower.resourceserver.config

import com.tower.resourceserver.service.KeycloakJwtTokenConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import java.util.*

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {
    private var keycloakJwtTokenConverter: KeycloakJwtTokenConverter? =
        KeycloakJwtTokenConverter(JwtGrantedAuthoritiesConverter())

    @Bean
    @Throws(Exception::class)
    fun myServerFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests { authorizeHttpRequests ->
            authorizeHttpRequests
                .requestMatchers("/private/**")
                .authenticated()
                // others are public
                .requestMatchers("/**")
                    .permitAll()

        }
        http.oauth2ResourceServer {
            oauth2: OAuth2ResourceServerConfigurer<HttpSecurity?> ->
            oauth2.jwt(
                Customizer { jwt ->
                    jwt.jwtAuthenticationConverter(
                        keycloakJwtTokenConverter // use custom converter
                    )
                })
        }

        return http.build()
    }
}