package xyz.cofe.q2.query


import xyz.cofe.q2.meta.MetaData

import java.util.function.BiPredicate
import java.util.function.Function
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
    DsExpr build(Map query ){
        if( query == null ) throw new IllegalArgumentException("query==null");

        if( query.type=='From' && query.name instanceof String ){
            return new From(meta: meta, name: query.name )
        }

        if( query.type=='Where' && query.dataSource instanceof Map ){
            DsExpr ds = build(query.dataSource as Map)
            Where where = new Where(origin: ds.compile())
            if( query.filter?.ast?.tree && query.filter?.ast?.parser == 'esprima' ){
                Closure clFilter = new EsPrimaCompiler().compile(query.filter.ast.tree)
                where.filter = clFilter as Predicate
            }
            return where
        }

        if( query.type=='Join' ){
            if( query.source==null )throw new Error("undefined `source` of join operation")
            if( query.join==null )throw new Error("undefined `join` of join opertation")

            DsExpr sourceDatasource = build(query.source as Map)
            DsExpr joinDatasource = build(query.join as Map)

            Join joinOp = new Join()
            joinOp.origin = sourceDatasource.compile()
            joinOp.join = joinDatasource.compile()

            if( query.filter?.ast?.tree && query.filter?.ast?.parser == 'esprima' ){
                Closure clFilter = new EsPrimaCompiler().compile(query.filter.ast.tree)
                joinOp.filter = clFilter as BiPredicate
            }

            return joinOp
        }

        if( query.type=='Select' ){
            if( query.source == null ) throw new Error("undefined `source` of Select operation")
            if( query.mapping == null ) throw new Error("undefined `mapping` of Select opertation")

            EsPrimaCompiler compiler = new EsPrimaCompiler()
            Map<String, Function> mapping = [:]
            query.mapping.each { key, mapDef ->
                if( !(key instanceof String) )return
                if( !(mapDef instanceof Map) )return

                if( mapDef.ast?.tree && mapDef.ast?.parser == 'esprima' ){
                    Closure mapCl = compiler.compile(mapDef.ast.tree)
                    mapping[key] = mapCl as Function
                }else{
                    throw new Error("can't compute map fn for $key column")
                }
            }

            DsExpr origin = build(query.source as Map)

            Select selectOp = new Select()
            selectOp.origin = origin.compile()
            selectOp.mapping = mapping

            return selectOp
        }

        throw new Error("undefined query.type=$query.type")
    }
}
