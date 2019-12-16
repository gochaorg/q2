package xyz.cofe.q2.query.ast

class AstBuilder {
    ASTNode parse( node ){
        if( node==null )throw new IllegalArgumentException("node == null")
        switch (node.type){
            case 'From': return new From(name: node.name)
            case 'Where': return new Where(source: parse(node.dataSource), filter: expression(node.filter))
            case 'Join': return new Join(source: parse(node.source), joinSource: parse(node.join), link: expression(node.filter))
            case 'Select': return new Select(source: parse(node.source),mapping: node.collectEntries { k,v ->
                [(k):(expression(v))]
            })
        }
    }

    ASTNode expression( node ){
        if( node==null )throw new IllegalArgumentException("node == null")
        if( node.ast?.tree && node.ast?.parser == 'esprima' ){
            return new EsPrimaAst().parse(node.ast.tree)
        }
    }
}
