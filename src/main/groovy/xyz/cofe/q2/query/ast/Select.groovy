package xyz.cofe.q2.query.ast

import xyz.cofe.q2.DataSource

import java.util.function.Function

/**
 * Операция select (по аналогии с SQL)
 */
class Select implements Expr {
    /** исходные данные */
    DataSource origin

    /**
     * Набор функция преобразования данных, подробно см.
     * {@link DataSource#select(java.util.Map)}
     */
    Map<String, Function> mapping

    /**
     * Источник данных
     * @return Источник данных
     */
    DataSource compile(){
        DataSource origin = this.origin
        if( origin==null )throw new IllegalStateException("origin not set")

        Map<String,Function> mapping = this.mapping
        if( mapping==null )throw new IllegalStateException("mapping not set")

        return origin.select(mapping)
    }
}
