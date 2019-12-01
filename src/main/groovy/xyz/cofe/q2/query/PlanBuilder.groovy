package xyz.cofe.q2.query


import xyz.cofe.q2.meta.MetaData
import xyz.cofe.q2.query.ast.Expr
import xyz.cofe.q2.query.ast.From
import xyz.cofe.q2.query.ast.Join
import xyz.cofe.q2.query.ast.Where

import java.util.function.BiPredicate
import java.util.function.Predicate

/**
 * Выполнение запроса
 */
class PlanBuilder {
    /**
     * Описание данных
     */
    final MetaData meta

    /** Конструктор */
    PlanBuilder( MetaData meta ){
        if( meta == null ) throw new IllegalArgumentException("meta == null");
        this.meta = meta
    }

    /**
     * Создает запрос к даннным
     * @param query тело запроса
     */
    Expr build( Map query ){
        if( query == null ) throw new IllegalArgumentException("query==null");

        if( query.type=='From' && query.name instanceof String ){
            return new From(meta: meta, name: query.name )
        }

        if( query.type=='Where' && query.dataSource instanceof Map ){
            Expr ds = build(query.dataSource as Map)
            Where where = new Where(origin: ds.compile())
            if( query.filter?.ast?.tree && query.filter?.ast?.parser == 'esprima' ){
                Closure clFilter = new EsPrimaCompiler().compileFilter(query.filter.ast.tree)
                where.filter = clFilter as Predicate
            }
            return where
        }

        if( query.type=='Join' ){
            if( query.source==null )throw new Error("undefined `source` of join operation")
            if( query.join==null )throw new Error("undefined `join` of join opertation")

            Expr sourceDatasource = build(query.source as Map)
            Expr joinDatasource = build(query.join as Map)

            Join joinOp = new Join()
            joinOp.origin = sourceDatasource.compile()
            joinOp.join = joinDatasource.compile()

            if( query.filter?.ast?.tree && query.filter?.ast?.parser == 'esprima' ){
                Closure clFilter = new EsPrimaCompiler().compileFilter(query.filter.ast.tree)
                joinOp.filter = clFilter as BiPredicate
            }

            return joinOp
        }

        throw new Error("undefined query.type=$query.type")
    }
}
