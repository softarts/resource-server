package com.tower.resourceserver.service

import org.springframework.core.convert.converter.Converter
import org.springframework.lang.NonNull
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

const val RESOURCE_ID = "account"
const val PRINCIPAL_ATTR = "preferred_username"

class KeycloakJwtTokenConverter(
    private val jwtGrantedAuthoritiesConverter: JwtGrantedAuthoritiesConverter,
) : Converter<Jwt, JwtAuthenticationToken> {

    override fun convert(@NonNull jwt: Jwt): JwtAuthenticationToken {
        val accesses = Optional.of<Jwt>(jwt)
            .map { token: Jwt -> token.getClaimAsMap(KEYCLOAK_RESOURCE_ACCESS) } // return Map<String, Any>
            .map { claimMap: Map<String, Any> -> claimMap[RESOURCE_ID] as Map<String?, Any?>? } // return Map<String?, Any?>?
            .map { resourceData: Map<String?, Any?>? -> resourceData!![KEYCLOAK_ROLES] as Collection<String?>? } // return  Collection<String?>?
            // it is arrayListOf("manage-account","manage-account-links","view-profile")
            .stream().flatMap { x->x?.stream() } // flatMap the child:ArrayList<String>
            .map { role ->
                SimpleGrantedAuthority(
                    KEYCLOAK_ROLE_PREFIX + role
                )
            }

        // extract realm_access as well
        val realm = Optional.of<Jwt>(jwt)
            .map<Map<String?, Any?>> { token: Jwt -> token.getClaimAsMap( KEYCLOAK_REALM_ACCESS) as Map<String?, Any?>?}
            .map<Collection<String?>?> { resourceData: Map<String?, Any?>? -> resourceData!![KEYCLOAK_ROLES] as Collection<String?>? } // arrayListOf("manage-account","manage-account-links","view-profile")
            .stream().flatMap { x->x.stream() }
            .map { role ->
                SimpleGrantedAuthority(
                    KEYCLOAK_ROLE_PREFIX + role
                )
            }

        // concat the 3 streams
        val authorities = Stream
            .concat(
                Stream.concat(jwtGrantedAuthoritiesConverter.convert(jwt).stream(), accesses),
                realm
            )
            .collect(Collectors.toSet())

        // an example

//        "sub" -> "dcbfb403-75cf-4388-8c6a-97d642964239"
//        "resource_access" -> {LinkedTreeMap@8450}  size = 1
//        "email_verified" -> {Boolean@8452} false
//        "iss" -> "http://localhost:8080/auth/realms/SpringBootKeycloak"
//        "typ" -> "Bearer"
//        "preferred_username" -> "user1"
//        "sid" -> "ff8bfcdd-1d91-4773-9c84-9b0a12dcd5a2"
//        "aud" -> {ArrayList@8462}  size = 1
//        "acr" -> "1"
//        "realm_access" -> {LinkedTreeMap@8466}  size = 1
//        "azp" -> "login-app"
//        "scope" -> "email profile"
//        "exp" -> {Instant@8426} "2024-01-28T15:04:17Z"
//        "session_state" -> "ff8bfcdd-1d91-4773-9c84-9b0a12dcd5a2"
//        "iat" -> {Instant@8425} "2024-01-28T14:59:17Z"
//        "jti" -> "1d73943b-cc38-4a8d-989f-f120caa59ce0"

        val principalClaimName: String = jwt.getClaimAsString(PRINCIPAL_ATTR) ?:jwt.getClaimAsString(JwtClaimNames.SUB)

        return JwtAuthenticationToken(jwt, authorities, principalClaimName)
    }

    companion object {
        private const val KEYCLOAK_REALM_ACCESS = "realm_access"
        private const val KEYCLOAK_RESOURCE_ACCESS = "resource_access"
        private const val KEYCLOAK_ROLES = "roles"
        private const val KEYCLOAK_ROLE_PREFIX = "ROLE_"
    }
}

