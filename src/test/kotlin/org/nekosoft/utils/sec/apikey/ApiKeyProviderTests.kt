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
    fun `return null if no server API key and no resolver`() {
        val provider = ApiKeyAuthenticationProvider(
            authenticationResolver = Optional.empty(),
            serverApiKey = null,
        )
        val token = provider.authenticate(ApiKeyAuthenticationToken("anyapikey"))
        assertNull(token)
    }

    @Test
    fun `if resolver is present, it should be used even if no server API key is present`() {
        val tk = UsernamePasswordAuthenticationToken("user", "apikey", listOf(SimpleGrantedAuthority("ROLE_API")))
        val resolver = object : ApiKeyAuthenticationResolver {
            override fun resolveApiKey(apiKey: String): Authentication =
                tk
        }
        val provider = ApiKeyAuthenticationProvider(
            authenticationResolver = Optional.of(resolver),
            serverApiKey = null,
        )
        val token = provider.authenticate(ApiKeyAuthenticationToken("anyapikey"))
        assertEquals(tk, token)
    }

    @Test
    fun `exception if server api key does not match given key`() {
        val provider = ApiKeyAuthenticationProvider(
            authenticationResolver = Optional.empty(),
            serverApiKey = "theapikey",
        )
        assertThrows<BadCredentialsException> {
            provider.authenticate(ApiKeyAuthenticationToken("anyapikey"))
        }
    }

    @Test
    fun `valid token if server api key matches given key`() {
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

    @Test
    fun `provider does nothing with the wrong class`() {
        val tk = UsernamePasswordAuthenticationToken("user", "apikey", listOf(SimpleGrantedAuthority("ROLE_API")))
        val provider = ApiKeyAuthenticationProvider(
            serverApiKey = "theapikey",
            authenticationResolver = Optional.empty(),
        )
        val token = provider.authenticate(tk)
        assertNull(token)
    }

    @Test
    fun `supports method refuses the wrong class`() {
        val tk = UsernamePasswordAuthenticationToken("user", "apikey", listOf(SimpleGrantedAuthority("ROLE_API")))
        val provider = ApiKeyAuthenticationProvider(
            serverApiKey = "theapikey",
            authenticationResolver = Optional.empty(),
        )
        assertFalse(provider.supports(tk.javaClass))
    }

    @Test
    fun `supports method accepts the right class`() {
        val tk = ApiKeyAuthenticationToken("anapikey", "apiuser")
        val provider = ApiKeyAuthenticationProvider(
            serverApiKey = "theapikey",
            authenticationResolver = Optional.empty(),
        )
        assertTrue(provider.supports(tk.javaClass))
    }

}
