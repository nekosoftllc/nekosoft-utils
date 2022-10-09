package org.nekosoft.utils.sec.apikey

import org.springframework.security.core.Authentication

interface ApiKeyAuthenticationResolver {

    /**
     * Validates the given API key
     * @return An authenticated instance of [Authentication], or `null` if the
     * given API key was not valid
     */
    fun resolveApiKey(apiKey: String): Authentication?
}
