package xyz.cofe.q2.it

import java.util.function.BiFunction
import java.util.function.Function

/**
 * Join содинение
 * @param <A> Исходный тип данных
 * @param <B> Присоединенный тип данных
 * @param <R> Функция получения результата
 */
class JoinIterator<A,B,R> implements Iterator<R> {
    private volatile Iterator<A> source
    private volatile Function<A,Iterator<B>> join
    private volatile BiFunction<A,B,R> result

    /**
     * Конструктор
     * @param src исходные данные
     * @param join получение присоединенных данных на каждый объект исходных данных
     * @param result ковертация в результат
     * @param withNoJoin true - возвращать исходные данные, даже если нет соответ присоединенных данных
     */
    JoinIterator( Iterator<A> src, Function<A,Iterator<B>> join, BiFunction<A,B,R> result, boolean withNoJoin=true ){
        this.source = src
        this.join = join
        this.result = result
        this.notJoined = withNoJoin
        fetch()
    }

    private volatile A sourceData
    private volatile int sourceDataConsumed = 0

    private volatile Iterator<B> joinData
    private volatile R fetched
    private volatile boolean notJoined = true

    private synchronized boolean fetch(){
        while( true ){
            if( source==null )return false
            if( join==null )return false
            if( result==null )return false

            if( sourceDataConsumed == 0 ){
                if( !source.hasNext() ){
                    close()
                    return false
                }
                sourceData = source.next()
                sourceDataConsumed++
            }

            boolean joinDataCreatedNow = false
            if( joinData == null ){
                joinData = join.apply(sourceData)
                joinDataCreatedNow = true
                if( joinData == null ){
                    if( notJoined ){
                        // return (A,null)
                        fetched = result.apply(sourceData, null)
                        return true
                    }else{
                        continue
                    }
                }
            }

            if( joinData.hasNext() ){
                B joinDataItem = joinData.next()
                fetched = result.apply(sourceData, joinDataItem)
                return true
            } else {
                if( notJoined && joinDataCreatedNow ){
                    // return (A,null)
                    fetched = result.apply(sourceData, null)
                    sourceDataConsumed = 0
                    return true
                }

                // try get next from source
                if( !source.hasNext() ){
                    close()
                    return false
                }
                sourceData = source.next()
                sourceDataConsumed++
                joinData = null
                // repeat
            }
        }
    }

    private synchronized void close(){
        source = null
        join = null
        result = null
        sourceData = null
        joinData = null
        fetched = null
    }

    @Override
    synchronized boolean hasNext(){
        return source!=null && join!=null && result!=null
    }

    @Override
    R next(){
        R res = fetched
        fetch()
        return res
    }
}
