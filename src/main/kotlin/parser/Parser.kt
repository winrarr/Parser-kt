package parser

import lexer.Token
import lexer.TokenType
import lexer.TokenType.*

class Parser(private val tokens: List<Token>) {

    private var current = 0

    fun parse(): BlockExp {
        return parseBlock()
    }

    private fun parseBlock(): BlockExp {
        advance()
        val vals = mutableListOf<ValDecl>()
        val vars = mutableListOf<VarDecl>()
        val defs = mutableListOf<DefDecl>()
        val classes = mutableListOf<ClassDecl>()
        val exps = mutableListOf<Exp>()
        while (true) {
            when (val token = advance().tokenType) {
                RIGHT_BRACE -> break
                VAL -> vals.add(parseVal())
                RETURN -> exps.add(ReturnExp(parseExp()))
                else -> exps.add(parseExp())
            }
        }
        return BlockExp(vals, vars, defs, classes, exps)
    }

    private fun parseVal(): ValDecl {
        val idToken = advance()
        advance()
        val id = when (idToken.tokenType) {
            IDENTIFIER -> idToken.data as String
            else -> throw UnexpectedTokenException(idToken)
        }
        return ValDecl(id, null, parseExp())
    }

    private fun parseExp(): Exp {
        val token = advance()
        val tokenType = token.tokenType

        return when {
            tokenType == INTEGER && match(NEWLINE) -> IntLit(token.data as Int)
            tokenType == STRING && match(NEWLINE) -> StringLit(token.data as String)
            tokenType == TRUE && match(NEWLINE) -> BoolLit(true)
            tokenType == FALSE && match(NEWLINE) -> BoolLit(false)
            tokenType == IDENTIFIER && match(NEWLINE) -> VarExp(token.data as String)
            else -> throw NotImplementedError("parseExp: $tokenType")
        }
    }

    private fun match(expected: TokenType): Boolean {
        if (isAtEnd() || tokens[current].tokenType != expected) return false
        current++
        return true
    }

    private fun advance(): Token {
        current++
        return tokens[current - 1]
    }

    private fun peek(): Token = tokens[current + 1]

    private fun isAtEnd(): Boolean {
        return current >= tokens.size
    }

    class UnexpectedTokenException(token: Token) : Exception(token.toString())
}