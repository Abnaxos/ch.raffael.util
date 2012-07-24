grammar CEL;

options {
	output = AST;
	backtrack = true;
//	memoize = true;
//	k = 2;
}

tokens {
	IF		= 'if';
	FOR		= 'for';
	FINALLY 	= 'finally';
	TRUE		= 'true';
	FALSE		= 'false';
	NULL		= 'null';
	CLASS		= 'class';
	
	TINT		= 'int';
	TLONG		= 'long';
	TSHORT		= 'short';
	TBYTE		= 'byte';
	TBOOLEAN	= 'boolean';
	TFLOAT		= 'float';
	TDOUBLE		= 'double';
	TCHAR		= 'char';
	TVOID		= 'void';
	
	THIS		= 'this';
	SUPER		= 'super';
	
	PRE		= '@pre';
	THROWN		= '@thrown';
	EQUALS		= '@equals';
	PARAM		= '@param';
	RESULT		= '@result';
	EACH		= '@each';
	
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
	COLON		= ':';
	CLOSURE		= '->';
	
	CONDITION;
	POS;
	NEG;
	CAST;
	CALL;
	INDEX;
	ARRAY;
}

@parser::header {
package ch.raffael.util.contracts.processor.expr;
}

@lexer::header {
package ch.raffael.util.contracts.processor.expr;
}

condition
	:	FINALLY?
		fullExpression
		EOF
	-> ^(CONDITION FINALLY? fullExpression)
	;

/*ifExpression
	:	(IF PAREN_OPEN! expression PAREN_CLOSE!)* expression
	;*/

fullExpression
	:	ifExpression* expression
	;

ifExpression
	:	IF^ PAREN_OPEN! expression PAREN_CLOSE!
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
	:	shift INSTANCEOF^ typeref
	;

shift	:	addition ( (LEFT_SHIFT|RIGHT_SHIFT|URIGHT_SHIFT)^ addition )*
	;

addition:	multiplication ( (ADD|SUB)^ multiplication )*
	;
multiplication
	:	unary ( (MUL|DIV|MOD)^ unary )*
	;
	
unary	:	add=ADD unary -> ^(POS[$add] unary)
	|	sub=SUB unary -> ^(NEG[$sub] unary)
	|	unary2
	;
unary2	:	BITWISE_NOT^ unary
	|	LOGICAL_NOT^ unary
	|	cast
	|	factor selector^*
	;
cast	:	paren=PAREN_OPEN primitiveType PAREN_CLOSE unary -> ^(CAST[$paren] primitiveType unary)
	|	paren=PAREN_OPEN typeref PAREN_CLOSE unary2 -> ^(CAST[$paren] typeref unary2)
	;

selector:	DEREFERENCE member=ID -> ^(DEREFERENCE[$member])
	|	DEREFERENCE method=ID PAREN_OPEN argList? PAREN_CLOSE -> ^(CALL[$method] argList*)
	|	index=INDEX_OPEN expression INDEX_CLOSE -> ^(INDEX[$index] expression)
	;
	
factor	:
	(	ID|INT|FLOAT|STRING|CHAR
	|	TRUE|FALSE|NULL|THIS|SUPER
	|	( typeref | primitiveType | TVOID ) DEREFERENCE! CLASS^
	|	call
	|	function
	|	(PAREN_OPEN! expression PAREN_CLOSE!));
call	:	method=ID PAREN_OPEN argList? PAREN_CLOSE -> ^(CALL[$method] argList* THIS);

function:	PRE^ PAREN_OPEN! expression PAREN_CLOSE!
	|	THROWN^ PAREN_OPEN! classref? PAREN_CLOSE!
	|	PARAM^ PAREN_OPEN! ( ((ADD|SUB)? INT | ID) )? PAREN_CLOSE!
	|	RESULT^ PAREN_OPEN! PAREN_CLOSE!
	|	EQUALS^ PAREN_OPEN! expression ','! expression PAREN_CLOSE!
	|	EACH^ PAREN_OPEN! ID COLON! expression CLOSURE! fullExpression PAREN_CLOSE!
	;
		
argList	:	expression ( ','! expression )*
	;

typeref	:	classref array^*
	|	primitiveType array^+
	;
classref:	ID classDereference^*;
classDereference
	:	DEREFERENCE pkgOrCls=ID -> ^(DEREFERENCE[$pkgOrCls]);
typerefFragment
	:	ID
	;
array	:	arr=INDEX_OPEN INDEX_CLOSE -> ^(ARRAY[$arr]);
primitiveType
	:	TINT | TLONG | TSHORT | TBYTE | TDOUBLE | TFLOAT | TCHAR | TBOOLEAN
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
    :
    ( ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ) ('d'|'D'|'f'|'F')?
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
