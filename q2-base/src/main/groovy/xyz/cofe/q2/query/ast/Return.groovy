package xyz.cofe.q2.query.ast

import groovy.transform.ToString

@ToString(includeNames = true,includePackage = false, excludes = ['nodes','arg','tree'])
class Return implements ASTNode {
    ASTNode arg

    @Override
    List<ASTNode> getNodes() { [arg] }
}
