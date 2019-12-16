package xyz.cofe.q2.query.ast

import groovy.transform.ToString

@ToString(includeNames = true,includePackage = false, excludes = ['nodes','tree'])
class Block implements ASTNode {
    List<ASTNode> nodes
}
