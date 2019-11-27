package xyz.cofe.q2.meta

import java.lang.reflect.Field

/**
 * Описывает колонку данных
 */
class Column {
    /** имя */
    String name

    /** тип данных */
    Class type

    static def getGroovyFields(){ ['$staticClassInfo','__$stMC','metaClass','$staticClassInfo$','$callSiteArray'] }
    static def getExcludeFields(){ return getGroovyFields() }

    /** Создание списка колонок из описания объекта */
    static List<Column> columnsOf( Class dataType ){
        if( dataType == null ) throw new IllegalArgumentException("dataType==null");

        dataType.declaredFields.collect { Field fld ->
            if( fld.name in excludeFields ){
                return null
            }
            return new Column(name: fld.name, type: fld.type)
        }.findAll { it!=null }
    }
}
