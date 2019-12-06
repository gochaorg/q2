package xyz.cofe.q2.it

import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate

/**
 * Итераторы
 */
class It {
    /**
     * Итератор с фильтром
     * @param source исходный итератор
     * @param filter фильтр
     * @return фильтрующий итератор
     */
    public static <T> Iterable<T> where( Iterable<T> source, Predicate<T> filter ){
        if( source == null ) throw new IllegalArgumentException("source==null");
        if( filter == null ) throw new IllegalArgumentException("filter==null");

        return [
            iterator: {
                return new FilterIterator<>( source.iterator(), filter )
            }
        ] as Iterable
    }

    /**
     * join соединение
     * @param source исходные данные
     * @param join функция присоединения данных
     * @param result функция получения результата
     * @param withNoJoin ковертация соединения в результат
     * @return результирующий набор
     */
    public static <A,B,R> Iterable<R> join( Iterable<A> source, Function<A,Iterable<B>> join, BiFunction<A,B,R> result, boolean withNoJoin=true ){
        if( source == null ) throw new IllegalArgumentException("source==null");
        if( join == null ) throw new IllegalArgumentException("join==null");
        if( result == null ) throw new IllegalArgumentException("result==null");

        return [
            iterator: {
                return new JoinIterator(
                    source.iterator(),
                    { left ->
                        def bitr = join.apply(left)
                        return bitr!=null ? bitr.iterator() : null
                    },
                    result,
                    withNoJoin
                )
            }
        ] as Iterable
    }

    /**
     * преобразование данных из одно типа данных в другой
     * @param source исходные данные
     * @param mapping функция преобразования
     * @return итератор
     */
    public static <A,B> Iterable<B> map( Iterable<A> source, Function<A,B> mapping ){
        if( mapping == null ) throw new IllegalArgumentException("mapping==null");
        if( source == null ) throw new IllegalArgumentException("source==null");

        return [
            iterator: {
                return new MapIterator( source.iterator(), mapping )
            }
        ] as Iterable
    }
}
