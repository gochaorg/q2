package xyz.cofe.q2.query

import xyz.cofe.q2.DataSource
import xyz.cofe.q2.meta.MetaData

/**
 * Выполнение чтения данных из источника
 */
class From implements DsExpr {
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
