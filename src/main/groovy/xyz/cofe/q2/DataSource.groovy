package xyz.cofe.q2

import xyz.cofe.q2.meta.BasicPair
import xyz.cofe.q2.meta.Column
import xyz.cofe.q2.meta.Pair
import xyz.cofe.q2.model.Bar
import xyz.cofe.q2.model.Foo

import java.util.function.BiFunction
import java.util.function.BiPredicate
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
     * @param link функция которая связывает обеъект исходной выборки с объектами присоединенных данных
     * @return Набор хранящий пары значений из исходного и присоединенного источников данных
     */
    public <U> DataSource<Pair<T,U>> join( DataSource<U> ds, BiPredicate<T, U> link ){
        if( ds == null ) throw new IllegalArgumentException("ds==null");
        if( link == null ) throw new IllegalArgumentException("link == null");
        List<Column> columns = []
        columns.add(new Column(name: 'a', type: Object))
        columns.add(new Column(name: 'b', type: Object))

        def data = It.join(
            this,
            { T foo ->
                ds.where( { bar -> link.test(foo,bar)} )
            },
            { foo, bar -> new BasicPair( foo, bar) }
        )

        def resultDataset = new DataSource( columns, data )
        return resultDataset
    }

    /**
     * Преобразовывает входные данные в выходные
     * @param mapping объект содержащий именнованые функции преобхования входных данных, в целевые.
     * <br>
     * Пример:
     * <br><br>
     *
     * <code>
     * foo.select(                          <br>
     * &nbsp; id: &#x007b; Foo foo -> foo.id &#x007d;,         <br>
     * &nbsp; id2: &#x007b; Foo  foo -> foo.id*2 &#x007d;      <br>
     * ).each &#x007b; row ->                      <br>
     * &nbsp;  println "id=$row.id id2=$row.id2"  <br>
     * &#x007d;
     * </code>
     * @return вернет объекты типа Map, которые содержат результаты вычислений
     */
    public DataSource select( Map mapping ){
        if( mapping == null ) throw new IllegalArgumentException("mapping==null");

        Map<String,Function> mapFn = [:]
        mapping.each { k,f ->
            if( k instanceof String ){
                if( f instanceof Closure ){
                    mapFn[k] = f as Function
                }else if( f instanceof Function ){
                    mapFn[k] = f
                }
            }
        }

        Function mapper = { srcRow ->
            def resRow = [:]
            mapFn.each { String name, Function mapf ->
                resRow[name] = mapf.apply(srcRow)
            }
            return resRow
        } as Function

        List<Column> columns = mapFn.keySet().collect { new Column(name: it, type: Object) }
        return new DataSource( columns, It.map(this, mapper) )
    }
}
