package xyz.cofe.q2.query.ast

import groovy.transform.ToString

/**
 * Константа
 */
@ToString(includeNames = true,includePackage = false, excludes = ['nodes','tree'])
class Literal implements ASTNode {
    /**
     * Значение константы
     */
    def value
    List<ASTNode> getNodes(){ [] }
}
