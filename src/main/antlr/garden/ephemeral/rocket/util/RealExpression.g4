
grammar RealExpression;

@header {
package garden.ephemeral.rocket.util;
}

start: expression;

parenthesizedExpression
    : OPEN_PARENTHESIS nested=expression CLOSE_PARENTHESIS
    ;

expression: atom=rationalExpression;

rationalExpression
    : atom=minusExpression
    | numerator=minusExpression SLASH denominator=minusExpression
    ;

minusExpression
    : atom=squareRootExpression
    | MINUS nested=squareRootExpression
    ;

squareRootExpression
    : atom=nPiExpression
    | RADICAL
      ( nested=nPiExpression
      | nestedInParentheses=parenthesizedExpression
      )
    ;

nPiExpression
    : atom=number
    | n=number PI
    ;

number: nested=(INTEGER | FLOAT | PI | INFINITY);

OPEN_PARENTHESIS: '(';
CLOSE_PARENTHESIS: ')';
SLASH: '/';
RADICAL: '√';
PI: 'π';
INFINITY: '∞';
MINUS: '-';
INTEGER: Digits;
FLOAT: Digits '.' Digits;

fragment Digits: [0-9]+;

WS: ' '+ -> skip;
