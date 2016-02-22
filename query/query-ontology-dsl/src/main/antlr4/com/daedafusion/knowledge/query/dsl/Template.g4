grammar Template;

WHITESPACE : ( ' ' )+ -> skip;

VAR_OP : '${';
VAR_ED : '}';

OR : '||';

//PROP_SUB : ('a'..'z'|'A'..'Z'|'0'..'9'|'.'|'-'|'_')+;
LIT : ('a'..'z'|'A'..'Z'|'0'..'9'|'.'|'-'|'_'|':'|'/')+;

FUNC_UUID : '@uuid()';
FUNC_DATE : '@date()';
FUNC_TIME : '@time()';
FUNC_DATETIME : '@dateTime()';
funcHash : '@hash(' expression ')';

literal : LIT;

function : FUNC_UUID | FUNC_DATE | FUNC_TIME | FUNC_DATETIME | funcHash;

orExp : OR LIT;

substitution : LIT (orExp)*;
//substitution : PROP_SUB;

variable : VAR_OP ( function | substitution ) VAR_ED;

expression : (literal+ variable* literal*)+ | (literal* variable+ literal*)+;