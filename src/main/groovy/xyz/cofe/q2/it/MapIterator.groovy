package xyz.cofe.q2.it

import java.util.function.Function

/**
 * Итератор для преобразования из одного типа данных в другой
 * @param S исходный тип данных
 * @param D целевой тип данных
 */
class MapIterator<S,D> implements Iterator<D> {
    private volatile Iterator<S> origin
    private volatile Function<S,D> mapping

    public MapIterator(Iterator<S> src,Function<S,D> map){
        if( src==null )throw new IllegalArgumentException("src == null")
        if( map==null ) throw new IllegalArgumentException("map == null")
        this.origin = src
        this.mapping = map
    }

    @Override
    synchronized boolean hasNext(){
        return origin!=null && origin.hasNext()
    }

    @Override
    synchronized D next(){
        if( origin==null )return null
        if( !origin.hasNext() ){
            close()
            return null
        }

        S s = origin.next()
        return mapping.apply(s)
    }

    synchronized void close(){
        origin = null
        mapping = null
    }
}
