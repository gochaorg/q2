package xyz.cofe.q2.query.ast

import groovy.transform.ToString

@ToString(includeNames = true,includePackage = false, excludes = ['nodes','tree'])
class Identifier implements ASTNode {
    String name
    List<ASTNode> getNodes(){ [] }
}
