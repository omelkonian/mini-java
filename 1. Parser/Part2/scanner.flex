import java_cup.runtime.*;

%%

%class Scanner

%line
%column

%cup
%cupdebug

%{
    StringBuffer string = new StringBuffer();
    private Symbol symbol(int type) {
       return new Symbol(type, yyline, yycolumn);
    }
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}

LineTerminator  = \r|\n|\r\n
WhiteSpace      = {LineTerminator} | [ \t\f]

dec_int_lit     = 0 | [1-9][0-9]*

fstVarChar      = [a-zA-z]
innerVarChar    = {fstVarChar} | _ | [0-9]
identifier      = {fstVarChar}{innerVarChar}*  

def_start 		= \){WhiteSpace}*\{

%%

<YYINITIAL> {
/* operators */
 "+"      	{ return symbol(sym.PLUS); }
 "-"      	{ return symbol(sym.MINUS); }
 "*"      	{ return symbol(sym.TIMES); }
 "/"      	{ return symbol(sym.DIV); }
 "%"      	{ return symbol(sym.MOD); }
 "}"      	{ return symbol(sym.RBRACKET); }
 "("      	{ return symbol(sym.LPAREN); }
 ")"      	{ return symbol(sym.RPAREN); }
 ","      	{ return symbol(sym.COMMA); }
 "=="     	{ return symbol(sym.EQ); }
 "!="     	{ return symbol(sym.NEQ); }
 ">="     	{ return symbol(sym.GEQ); }
 "<="     	{ return symbol(sym.LEQ); }
 ">"     	{ return symbol(sym.GR); }
 "<"     	{ return symbol(sym.LE); }
 "if"     	{ return symbol(sym.IF); }
 "else"   	{ return symbol(sym.ELSE); }
}

{def_start}				{ return symbol(sym.RPAREN_LBRACKET); }

{dec_int_lit} 			{ return symbol(sym.NUMBER, new Integer(yytext())); }
{identifier} 			{ return symbol(sym.IDENTIFIER, new String(yytext())); }
{WhiteSpace} 			{ /* just skip what was found, do nothing */ }

/* No token was found for the input so through an error.  Print out an
   Illegal character message with the illegal character that was found. */
[^]                    { throw new Error("Illegal character <"+yytext()+">"); }
