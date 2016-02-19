grammar Query;

WHITESPACE : ( ' ' )+ -> skip;

STRING : ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'*'|'-'|'.')+;

QUOTED_STRING : '"' ( 'a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-'|'.'|':'|' '|'#'|','|'*'|'/'|'$'|'!'|'?'|'%')+ '"';

FOLLOW : '|';
REVERSE : '%';

AND : '&&';
OR : '||';

COLON : ':';

ontElem : STRING;

rootClass : STRING;

literal : QUOTED_STRING | STRING;

followOP : FOLLOW ontElem;

reverseOP : REVERSE ontElem;

exp : rootClass (followOP | reverseOP)* COLON literal;

andExp: AND? exp;
orExp: OR exp;

expQuery : exp (andExp | orExp)*;