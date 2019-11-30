package xyz.cofe.q2

import org.junit.Test
import xyz.cofe.q2.it.It
import xyz.cofe.q2.it.JoinIterator
import xyz.cofe.q2.meta.BasicPair
import xyz.cofe.q2.meta.Pair
import xyz.cofe.q2.model.Bar
import xyz.cofe.q2.model.Foo

import java.util.function.BiFunction
import java.util.function.Function

class JoinIteratorTest {
    @Test
    void test01(){
        println " test01() ".center(40,'=')

        def fooDs = RootData.instance.foo
        def barDs = RootData.instance.bar

        JoinIterator<Foo, Bar, Pair<Foo,Bar>> itr = new JoinIterator<>(
            fooDs.where( {foo -> foo.id==1} ).values.iterator(),
            { Foo foo ->
                barDs.where( { bar -> bar.fooId == foo.id } ).values.iterator()
            },
            { leftRow, joinRow -> new BasicPair(leftRow, joinRow) }
        )

        while( itr.hasNext() ){
            def v = itr.next()
            println "foo=${v.a?.name} <- bar=${v.b?.name}"
        }
    }

    @Test
    void test02(){
        println " test02() ".center(40,'=')

        def fooDs = RootData.instance.foo
        def barDs = RootData.instance.bar

        It.join(
            fooDs.where {foo -> foo.id==1},
            { Foo foo ->
                barDs.where { bar -> bar.fooId == foo.id }
            },
            { Foo foo, Bar bar -> [a:foo, b:bar]}
        ).each { v ->
            println "foo=${v.a?.name} <- bar=${v.b?.name}"
        }
    }

    @Test
    void test03(){
        println " test03() ".center(40,'=')

        def fooDs = RootData.instance.foo
        def barDs = RootData.instance.bar
        def joinDs = fooDs.where { it.id==1 } join( barDs ) { foo, bar -> bar.fooId == foo.id }

        joinDs.each { v ->
            println "foo=${v.a?.name} <- bar=${v.b?.name}"
        }
    }
}
