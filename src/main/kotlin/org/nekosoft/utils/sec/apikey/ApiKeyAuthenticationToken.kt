package org.nekosoft.utils.sec.apikey

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class ApiKeyAuthenticationToken(
    private val apiKey: String,
    private val username: String? = null,
    authorities: HashSet<GrantedAuthority>? = null
) : AbstractAuthenticationToken(authorities) {

    init {
        isAuthenticated = false
    }

    override fun getCredentials(): String = apiKey

    override fun getPrincipal(): String? = username

}
