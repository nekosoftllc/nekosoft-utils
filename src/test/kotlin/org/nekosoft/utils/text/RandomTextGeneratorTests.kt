package org.nekosoft.utils.text

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.random.Random

class RandomTextGeneratorTests {

    @Test
    fun `length is as required`() {
        val reqLen = Random.nextInt(5, 100)
        val str = generateRandomText(length = reqLen)
        assertEquals(reqLen, str.length)
    }

    @Test
    fun `characters are within the given alphabet`() {
        val alphabets = arrayOf(
            "123456abcdef", "ABDFEJqity.,/.", "!\"#&)([]{}mnbv",
            "ABCDEFabcdef0123456789", "01abd", "01234567",
        )
        val gvnAlf = Random.nextInt(alphabets.size)
        val str = generateRandomText(charPool = alphabets[gvnAlf].toList())
        assertThat(str.toList()).allMatch { it in alphabets[gvnAlf].toList() }
    }

    @Test
    fun `characters are within extremely small alphabet`() {
        val alphabet = listOf('0','1')
        val str = generateRandomText(charPool = alphabet)
        assertThat(str.toList()).allMatch { it in alphabet }
    }

    @Test
    fun `one char alphabets are not allowed`() {
        assertThrows(IllegalArgumentException::class.java) {
            val alphabet = listOf('a')
            val str = generateRandomText(charPool = alphabet)
            println(str)
        }
    }

    @Test
    fun `generated texts are all different`() {
        val s1 = generateRandomText()
        val s2 = generateRandomText()
        assertThat(s1).isNotEqualTo(s2)
        val s3 = generateRandomText()
        assertThat(s3).isNotEqualTo(s1)
        assertThat(s3).isNotEqualTo(s2)
        val s4 = generateRandomText()
        assertThat(s4).isNotEqualTo(s1)
        assertThat(s4).isNotEqualTo(s2)
        assertThat(s4).isNotEqualTo(s3)
        val s5 = generateRandomText()
        assertThat(s5).isNotEqualTo(s1)
        assertThat(s5).isNotEqualTo(s2)
        assertThat(s5).isNotEqualTo(s3)
        assertThat(s5).isNotEqualTo(s4)
    }

}