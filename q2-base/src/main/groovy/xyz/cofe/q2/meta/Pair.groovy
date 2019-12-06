package xyz.cofe.q2.meta

/**
 * Составной тип данных
 * @param <A> первый тип
 * @param <B> второй тип
 */
interface Pair<A,B> {
    /**
     * Возвращает дынные первого типа
     * @return данные
     */
    A getA()

    /**
     * Данные второго типа
     * @return данные
     */
    B getB()
}
