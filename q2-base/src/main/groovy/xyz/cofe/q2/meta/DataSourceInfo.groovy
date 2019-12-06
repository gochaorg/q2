package xyz.cofe.q2.meta

import xyz.cofe.q2.DataSource

import java.lang.reflect.Field

/**
 * Корневой объект источника данных
 */
class DataSourceInfo {
    /** Корневой объект */
    Object root

    /** Поле корневого объекта содержащие данные */
    Field rootField

    /** Имя источника данных */
    String name

    /** Источник данных */
    DataSource dataSource

    /** Тип объекта в источнике данных */
    Class dataType

    /** Описание "колонок" объекта */
    @Lazy List<Column> columns = {
        if( dataType==null )return []
        Column.columnsOf( dataType )
    }()
}
