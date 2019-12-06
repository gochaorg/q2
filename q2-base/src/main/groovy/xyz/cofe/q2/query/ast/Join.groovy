package xyz.cofe.q2.query.ast

import xyz.cofe.q2.DataSource

import java.util.function.BiPredicate
import java.util.function.Predicate

/**
 * Операция присоединения части данных
 */
class Join implements Expr {
    /** Фильтр данных */
    BiPredicate filter

    /** исходные данные */
    DataSource origin

    /** исходные данные */
    DataSource join

    /** Получение источника данных */
    DataSource compile(){
        DataSource origin = this.origin
        if( origin==null )throw new IllegalStateException("origin not set")

        DataSource join = this.join
        if( origin==null )throw new IllegalStateException("join not set")

        BiPredicate f = this.filter
        if( f==null ){
            return origin.join( join, {a,b -> true} as BiPredicate )
        }

        return origin.join( join, f )
    }
}
