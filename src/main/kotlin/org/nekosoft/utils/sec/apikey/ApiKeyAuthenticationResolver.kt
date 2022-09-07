package org.nekosoft.utils.sec.apikey

import org.springframework.security.core.Authentication

interface ApiKeyAuthenticationResolver {
    fun resolveApiKey(apiKey: String): Authentication?
}
