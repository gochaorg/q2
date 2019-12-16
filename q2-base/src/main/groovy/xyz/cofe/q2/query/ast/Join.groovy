package xyz.cofe.q2.query.ast

import groovy.transform.ToString

@ToString(includeNames = true,includePackage = false, excludes = ['nodes','tree','source','link','joinSource'])
class Join implements ASTNode {
    ASTNode source
    ASTNode joinSource
    ASTNode link
    @Override List<ASTNode> getNodes() { [source,joinSource,link] }
}
