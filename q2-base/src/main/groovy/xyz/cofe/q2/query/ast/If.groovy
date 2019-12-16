package xyz.cofe.q2.query.ast

import groovy.transform.ToString

@ToString(includeNames = true,includePackage = false, excludes = ['nodes','consequent','alternate','test','tree'])
class If implements ASTNode {
    ASTNode test
    ASTNode consequent
    ASTNode alternate

    @Override
    List<ASTNode> getNodes() {
        def lst = []
        if( test!=null )lst.add test
        if( consequent!=null )lst.add consequent
        if( alternate!=null )lst.add alternate
        return lst
    }
}
