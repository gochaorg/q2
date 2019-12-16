package xyz.cofe.q2.query.ast

import xyz.cofe.q2.it.TreeStep

class ASTNodeIterator implements Iterator<TreeStep<ASTNode>> {
    private final List<TreeStep<ASTNode>> workset = []

    ASTNodeIterator(ASTNode root){
        if (root == null) throw new IllegalArgumentException("root==null");
        workset.add new TreeStep<ASTNode>(root)
    }

    @Override
    synchronized boolean hasNext() {
        return !workset.empty
    }

    @Override
    synchronized TreeStep<ASTNode> next() {
        TreeStep step = workset.remove(0 as int)
        workset.addAll( 0, step.node.nodes.collect { new TreeStep<>(it, step) } )
        return step
    }
}
