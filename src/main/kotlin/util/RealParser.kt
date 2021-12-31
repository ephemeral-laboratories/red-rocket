package garden.ephemeral.rocket.util

import org.antlr.v4.runtime.BailErrorStrategy
import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.Token
import kotlin.math.PI
import kotlin.math.sqrt

class RealParser {
    companion object {
        private const val doubleTermRegex = "(?:\\d+(?:\\.\\d+)?)"
        private const val realTermRegex = "(?:-?(?:√?(?:${doubleTermRegex}π?|π)|∞))"
        const val realRegex = "(?:$realTermRegex(?:\\s*\\/\\s*$realTermRegex)?)"

        fun realFromString(string: String): Double {

            val lexer = RealExpressionLexer(CharStreams.fromString(string))
            val tokenStream = BufferedTokenStream(lexer)
            val parser = RealExpressionParser(tokenStream)
            parser.errorHandler = BailErrorStrategy()

            return convert(parser.expression())
        }

        private fun convert(context: ParserRuleContext): Double {
            return when (context) {
                is RealExpressionParser.ExpressionContext -> convert(context.atom)
                is RealExpressionParser.ParenthesizedExpressionContext -> convert(context.nested)
                is RealExpressionParser.RationalExpressionContext ->
                    if (context.atom != null) {
                        convert(context.atom)
                    } else {
                        convert(context.numerator) / convert(context.denominator)
                    }
                is RealExpressionParser.SquareRootExpressionContext ->
                    if (context.atom != null) {
                        convert(context.atom)
                    } else {
                        sqrt(convert(context.nested ?: context.nestedInParentheses))
                    }
                is RealExpressionParser.MinusExpressionContext ->
                    if (context.atom != null) {
                        convert(context.atom)
                    } else {
                        -convert(context.nested)
                    }
                is RealExpressionParser.NPiExpressionContext ->
                    if (context.atom != null) {
                        convert(context.atom)
                    } else {
                        convert(context.n) * PI
                    }
                is RealExpressionParser.NumberContext -> convert(context.nested)
                else -> throw UnsupportedNodeException(context)
            }
        }

        private fun convert(token: Token): Double {
            return when (token.type) {
                RealExpressionLexer.INTEGER -> token.text.toInt().toDouble()
                RealExpressionLexer.FLOAT -> token.text.toDouble()
                RealExpressionLexer.PI -> PI
                RealExpressionLexer.INFINITY -> Double.POSITIVE_INFINITY
                else -> throw UnsupportedTokenException(token)
            }
        }

        private fun realTermFromString(string: String): Double {
            var s = string
            var negative = false
            var root = false
            if (s.startsWith("-")) {
                s = s.substring(1)
                negative = true
            }
            if (s.startsWith("√")) {
                s = s.substring(1)
                root = true
            }

            var n = when {
                s == "π" -> Math.PI
                s.endsWith("π") -> s.substring(0, s.length - 1).toDouble() * Math.PI
                s == "∞" -> Double.POSITIVE_INFINITY
                else -> s.toDouble()
            }

            if (root) {
                n = sqrt(n)
            }
            if (negative) {
                n = -n
            }
            return n
        }
    }

    class UnsupportedNodeException(val context: ParserRuleContext) :
        UnsupportedOperationException("Unsupported node: <${context.text}> (${context.javaClass})")

    class UnsupportedTokenException(val token: Token) :
        UnsupportedOperationException("Unsupported token: <${token.text}> (${token.type})")
}
