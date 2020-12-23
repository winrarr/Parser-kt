package lexer

object Lexer {
    fun run(input: String): List<Token> {
        return Tokenizer.run(input)
    }
}