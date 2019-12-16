package xyz.cofe.q2.query.ast

/**
 * Операторы
 */
enum OpToken {
    Divide('/'),
    Multiply('*'),
    Plus('+'),
    Minus('-'),
    Equals('==', '==='),
    NotEquals('!=','<>','!=='),
    Less('<'),
    More('>'),
    EqualsOrLess('<='),
    EqualsOrMore('>='),
    Or('|', '||'),
    And('&', '&&'),
    Not('!'),
    In('in'),
    Pow('**'),
    Mod('%'),
    LeftShift('<<'),
    RightShift('>>'),
    RRightShift('>>>'),
    ;

    OpToken(String ... strings1){
        this.strings = strings1;
    }

    /**
     * Текстовое представление оператора
     */
    final String[] strings;
}
