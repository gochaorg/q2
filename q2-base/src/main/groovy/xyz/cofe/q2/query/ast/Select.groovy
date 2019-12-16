package xyz.cofe.q2.query.ast

class Select implements ASTNode {
    ASTNode source
    Map<String,ASTNode> mapping
}
