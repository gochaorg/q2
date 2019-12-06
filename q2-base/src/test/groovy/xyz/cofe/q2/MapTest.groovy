package xyz.cofe.q2

import org.junit.Test
import xyz.cofe.q2.model.Foo

class MapTest {
    @Test
    void dsMapTest(){
        println " dsMapTest ".center(40,'=')
        RootData.instance.foo.select(
            id: { Foo foo -> foo.id },
            id2: { Foo  foo -> foo.id*2 },
            name: { Foo  foo -> foo.name },
            name2: { Foo  foo -> foo.name*2 }
        ).each { row ->
            println "id=$row.id id2=$row.id2 name=$row.name name2=$row.name2"
        }
    }
}
