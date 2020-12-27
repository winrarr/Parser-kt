import interpreter.Interpreter
import lexer.Lexer
import parser.Parser

class Driver {
    fun run() {
        try {
            val input = javaClass.getResource("sample.txt").readText()

            val tokens = Lexer(input).getTokens()
            println(tokens)
            val ast = Parser(tokens).parse()
            println(ast)
            println(Interpreter.interpret(ast))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}