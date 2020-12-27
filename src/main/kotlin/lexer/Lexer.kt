package lexer

import lexer.TokenType.*


class Lexer(private val source: String) {
    private val keywords = mapOf(
        "val"       to VAL,
        "and"       to AND,
        "class"     to CLASS,
        "else"      to ELSE,
        "false"     to FALSE,
        "for"       to FOR,
        "fun"       to FUN,
        "if"        to IF,
        "nil"       to NIL,
        "or"        to OR,
        "print"     to PRINT,
        "return"    to RETURN,
        "super"     to SUPER,
        "this"      to THIS,
        "true"      to TRUE,
        "var"       to VAR,
        "while"     to WHILE,
    )

    private var start = 0
    private var current = 0
    private val tokens = mutableListOf(Token(LEFT_BRACE, null))
    private var line = 1

    fun getTokens(): List<Token> {
        while (!isAtEnd()) {
            start = current
            scanNextToken()
        }
        return tokens + Token(NEWLINE, null) + Token(RIGHT_BRACE, null)
    }

    private fun scanNextToken() {
        when (val c = advance()) {
            '(' -> addToken(LEFT_PAREN)
            ')' -> addToken(RIGHT_PAREN)
            '{' -> addToken(LEFT_BRACE)
            '}' -> addToken(RIGHT_BRACE)
            ',' -> addToken(COMMA)
            '.' -> addToken(DOT)
            '-' -> addToken(MINUS)
            '+' -> addToken(PLUS)
            ';' -> addToken(SEMICOLON)
            '*' -> addToken(STAR)
            '!' -> addToken(if (match('=')) BANG_EQUAL else BANG)
            '=' -> addToken(if (match('=')) EQUAL_EQUAL else EQUAL)
            '<' -> addToken(if (match('=')) LESS_EQUAL else LESS)
            '>' -> addToken(if (match('=')) GREATER_EQUAL else GREATER)
            '/' -> if (match('/')) {
                while (peek() != '\n' && !isAtEnd()) advance()
            } else {
                addToken(SLASH)
            }
            ' ', '\r', '\t' -> { }
            '\n' -> {
                line++
                addToken(NEWLINE)
            }
            '"' -> string()
            else -> when {
                c.isDigit() -> number()
                c.isJavaIdentifierStart() -> identifier()
                else -> throw UnexpectedCharacterException(c)
            }
        }
    }

    private fun number() {
        while (!isAtEnd() && peek().isDigit()) advance()

        if (peek() == '.' && peekNext().isDigit()) {
            advance()
            while (peek().isDigit()) advance()
            addToken(DOUBLE, source.substring(start, current).toDouble())
            return
        }
        addToken(INTEGER, source.substring(start, current).toInt())
    }

    private fun identifier() {
        while (!isAtEnd() && peek().isJavaIdentifierPart()) advance()

        val str = source.substring(start, current)
        val keyword = keywords[str]
        if (keyword != null) {
            addToken(keyword)
        } else {
            addToken(IDENTIFIER, str)
        }
    }

    private fun string() {
        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\n') line++
            advance()
        }
        if (isAtEnd()) throw UnterminatedStringException(start)

        advance()
        addToken(STRING, source.substring(start + 1, current - 1))
    }

    private fun peek(): Char {
        return if (isAtEnd()) '\u0000' else source[current]
    }

    private fun peekNext(): Char {
        return if (current + 1 >= source.length) '\u0000' else source[current + 1]
    }

    private fun advance(): Char {
        current++
        return source[current - 1]
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd() || source[current] != expected) return false
        current++
        return true
    }

    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun addToken(type: TokenType) {
        addToken(type, null)
    }

    private fun addToken(type: TokenType, value: Any?) {
        tokens.add(Token(type, value))
    }

    class UnterminatedStringException(position: Int) : Exception("... starting at position $position")
    class UnexpectedCharacterException(char: Char) : Exception(char.toString())
}