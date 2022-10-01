package org.nekosoft.utils.text

// Inspired by https://www.baeldung.com/kotlin/random-alphanumeric-string

import kotlin.math.abs
import java.security.SecureRandom

private const val RANDOM_TEXT_LENGTH = 9
private const val RANDOM_TEXT_CHAR_POOL = "abcdefghijklmnopqrstuvwxyz@!%\$ABCDEFGHIJKLMNOPQRSTUWXYZ_-#0123456789&^?"

fun generateRandomText(
    length: Int = RANDOM_TEXT_LENGTH,
    charPool: List<Char> = RANDOM_TEXT_CHAR_POOL.toList(),
    edgesPool: List<Char>? = null,
): String {
    val poolSize = charPool.size
    if (poolSize < 2) {
        throw IllegalArgumentException("Char pool size must be at least 2")
    }
    val random = SecureRandom()
    val bytes = ByteArray(length)
    random.nextBytes(bytes)
    return if (edgesPool == null) {
        bytes
            .fold(StringBuilder(length)) { acc, e ->
                acc.append(charPool[abs(e % poolSize)])
            }.toString()
    } else {
        val edgesPoolSize = edgesPool.size
        if (edgesPoolSize < 2) {
            throw IllegalArgumentException("Char edges pool size must be at least 2")
        }
        val builder = StringBuilder(length)
        builder.append(edgesPool[abs(bytes.first() % edgesPoolSize)])
        bytes.drop(1).dropLast(1)
            .fold(builder) { acc, e ->
                acc.append(charPool[abs(e % poolSize)])
            }
        builder.append(edgesPool[abs(bytes.last() % edgesPoolSize)])
        builder.toString()
    }
}

fun tokenizeCommandLineString(args: String): Array<String> {
    val arguments = mutableListOf<String>()
    val buf = StringBuilder()
    fun addToken() {
        arguments.add(buf.toString());
        buf.clear()
    }
    var escape = '\u0000'
    var quote = '\u0000'
    var wasQuote = false
    for (ch in args) {
        if (escape != '\u0000') { // we're in an escape, always resolve it
            buf.append(ch) // TODO decide how to handle escapes properly here
            escape = '\u0000' // this terminates the escape
        } else if (ch == '\\') { // escapes are always parsed as such
            escape = ch
        } else if (quote != '\u0000') { // we're in a quote
            if (ch == quote) { // Specifying the same quote again terminates the quote
                quote = '\u0000'
                wasQuote = true
            } else {
                buf.append(ch)
            }
        } else { // outside of quotes and escapes
            when (ch) {
                ' ' -> // we can tokenize
                    if (wasQuote || buf.isNotEmpty()) {
                        addToken()
                        wasQuote = false;
                    }
                '\'', '"' -> // we can start quotes
                    quote = ch
                else ->
                    buf.append(ch)
            }
        }
    }
    if (escape != '\u0000') { // makes trailing escapes be appended (erroneous string, though, IMO)
        buf.append(escape)
    }
    if (wasQuote || buf.isNotEmpty()) { // add the final string
        addToken()
    }
    return arguments.toTypedArray()
}
