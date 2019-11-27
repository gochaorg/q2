package xyz.cofe.q2.meta

import xyz.cofe.q2.DataSource
import xyz.cofe.q2.RootData

import java.lang.reflect.Field

/**
 * Описание модели данных
 */
class MetaData {
    /**
     * Кореневой объект источников данных
     */
    final def rootData

    /**
     * Конструктор
     * @param rootData кореневой объект источников данных
     */
    MetaData(def rootData){
        this.rootData = rootData
    }

    /** Поля корневого объекта содержащие данные */
    @Lazy List<Field> rootDataFields = {
        if( rootData==null )return []

        rootData.getClass().getDeclaredFields().findAll {
            DataSource.isAssignableFrom( it.type )
        }
    }()

    /**
     * Источники данных
     */
    @Lazy Map<String,DataSourceInfo> dataSources = {
        def map = [:]
        if( rootData!=null ){
            rootDataFields.collect { field ->
                def gt = field.getGenericType()
                if( gt instanceof java.lang.reflect.ParameterizedType ){
                    def pt = gt as java.lang.reflect.ParameterizedType
                    def tparams = pt.actualTypeArguments
                    if( tparams.length == 1 ) return [
                        name : field.name,
                        type : tparams[ 0 ],
                        field: field
                    ]
                }
                return null
            }.findAll {
                it != null
            }.each {
                map[ it.name ] = new DataSourceInfo(
                    name: it.name,
                    dataType: it.type,
                    rootField: it.field,
                    dataSource: rootData[ it.name ]
                )
            }
        }
        return map
    }()
}
