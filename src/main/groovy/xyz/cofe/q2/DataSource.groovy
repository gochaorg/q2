package xyz.cofe.q2

import java.util.function.Consumer
import java.util.function.Predicate

/** Набор данных */
class DataSource<T> {
    private final Iterable<T> values

    /** Набор данных */
    DataSource( Iterable<T> values ){
        if( values instanceof List ){
            this.values = values
        }else if( values!=null ){
            this.values = values.toList()
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
    DataSource<T> where( Predicate<T> filer ){
        if( filer==null )return this
        new DataSource<T>( values.findAll { filer.test(it)} )
    }
}
