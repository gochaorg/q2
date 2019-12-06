package xyz.cofe.q2

import org.junit.Test
import xyz.cofe.q2.meta.DataSourceInfo
import xyz.cofe.q2.model.Foo

class DataSourceInfoTest1 {
    @Test
    void test1(){
        DataSourceInfo dsInfo = new DataSourceInfo(
            dataType: Foo
        )
        println dsInfo.columns.collect {
            "name: $it.name, type: $it.type"
        }.join("\n")
    }
}
