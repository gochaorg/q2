package xyz.cofe.q2.query.ast

import groovy.transform.ToString

@ToString(includeNames = true,includePackage = false, excludes = ['nodes','tree','source','filter'])
class Where implements ASTNode {
    ASTNode source
    ASTNode filter
    @Override List<ASTNode> getNodes() { [source,filter] }
}
