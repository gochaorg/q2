package xyz.cofe.q2.it

import java.util.function.Predicate

/**
 * Итератор с фильтрацией
 * @param <T> тип данных
 */
class FilterIterator<T> implements Iterator<T> {
    Predicate<T> filter
    Iterator<T> source
    T value

    boolean dataAvaliable = false

    FilterIterator( Iterator<T> source, Predicate<T> filter ){
        if( source == null ) throw new IllegalArgumentException("source==null");
        this.filter = filter
        this.source = source
        fetch()
    }

    boolean fetch(){
        dataAvaliable = false

        Iterator<T> src = source
        if( src==null ){
            dataAvaliable = false
            return false
        }

        while( true ){
            if( !src.hasNext() ){
                close()
                dataAvaliable = false
                return false
            }

            T val = src.next()

            Predicate<T> filter = this.filter
            if( filter == null ){
                value = val
                dataAvaliable = true
                return true
            }

            if( filter.test(val) ){
                value = val
                dataAvaliable = true
                return true
            }
        }
    }

    void close(){
        source = null
        value = null
        filter = null
        dataAvaliable = false
    }

    @Override
    boolean hasNext(){
        return dataAvaliable
    }

    @Override
    T next(){
        T res = value
        fetch()
        return res
    }
}
