package xyz.cofe.q2

import xyz.cofe.q2.meta.BasicPair
import xyz.cofe.q2.meta.Column
import xyz.cofe.q2.meta.Pair
import xyz.cofe.q2.model.Bar
import xyz.cofe.q2.model.Foo

import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Predicate
import xyz.cofe.q2.it.It

/** Набор данных */
class DataSource<T> implements Iterable<T> {
    /**
     * Набор данных
     */
    @SuppressWarnings("GrFinalVariableAccess")
    private final Iterable<T> valuesInstance

    /**
     * Описание набора данных
     */
    @SuppressWarnings("GrFinalVariableAccess")
    private final List<Column> columnsInstance

    /**
     * Набор данных
     * @param columns описание колонок данных
     * @param values сами данные
     */
    DataSource( List<Column> columns, Iterable<T> values ){
        if( columns == null ) throw new IllegalArgumentException("columns==null");
        this.columnsInstance = columns

        if( values!=null ){
            this.valuesInstance = values
        }else{
            this.valuesInstance = []
        }
    }

    /**
     * Набор данных
     * @param dataType описание колонок данных
     * @param values сами данные
     */
    DataSource( Class dataType, Iterable<T> values ){
        if( dataType == null ) throw new IllegalArgumentException("dataType==null");
        this.columnsInstance = Column.columnsOf(dataType)

        if( values!=null ){
            this.valuesInstance = values
        }else{
            this.valuesInstance = []
        }
    }

    /** Извлечение данных */
    void fetch( Consumer<T> dataConsumer ){
        if( dataConsumer==null )throw new IllegalArgumentException("dataConsumer == null")
        valuesInstance.each { dataConsumer.accept(it) }
    }

    /** Получение списка значений */
    Iterable<T> getValues(){ return this.valuesInstance }

    /**
     * Получение итератора
     * @return итератор
     */
    Iterator<T> iterator(){ return values.iterator() }

    /**
     * Фильтрация данных
     * @param filer фильтр
     * @return Источник данных
     */
    DataSource<T> where( Predicate<T> filter ){
        if( filter==null )return this
        new DataSource<T>( columnsInstance, It.where(valuesInstance,filter) )
    }

    /**
     * Join соединение данных
     * @param ds набор данных из которого будет формироваться присоединяемая порция данных
     * @param fetching функция которая извлекает из набора ds порцию данных, для каждого объекта исходного набора данных
     * @return Набор хранящий пары значений из исходного и присоединенного источников данных
     */
    public <U> DataSource<Pair<T,U>> join2( DataSource<U> ds, BiFunction<DataSource<U>, T, Iterable<U>> fetching ){
        if( ds == null ) throw new IllegalArgumentException("ds==null");
        if( fetching == null ) throw new IllegalArgumentException("fetching==null");
        List<Column> columns = []
        columns.add(new Column(name: 'a', type: Object))
        columns.add(new Column(name: 'b', type: Object))

        def data = It.join(
            this,
            { T foo ->
                def vals = fetching.apply(ds, foo)
                return vals
            },
            { foo, bar -> new BasicPair( foo, bar) }
        )

        def resultDataset = new DataSource( columns, data )
        return resultDataset
    }

    /**
     * Join соединение данных
     * @param ds набор данных из которого будет формироваться присоединяемая порция данных
     * @param fetching функция которая извлекает из набора ds порцию данных, для каждого объекта исходного набора данных
     * @return Набор хранящий пары значений из исходного и присоединенного источников данных
     */
    public <U> DataSource<Pair<T,U>> join( DataSource<U> ds, Function<T, Iterable<U>> fetching ){
        if( ds == null ) throw new IllegalArgumentException("ds==null");
        if( fetching == null ) throw new IllegalArgumentException("fetching==null");
        List<Column> columns = []
        columns.add(new Column(name: 'a', type: Object))
        columns.add(new Column(name: 'b', type: Object))

        def data = It.join(
            this,
            fetching,
            { foo, bar -> new BasicPair( foo, bar) }
        )

        def resultDataset = new DataSource( columns, data )
        return resultDataset
    }
}
