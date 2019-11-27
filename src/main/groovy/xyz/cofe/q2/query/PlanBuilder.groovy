package xyz.cofe.q2.query

import groovy.transform.TypeChecked
import xyz.cofe.q2.DataSource
import xyz.cofe.q2.meta.MetaData
import xyz.cofe.q2.meta.Column

import java.util.function.BiConsumer
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

        throw new Error("undefined query.type=$query.type")
    }
}
