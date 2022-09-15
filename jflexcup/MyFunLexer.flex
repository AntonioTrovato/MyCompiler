// .lex
//
// Description of lexer for  description language.
//
// Ian Stark

import java_cup.runtime.*;
import java.util.HashMap;

%%

%class MyFunLexer
%unicode
%cupsym sym
%cup
%line
%column

%{
    private Symbol symbol(int type) {
        return new Symbol(type, yyline, yycolumn);
    }
    private Symbol symbol(int type, Object value) {
        return new Symbol(type, yyline, yycolumn, value);
    }
%}
										// Declarations for JFlex
								// We wish to read text files							// Declare that we expect to use Java CUP

										// Abbreviations for regular expressions
WhiteSpace = [ \t\n\t\r]+
Integer = \d+
Real = ({Integer})(\.{Integer})?
Identifier = [$_A-Za-z][$_A-Za-z0-9]*
String = '[A-Za-z0-9/_.:,%&$()?!<>\\\-+= ]*' | \"[A-Za-z:0-9/_.:,%&$()?!<>\\\-+= ]*\"

%state BLOCK_COMMENT
%state LINE_COMMENT
%%

<YYINITIAL> {
    "main"      {return symbol(sym.MAIN);}
    "integer"   {return symbol(sym.INTEGER);}
    "string"    {return symbol(sym.STRING);}
    "real"      {return symbol(sym.REAL);}
    "bool"      {return symbol(sym.BOOL);}
    "("         {return symbol(sym.LPAR);}
    ")"         {return symbol(sym.RPAR);}
    ":"         {return symbol(sym.COLON);}
    "Fun"       {return symbol(sym.FUN);}
    "end"       {return symbol(sym.END);}
    "if"        {return symbol(sym.IF);}
    "then"      {return symbol(sym.THEN);}
    "else"      {return symbol(sym.ELSE);}
    "while"     {return symbol(sym.WHILE);}
    "loop"      {return symbol(sym.LOOP);}
    "%"         {return symbol(sym.READ);}
    "?"         {return symbol(sym.WRITE);}
    "?."        {return symbol(sym.WRITELN);}
    "?,"        {return symbol(sym.WRITEB);}
    "?:"        {return symbol(sym.WRITET);}
    ":="        {return symbol(sym.ASSIGN);}
    "+"         {return symbol(sym.PLUS);}
    "-"         {return symbol(sym.MINUS);}
    "*"         {return symbol(sym.TIMES);}
    "div"       {return symbol(sym.DIVINT);}
    "/"         {return symbol(sym.DIV);}
    "^"         {return symbol(sym.POW);}
    "&"         {return symbol(sym.STR_CONCAT);}
    "="         {return symbol(sym.EQ);}
    "<>"        {return symbol(sym.NE);}
    "!="        {return symbol(sym.NE);}
    "<"         {return symbol(sym.LT);}
    "<="        {return symbol(sym.LE);}
    ">"         {return symbol(sym.GT);}
    ">="        {return symbol(sym.GE);}
    "and"       {return symbol(sym.AND);}
    "or"        {return symbol(sym.OR);}
    "not"       {return symbol(sym.NOT);}
    "true"      {return symbol(sym.TRUE);}
    "false"     {return symbol(sym.FALSE);}
    ";"         {return symbol(sym.SEMI);}
    ","         {return symbol(sym.COMMA);}
    "return"    {return symbol(sym.RETURN);}
    "@"         {return symbol(sym.OUTPAR);}
    "var"       {return symbol(sym.VAR);}
    "out"       {return symbol(sym.OUT);}

    /* identifiers */
    {Identifier} { return symbol(sym.ID, yytext()); }

    /* literals */
    {Integer} { return symbol(sym.INTEGER_CONST, Integer.parseInt(yytext())); }

    {Real} { return symbol(sym.REAL_CONST, Double.parseDouble(yytext())); }

    {String} { return symbol(sym.STRING_CONST, yytext()); }


    /* whitespace */
    {WhiteSpace} { /* ignore */ }

    "#*"            { yybegin(BLOCK_COMMENT); }
    "#" | "//"      { yybegin(LINE_COMMENT); }

    [^]			{ return symbol(sym.error);}

    <<EOF>>             {return symbol(sym.EOF);}
}

<BLOCK_COMMENT> {
    #                                  { yybegin(YYINITIAL); }
    [^#]                               { /* ignore */ }

    <<EOF>>                            { throw new Error("Il commento non è stato chiuso"); }
}

<LINE_COMMENT> {
    \r | \n | \r\n                   { yybegin(YYINITIAL); }
    [^]                                { /* ignore */ }
}