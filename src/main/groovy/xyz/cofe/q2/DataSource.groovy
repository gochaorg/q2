package xyz.cofe.q2

import xyz.cofe.q2.meta.Column

import java.util.function.Consumer
import java.util.function.Predicate
import xyz.cofe.q2.it.It

/** Набор данных */
class DataSource<T> {
    /**
     * Набор данных
     */
    @SuppressWarnings("GrFinalVariableAccess")
    private final Iterable<T> values

    /**
     * Описание набора данных
     */
    @SuppressWarnings("GrFinalVariableAccess")
    private final List<Column> columns

    /**
     * Набор данных
     * @param columns описание колонок данных
     * @param values сами данные
     */
    DataSource( List<Column> columns, Iterable<T> values ){
        if( columns == null ) throw new IllegalArgumentException("columns==null");
        this.columns = columns

        if( values!=null ){
            this.values = values
        }else{
            this.values = []
        }
    }

    /**
     * Набор данных
     * @param dataType описание колонок данных
     * @param values сами данные
     */
    DataSource( Class dataType, Iterable<T> values ){
        if( dataType == null ) throw new IllegalArgumentException("dataType==null");
        this.columns = Column.columnsOf(dataType)

        if( values!=null ){
            this.values = values
        }else{
            this.values = []
        }
    }

    /** Извлечение данных */
    void fetch( Consumer<T> dataConsumer ){
        if( dataConsumer==null )throw new IllegalArgumentException("dataConsumer == null")
        values.each { dataConsumer.accept(it) }
    }

    /**
     * Фильтрация данных
     * @param filer фильтр
     * @return Источник данных
     */
    DataSource<T> where( Predicate<T> filter ){
        if( filter==null )return this
        new DataSource<T>( columns, It.filter(values,filter) )
    }
}
