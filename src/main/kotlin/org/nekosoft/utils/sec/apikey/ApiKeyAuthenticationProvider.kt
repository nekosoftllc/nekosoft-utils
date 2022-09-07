package org.nekosoft.utils.sec.apikey

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*
import kotlin.collections.HashSet

class ApiKeyAuthenticationProvider(
    private val authenticationResolver: Optional<ApiKeyAuthenticationResolver>,
    private val serverApiKey: String? = null,
    private val serverApiRole: String = "ROLE_API",
    private val serverApiUsername: String = "apiuser",
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication? {
        if ((serverApiKey.isNullOrBlank() && authenticationResolver.isEmpty) || authentication !is ApiKeyAuthenticationToken) {
            return null
        }
        val authenticated = if (authenticationResolver.isPresent) {
            authenticationResolver.get().resolveApiKey(authentication.credentials)
        } else if (authentication.credentials == serverApiKey) {
            val authorities = HashSet<GrantedAuthority>()
            authorities.add(SimpleGrantedAuthority(serverApiRole))
            val authenticated = ApiKeyAuthenticationToken(authentication.credentials, serverApiUsername, authorities)
            authenticated.isAuthenticated = true
            authenticated
        } else {
            null
        }
        return authenticated ?: throw BadCredentialsException("Invalid API Key")
    }

    override fun supports(authentication: Class<*>) =
        authentication == ApiKeyAuthenticationToken::class.java

}