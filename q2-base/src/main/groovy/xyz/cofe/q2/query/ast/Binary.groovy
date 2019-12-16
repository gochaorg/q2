package xyz.cofe.q2.query.ast

import groovy.transform.ToString

/**
 * Бинарное выражение
 */
@ToString(includePackage = false, excludes = ['nodes','left','right','tree'],includeNames = true)
class Binary implements ASTNode {
    /**
     * Левая часть
     */
    ASTNode left

    /**
     * Правая часть
     */
    ASTNode right

    /**
     * Оператор
     */
    OpToken operator

    List<ASTNode> getNodes(){ [left, right] }
}
