package interpreter

import parser.BlockExp
import parser.Exp
import parser.IntLit
import parser.ReturnExp

object Interpreter {

    fun interpret(exp: Exp): Any? {
        when (exp) {
            is IntLit -> return exp.c
            is BlockExp -> {
                for (e in exp.exps) {
                    when (e) {
                        is ReturnExp -> return interpret(e.exp)
                        else -> throw NotImplementedError()
                    }
                }
                throw NotImplementedError()
            }
            else -> throw NotImplementedError()
        }
    }

}