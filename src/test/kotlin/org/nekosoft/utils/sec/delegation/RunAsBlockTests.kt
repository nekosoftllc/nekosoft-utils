package org.nekosoft.utils.sec.delegation

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

class RunAsBlockTests {

    @Test
    fun `correctly adding authorities with null authentication`() {
        SecurityContextHolder.getContext().authentication = null
        RunAs.anonymousWithRoles("ROLE_role1").use {
            assertThat(SecurityContextHolder.getContext().authentication).isNotNull
            assertThat(SecurityContextHolder.getContext().authentication).isInstanceOf((AnonymousAuthenticationToken::class.java))
            assertThat(SecurityContextHolder.getContext().authentication.authorities).contains(SimpleGrantedAuthority("ROLE_role1"))
        }
        assertThat(SecurityContextHolder.getContext().authentication).isNull()
    }

    @Test
    fun `correctly adds role prefix if not there`() {
        SecurityContextHolder.getContext().authentication = AnonymousAuthenticationToken(key, "username", listOf(SimpleGrantedAuthority("ROLE_nothing")))
        RunAs.anonymousWithRoles("role1").use {
            assertThat(SecurityContextHolder.getContext().authentication).isNotNull
            assertThat(SecurityContextHolder.getContext().authentication).isInstanceOf((AnonymousAuthenticationToken::class.java))
            assertThat(SecurityContextHolder.getContext().authentication.authorities).contains(SimpleGrantedAuthority("ROLE_role1"))
        }
    }

    @Test
    fun `correctly adds more than one role`() {
        SecurityContextHolder.getContext().authentication = AnonymousAuthenticationToken(key, "username", listOf(SimpleGrantedAuthority("ROLE_nothing")))
        RunAs.anonymousWithRoles("ROLE_role1", "ROLE_role2", "ROLE_role3").use {
            assertThat(SecurityContextHolder.getContext().authentication).isNotNull
            assertThat(SecurityContextHolder.getContext().authentication).isInstanceOf((AnonymousAuthenticationToken::class.java))
            assertThat(SecurityContextHolder.getContext().authentication.authorities).containsAll(listOf(SimpleGrantedAuthority("ROLE_role1"), SimpleGrantedAuthority("ROLE_role2"), SimpleGrantedAuthority("ROLE_role3")))
        }
    }

    @Test
    fun `includes the current roles of a user`() {
        SecurityContextHolder.getContext().authentication = AnonymousAuthenticationToken(key, "username", listOf(SimpleGrantedAuthority("ROLE_nothing")))
        RunAs.anonymousWithRoles("ROLE_role1").use {
            assertThat(SecurityContextHolder.getContext().authentication).isNotNull
            assertThat(SecurityContextHolder.getContext().authentication).isInstanceOf((AnonymousAuthenticationToken::class.java))
            assertThat(SecurityContextHolder.getContext().authentication.authorities).contains(SimpleGrantedAuthority("ROLE_nothing"))
        }
    }

    @Test
    fun `does not do anything if user is required and it's null`() {
        SecurityContextHolder.getContext().authentication = null
        RunAs.userWithRoles("ROLE_role1").use {
            assertThat(SecurityContextHolder.getContext().authentication).isNull()
        }
    }

    @Test
    fun `does not do anything if user is required and it's anonymous`() {
        val origToken = AnonymousAuthenticationToken(key, "username", listOf(SimpleGrantedAuthority("ROLE_nothing")))
        SecurityContextHolder.getContext().authentication = origToken
        RunAs.userWithRoles("ROLE_role1").use {
            assertThat(SecurityContextHolder.getContext().authentication).isSameAs(origToken)
        }
    }

    @Test
    fun `does not do anything if user is required and it's unauthenticated`() {
        val origToken = UsernamePasswordAuthenticationToken(key, "username")
        SecurityContextHolder.getContext().authentication = origToken
        RunAs.userWithRoles("ROLE_role1").use {
            assertThat(SecurityContextHolder.getContext().authentication).isSameAs(origToken)
        }
    }

    @Test
    fun `does change token if user is not required and it's null`() {
        SecurityContextHolder.getContext().authentication = null
        RunAs.anonymousWithRoles("ROLE_role1").use {
            assertThat(SecurityContextHolder.getContext().authentication).isNotNull
            assertThat(SecurityContextHolder.getContext().authentication.authorities).contains(SimpleGrantedAuthority("ROLE_role1"))
        }
    }

    @Test
    fun `does change token if user is not required and it's anonymous`() {
        val origToken = AnonymousAuthenticationToken(key, "username", listOf(SimpleGrantedAuthority("ROLE_nothing")))
        SecurityContextHolder.getContext().authentication = origToken
        RunAs.anonymousWithRoles("ROLE_role1").use {
            assertThat(SecurityContextHolder.getContext().authentication).isNotSameAs(origToken)
            assertThat(SecurityContextHolder.getContext().authentication.authorities).contains(SimpleGrantedAuthority("ROLE_role1"))
        }
    }

    @Test
    fun `does change token if user is not required and it's unauthenticated`() {
        val origToken = UsernamePasswordAuthenticationToken(key, "username")
        SecurityContextHolder.getContext().authentication = origToken
        RunAs.anonymousWithRoles("ROLE_role1").use {
            assertThat(SecurityContextHolder.getContext().authentication).isNotSameAs(origToken)
            assertThat(SecurityContextHolder.getContext().authentication.authorities).contains(SimpleGrantedAuthority("ROLE_role1"))
        }
    }

    companion object {
        private val key = "test-nekosoft-runas"
        init {
            RunAs.anonymousKey = key
        }
    }
}