package org.nekosoft.utils.sec.apikey

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*

class ApiKeyProviderTests {
    @Test
    fun `do nothing with no API key and no resolver`() {
        val provider = ApiKeyAuthenticationProvider(
            authenticationResolver = Optional.empty(),
            serverApiKey = null,
        )
        val token = provider.authenticate(ApiKeyAuthenticationToken("anyapikey"))
        assertNull(token)
    }

    @Test
    fun `exception if server api key does not match`() {
        val provider = ApiKeyAuthenticationProvider(
            authenticationResolver = Optional.empty(),
            serverApiKey = "theapikey",
        )
        assertThrows<BadCredentialsException> {
            provider.authenticate(ApiKeyAuthenticationToken("anyapikey"))
        }
    }

    @Test
    fun `valid token if server api key matches`() {
        val provider = ApiKeyAuthenticationProvider(
            authenticationResolver = Optional.empty(),
            serverApiKey = "theapikey",
        )
        val token = provider.authenticate(ApiKeyAuthenticationToken("theapikey"))
        assertNotNull(token)
        assertTrue(token!!.isAuthenticated)
    }

    @Test
    fun `exception if resolver returns null`() {
        val resolver = object : ApiKeyAuthenticationResolver {
            override fun resolveApiKey(apiKey: String): Authentication? =
                null
        }
        val provider = ApiKeyAuthenticationProvider(
            authenticationResolver = Optional.of(resolver),
            serverApiKey = null,
        )
        assertThrows<BadCredentialsException> {
            provider.authenticate(ApiKeyAuthenticationToken("anyapikey"))
        }
    }

    @Test
    fun `valid token if resolver returns token`() {
        val tk = UsernamePasswordAuthenticationToken("user", "apikey", listOf(SimpleGrantedAuthority("ROLE_API")))
        val resolver = object : ApiKeyAuthenticationResolver {
            override fun resolveApiKey(apiKey: String): Authentication =
                tk
        }
        val provider = ApiKeyAuthenticationProvider(
            authenticationResolver = Optional.of(resolver),
            serverApiKey = null,
        )
        val token = provider.authenticate(ApiKeyAuthenticationToken("theapikey"))
        assertNotNull(token)
        assertTrue(token!!.isAuthenticated)
        assertEquals(tk, token)
    }

    @Test
    fun `resolver wins over api key`() {
        val tk = UsernamePasswordAuthenticationToken("user", "apikey", listOf(SimpleGrantedAuthority("ROLE_API")))
        val resolver = object : ApiKeyAuthenticationResolver {
            override fun resolveApiKey(apiKey: String): Authentication =
                tk
        }
        val provider = ApiKeyAuthenticationProvider(
            authenticationResolver = Optional.of(resolver),
            serverApiKey = "theapikey",
        )
        val token = provider.authenticate(ApiKeyAuthenticationToken("theapikey"))
        assertNotNull(token)
        assertTrue(token!!.isAuthenticated)
        assertEquals(tk, token)
    }

}
