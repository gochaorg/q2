package xyz.cofe.q2.query.ast

import groovy.transform.ToString

/**
 * "Замыкание" (стрелочаня функция)
 */
@ToString(includeNames = true,includePackage = false, excludes = ['body','nodes','tree'])
class ClosureExp implements ASTNode {
    /**
     * Аргументы
     */
    List<Identifier> arguments = []

    /**
     * Тело функции
     */
    ASTNode body

    List<ASTNode> getNodes(){ [body] }
}
