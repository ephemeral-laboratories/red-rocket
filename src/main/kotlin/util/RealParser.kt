package garden.ephemeral.rocket.util

import org.antlr.v4.kotlinruntime.ANTLRErrorListener
import org.antlr.v4.kotlinruntime.BailErrorStrategy
import org.antlr.v4.kotlinruntime.BaseErrorListener
import org.antlr.v4.kotlinruntime.BufferedTokenStream
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.ParserRuleContext
import org.antlr.v4.kotlinruntime.RecognitionException
import org.antlr.v4.kotlinruntime.Recognizer
import org.antlr.v4.kotlinruntime.Token
import kotlin.math.PI
import kotlin.math.sqrt

class RealParser {
    companion object {
        private const val doubleTermRegex = "(?:\\d+(?:\\.\\d+)?)"
        private const val realTermRegex = "(?:-?(?:√?(?:${doubleTermRegex}π?|π)|∞))"
        const val realRegex = "(?:$realTermRegex(?:\\s*\\/\\s*$realTermRegex)?)"

        fun realFromString(input: String): Double {
            val errorListener: ANTLRErrorListener = BailErrorListener(input)
            val lexer = RealExpressionLexer(CharStreams.fromString(input))
            lexer.addErrorListener(errorListener)
            val tokenStream = BufferedTokenStream(lexer)
            val parser = RealExpressionParser(tokenStream)
            parser.addErrorListener(errorListener)
            parser.errorHandler = BailErrorStrategy()

            return convert(input, parser.expression())
        }

        private fun convert(input: String, context: ParserRuleContext): Double {
            // Many `!!` within to work around the generated code declaring things as nullable when they are not
            // https://github.com/Strumenta/antlr-kotlin/issues/84
            return when (context) {
                is RealExpressionParser.ExpressionContext -> convert(input, context.atom!!)
                is RealExpressionParser.ParenthesizedExpressionContext -> convert(input, context.nested!!)
                is RealExpressionParser.RationalExpressionContext ->
                    if (context.atom != null) {
                        convert(input, context.atom!!)
                    } else {
                        convert(input, context.numerator!!) / convert(input, context.denominator!!)
                    }
                is RealExpressionParser.SquareRootExpressionContext ->
                    if (context.atom != null) {
                        convert(input, context.atom!!)
                    } else {
                        sqrt(convert(input, context.nested ?: context.nestedInParentheses!!))
                    }
                is RealExpressionParser.MinusExpressionContext ->
                    if (context.atom != null) {
                        convert(input, context.atom!!)
                    } else {
                        -convert(input, context.nested!!)
                    }
                is RealExpressionParser.NPiExpressionContext ->
                    if (context.atom != null) {
                        convert(input, context.atom!!)
                    } else {
                        convert(input, context.n!!) * PI
                    }
                is RealExpressionParser.NumberContext -> convert(input, context.nested!!)
                else -> throw UnsupportedNodeException(input, context)
            }
        }

        private fun convert(input: String, token: Token): Double {
            return when (token.type) {
                RealExpressionLexer.Tokens.INTEGER.id -> token.text!!.toInt().toDouble()
                RealExpressionLexer.Tokens.FLOAT.id -> token.text!!.toDouble()
                RealExpressionLexer.Tokens.PI.id -> PI
                RealExpressionLexer.Tokens.INFINITY.id -> Double.POSITIVE_INFINITY
                else -> throw UnsupportedTokenException(input, token)
            }
        }
    }

    open class RealParserException(input: String, message: String, cause: Throwable? = null) :
        UnsupportedOperationException("$message, input: <$input>", cause)

    class UnsupportedNodeException(input: String, context: ParserRuleContext) :
        RealParserException(input, "Unsupported node: <${context.text}> (${context.javaClass})")

    class UnsupportedTokenException(input: String, token: Token) :
        RealParserException(input, "Unsupported token: <${token.text}> (${token.type})")

    class InvalidSyntaxException(
        input: String,
        line: Int,
        charPositionInLine: Int,
        message: String,
        cause: Throwable?
    ) : RealParserException(input, "Syntax error: line $line:$charPositionInLine $message", cause)

    private class BailErrorListener(private val input: String) : BaseErrorListener() {
        // Want to rename `msg` to `message` but causes a warning because name does not match superclass
        // https://github.com/Strumenta/antlr-kotlin/issues/83
        override fun syntaxError(
            recognizer: Recognizer<*, *>,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String,
            e: RecognitionException?
        ) {
            throw InvalidSyntaxException(input, line, charPositionInLine, msg, e)
        }
    }
}
