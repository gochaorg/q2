package xyz.cofe.q2

import org.junit.Test
import xyz.cofe.q2.model.Foo
import xyz.cofe.q2.proto.JsonOut

/** Тестирование json */
class JsonOutTest {
    @Test
    void test1(){
        JsonOut json = new JsonOut()

        StringWriter sw = new StringWriter()
        json.write(sw, RootData.instance.foo)

        println "json:\n$sw"
    }
}
