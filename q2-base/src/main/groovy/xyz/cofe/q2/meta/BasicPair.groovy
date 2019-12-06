package xyz.cofe.q2.meta

/**
 * Базовая реализация Pair
 * @param <A> Тип А
 * @param <B> Тип Б
 */
class BasicPair<A,B> implements Pair<A,B> {
    /**
     * Конструктор по умолчанию
     */
    BasicPair(){}

    /**
     * Конструктор
     * @param a значение А
     * @param b значение Б
     */
    BasicPair(A a, B b){
        this.a = a
        this.b = b
    }

    A a
    B b
}
