package xyz.cofe.q2.query.ast

import xyz.cofe.q2.it.TreeIterBuilder
import xyz.cofe.q2.it.TreeIterBuilderDefault
import xyz.cofe.q2.it.TreeStep

import java.util.function.Function

/**
 * Некое ast выражение
 */
trait ASTNode {
    List<ASTNode> getNodes() { [] }

    void visit(Closure fn){
        List<TreeStep<ASTNode>> workset = [ new TreeStep<ASTNode>(this) ]
        while (!workset.empty){
            TreeStep step = workset.remove(0 as int)
            fn( step )
            workset.addAll( 0, step.node.nodes.collect { new TreeStep<>(it, step) } )
        }
    }

    Iterable<TreeStep<ASTNode>> getTree(){
        return [ iterator : { new ASTNodeIterator(this) } ] as Iterable
    }
}
