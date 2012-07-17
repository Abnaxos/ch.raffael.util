grammar CEL;

options {
	output = AST;
	backtrack = true;
//	memoize = true;
//	k = 2;
}

tokens {
	IF		= 'if';
	FINALLY 	= 'finally';
	
	PRE		= '@pre';
	THROWN		= '@thrown';
	EQUALS		= '@equals';
	PARAM		= '@param';
	RESULT		= '@result';
	
	CONDITIONAL	= '?';
	
	LOGICAL_OR	= '||';
	LOGICAL_AND	= '&&';
	
	BITWISE_OR	= '|';
	BITWISE_XOR	= '^';
	BITWISE_AND	= '&';
	
	EQ		= '==';
	NE		= '!=';
	GT		= '>';
	GE		= '>=';
	LT		= '<';
	LE		= '<=';
	INSTANCEOF	= 'instanceof';
	
	ADD		= '+';
	SUB		= '-';
	MUL		= '*';
	DIV		= '/';
	MOD		= '%';
	
	LOGICAL_NOT	= '!';
	BITWISE_NOT	= '~';
	
	LEFT_SHIFT	= '<<';
	RIGHT_SHIFT	= '>>';
	URIGHT_SHIFT	= '>>>';

	PAREN_OPEN	= '(';
	PAREN_CLOSE	= ')';
	INDEX_OPEN	= '[';
	INDEX_CLOSE	= ']';
	
	DEREFERENCE	= '.';
		
	CONDITION;
	POS;
	NEG;
	CAST;
	CALL;
	INDEX;
}

@parser::header {
package ch.raffael.util.contracts.processor.expr;
}

@lexer::header {
package ch.raffael.util.contracts.processor.expr;
}

@members {
/*    private List<Problem> problems = new java.util.LinkedList<Problem>();
    public List<Problem> getProblems() { return problems; }
    @Override
    public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
        String msg = getErrorMessage(e, tokenNames);
        if ( !msg.isEmpty() && !Character.isUpperCase(msg.charAt(1)) )
            msg = Character.toUpperCase(msg.charAt(0))+msg.substring(1);
        problems.add(new Problem(e, msg));
    }*/
}

condition
	:	FINALLY?
		( IF '(' ifexpr=expression ')' )? expr=expression
		EOF
	-> ^(CONDITION FINALLY? ^(IF $ifexpr)? $expr)
	;

expression
	:	logicalOr ( CONDITIONAL^ expression ':'! expression )?
	;

logicalOr
	:	logicalAnd ( LOGICAL_OR^ logicalAnd )*
	;
logicalAnd
	:	bitwiseOr ( LOGICAL_AND^ bitwiseOr )*
	;
bitwiseOr
	:	bitwiseXor ( BITWISE_OR^ bitwiseXor )*
	;
bitwiseXor
	:	bitwiseAnd ( BITWISE_XOR^ bitwiseAnd )*
	;
bitwiseAnd
	:	equality ( BITWISE_AND^ equality )*
	;

equality:	(equality2|instanceOf) ( (EQ|NE)^ (equality2|instanceOf) )*
	;
equality2
	:	shift ( (GE|GT|LT|LE)^ shift)*
	;
instanceOf
	:	shift INSTANCEOF^ type
	;

shift	:	addition ( (LEFT_SHIFT|RIGHT_SHIFT|URIGHT_SHIFT)^ addition )*
	;

addition:	multiplication ( (ADD|SUB)^ multiplication )*
	;
multiplication
	:	unary ( (MUL|DIV|MOD)^ unary )*
	;

unary	: prefix|postfix; // don't understand why I had to do this, but oh, well ...
prefix	:
	(	ADD unary
		-> ^(POS unary)
	|	SUB unary
		-> ^(NEG unary)
	|	BITWISE_NOT unary
		-> ^(BITWISE_NOT unary)
	|	LOGICAL_NOT unary
		-> ^(LOGICAL_NOT unary)
	|	PAREN_OPEN type PAREN_CLOSE unary
		-> ^(CAST type unary)
	)
	;
postfix	:	factor postfixOp^*
	;
postfixOp
	:	
	(	DEREFERENCE ID -> ^(DEREFERENCE ID)
	|	PAREN_OPEN argList? PAREN_CLOSE -> ^(CALL argList?)
	|	INDEX_OPEN expression INDEX_CLOSE -> ^(INDEX expression)
	)
	;	
factor	:	(ID|INT|FLOAT|STRING|CHAR|function|(PAREN_OPEN! expression PAREN_CLOSE!));

function:	PRE^ PAREN_OPEN! expression PAREN_CLOSE!
	|	THROWN^ PAREN_OPEN! type? PAREN_CLOSE!
	|	PARAM^ PAREN_OPEN! ( ((ADD|SUB)? INT | ID) )? PAREN_CLOSE!
	|	RESULT^ PAREN_OPEN! PAREN_CLOSE!
	|	EQUALS^ PAREN_OPEN! expression ','! expression PAREN_CLOSE!
	;
		
argList	:	expression ( ','! expression )*
	;

type
	:	ID^ ( DEREFERENCE^ ID )* (INDEX_OPEN INDEX_CLOSE!)*
	;

ID	:	IDENT;
fragment IDENT  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

INT	:
	( '0'
	| ('1'..'9') DIGIT*
	| '0' OCT_DIGIT+
	| '0' 'x' HEX_DIGIT+
	) ('l'|'L')?
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

COMMENT
    :   '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;
STRING
	:	'\"' ( ESCAPE_SEQUENCE | ~('\"'|'\\') )* '\"'
	|	'\'' ( ESCAPE_SEQUENCE | ~('\''|'\\') )* '\''
	;

fragment ESCAPE_SEQUENCE
	:	'\\'
	(	'n'
	|	'r'
	|	't'
	|	'b'
	|	'f'
	|	'"'
	|	'\''
	|	'\\'
	|	'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
	|	OCT_DIGIT OCT_DIGIT OCT_DIGIT?
	)
	;

fragment DIGIT	: '0'..'9' ;
fragment OCT_DIGIT
	:	'0'..'7';
fragment HEX_DIGIT
	:	'0'..'9' | 'a'..'f' | 'A'..'F';

CHAR:  '\'' ( ESCAPE_SEQUENCE | ~'\'' ) '\'' ('c'|'C')
    ;

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;
