package xyz.cofe.q2.it

import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate

/**
 * Создание итератора по дереву
 *
 * @param <A> тип узла дерева
 */
public interface TreeIterBuilder<A> {
    /**
     * Создание итератора
     *
     * @return итератор
     */
    Iterable<A> walk();

    /**
     * Создание итератора
     *
     * @return итератор
     */
    Iterable<TreeStep<A>> go();

    TreeIterBuilder<A> poll(Function<List<TreeStep<A>>, TreeStep<A>> poll );

    TreeIterBuilder<A> push(Consumer<TreeIterator.PushStep<A>> push );

    TreeIterBuilder<A> filter(Predicate<TreeStep<A>> allow );

    TreeIterBuilder<A> pushFirst();

    TreeIterBuilder<A> pushLast();

    TreeIterBuilder<A> pushOrdered();

    TreeIterBuilder<A> pollFirst();

    TreeIterBuilder<A> pollLast();

    TreeIterBuilder<A> checkCycles();
}
