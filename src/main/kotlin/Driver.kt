import lexer.Lexer

class Driver {
    fun run() {
        try {
            val input =javaClass.getResource("sample.txt").readText()
            println(input.split(" "))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}