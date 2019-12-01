package xyz.cofe.q2.query.ast

import xyz.cofe.q2.DataSource
import xyz.cofe.q2.meta.MetaData
import xyz.cofe.q2.query.PlanError

/**
 * Выполнение чтения данных из источника
 */
class From implements Expr {
    /** Мета информация */
    MetaData meta

    /** Имя источника данных */
    String name

    /**
     * Источник данных
     * @return Источник данных
     */
    DataSource compile(){
        MetaData meta = this.meta
        if( meta == null ) throw new IllegalStateException("meta not defined")

        String dsName = this.name
        if( dsName == null ) throw new IllegalStateException("name not defined")

        def dsInfo = meta.dataSources[ dsName ]
        if( dsInfo == null ) throw new PlanError("data source = '$dsName' not found", this)

        return dsInfo.dataSource
    }
}
