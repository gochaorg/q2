package xyz.cofe.q2.query.ast

import groovy.transform.ToString

@ToString(includeNames = true,includePackage = false, excludes = ['nodes','object','property','tree'])
class Member implements ASTNode {
    ASTNode object
    ASTNode property
    List<ASTNode> getNodes(){ [object,property] }
}
