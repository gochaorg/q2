package xyz.cofe.q2.query

import xyz.cofe.q2.DataSource

import java.util.function.Predicate

/**
 * Фильтрование данных
 */
class Where implements Expr {
    /** Фильтр данных */
    Predicate filter

    /** исходные данные */
    DataSource origin

    /** Получение источника данных */
    DataSource compile(){
        DataSource origin = this.origin
        if( origin==null )throw new IllegalStateException("origin not set")

        Predicate f = this.filter
        if( f==null )return origin

        return origin.where( f )
    }
}
