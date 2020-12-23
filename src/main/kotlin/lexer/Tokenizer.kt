package lexer

sealed class Match
data class RegexMatch(val value: String, val getToken: (String) -> Token) : Match()
data class StringMatch(val value: String, val token: Token) : Match()

object Tokenizer {

    private val tokenList = mapOf<String, (String) -> Token>(
        "val" to { Val },
        "\\S+" to { str -> Identifier(str) },
        "=" to { Equals },
        "\\d+" to { str -> IntLit(str) }
    )

    fun run(input: String): List<Token> {
        val runningInput = StringBuilder(input)
        val tokens = mutableListOf<Token>()

        tokenList.forEach {
            when (it) {
                is StringMatch -> {
                    if (runningInput.regionMatches(0, it.value, 0, it.value.length)) {
                        tokens.add(it.token)
                        runningInput.deleteRange(0, it.value.length)
                    }
                }
                is RegexMatch -> {
                    for (i in runningInput.indices) {
                        if (it.value.toRegex().matches(runningInput.take(i))) {
                            tokens.add(it.getToken(runningInput.take(i).toString()))
                            runningInput.removeRange(0, i)
                        }
                    }
                }
            }
        }

        return tokens
    }
}