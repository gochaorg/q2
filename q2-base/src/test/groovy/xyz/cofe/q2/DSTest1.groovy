package xyz.cofe.q2

import org.junit.Test
import xyz.cofe.q2.model.Foo

import static xyz.cofe.q2.RootData.instance as root

/**
 * Тестирование DataSource
 */
class DSTest1 {
    @Test
    void fetch(){
        println "=== Пример простой выборки ==="
        root.foo.pick( { row ->
            println "id=$row.id name=$row.name"
        } )
    }

    @Test
    void where(){
        println "=== Выборка с фильтрацией ==="
        def ds = root.foo.where( row -> row.id==1 )

        int c1 = 0
        ds.pick( { row ->
            c1++
            println "id=$row.id name=$row.name"
        })
        println "получено $c1 строк"

        root.fooData.add( new Foo(id:1, name:"addA") )
        root.fooData.add( new Foo(id:1, name:"addB") )

        int c2 = 0
        ds.pick( { row ->
            c2++
            println "id=$row.id name=$row.name"
        })
        println "получено $c2 строк"

        assert( (c2-c1)==2 )
    }
}
