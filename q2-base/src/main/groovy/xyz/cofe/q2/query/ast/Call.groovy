package xyz.cofe.q2.query.ast

import groovy.transform.ToString

@ToString(includeNames = true,includePackage = false, excludes = ['nodes','arguments','tree'])
class Call implements ASTNode {
    ASTNode callee
    List<ASTNode> arguments

    @Override
    List<ASTNode> getNodes() {arguments}
}
