package xyz.cofe.q2.query.ast

import groovy.transform.ToString

/**
 * Унарное выражение
 */
@ToString(includeNames = true,includePackage = false, excludes = ['nodes','arg','tree'])
class Unary implements ASTNode {
    ASTNode arg
    OpToken operator
    List<ASTNode> getNodes(){ [arg] }
}
