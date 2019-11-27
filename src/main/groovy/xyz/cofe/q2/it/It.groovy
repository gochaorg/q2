package xyz.cofe.q2.it

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
    public static <T> Iterable<T> filter( Iterable<T> source, Predicate<T> filter ){
        if( source == null ) throw new IllegalArgumentException("source==null");
        if( filter == null ) throw new IllegalArgumentException("filter==null");

        return [
            iterator: {
                return new FilterIterator<>( source.iterator(), filter )
            }
        ] as Iterable
    }
}
