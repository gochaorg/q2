package xyz.cofe.q2.query.ast

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test

class AstBuilderTest {
    def toMap( String jsonSource ){
        ObjectMapper om = new ObjectMapper()
        def jsonNode = om.readTree jsonSource
        return om.treeToValue(jsonNode, Map)
    }

    String getWhereSource(){
        AstBuilderTest.class.getResource('/json/where.json')?.getText('utf-8')
    }

    @Test
    void testWhere01(){
        AstBuilder bldr = new AstBuilder()

        def ast = bldr.parse( toMap(whereSource) )
        assert ast!=null

        ast.tree.each { ts ->
            println ".|"*ts.level+" "+ts.node
        }
    }
}
