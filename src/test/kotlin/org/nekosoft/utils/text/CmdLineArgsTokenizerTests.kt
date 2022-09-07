package org.nekosoft.utils.text

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test

class CmdLineArgsTokenizerTests {
    @Test
    fun `tokenize one parameter`() {
        val args = tokenizeCommandLineString("param")
        assertArrayEquals(arrayOf("param"), args)
    }

    @Test
    fun `tokenize quoted parameter`() {
        val args = tokenizeCommandLineString("--try \"param me one\" pos")
        assertArrayEquals(arrayOf("--try","param me one","pos"), args)
    }

    @Test
    fun `tokenize quoted parameter at the end`() {
        val args = tokenizeCommandLineString("--try value \"1 2 3 \"")
        assertArrayEquals(arrayOf("--try","value","1 2 3 "), args)
    }

    @Test
    fun `tokenize unclosed quoted parameter at the end`() {
        val args = tokenizeCommandLineString("--try value \"1 2 3")
        assertArrayEquals(arrayOf("--try","value","1 2 3"), args)
    }

    @Test
    fun `tokenize escaped quote`() {
        val args = tokenizeCommandLineString("\"1 2\\\" 3\\\"\"")
        assertArrayEquals(arrayOf("1 2\" 3\""), args)
    }

    @Test
    fun `tokenize escaped escape`() {
        val args = tokenizeCommandLineString("grep \\\\123 done")
        assertArrayEquals(arrayOf("grep", "\\123","done"), args)
    }

    @Test
    fun `tokenize escaped anything`() {
        val args = tokenizeCommandLineString("grep \\$ done")
        assertArrayEquals(arrayOf("grep","$","done"), args)
    }

    @Test
    fun `tokenize trailing escape`() {
        val args = tokenizeCommandLineString("grep \\")
        assertArrayEquals(arrayOf("grep","\\"), args)
    }

    @Test
    fun `do not tokenize trailing space`() {
        val args = tokenizeCommandLineString("grep ")
        assertArrayEquals(arrayOf("grep"), args)
    }

    @Test
    fun `tokenize quoted trailing space`() {
        val args = tokenizeCommandLineString("\"grep \"")
        assertArrayEquals(arrayOf("grep "), args)
    }
}
