package lexer

sealed class Token

sealed class TypeModifier : Token()
object Val : TypeModifier()
object Var : TypeModifier()

data class Identifier(val value: String) : Token()

sealed class Operator : Token()
object Equals : Operator()


sealed class Literal : Token()

data class IntLit(val value: String) : Literal()