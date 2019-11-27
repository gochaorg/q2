package xyz.cofe.q2

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import ratpack.func.Action
import ratpack.http.Status

import xyz.cofe.q2.model.Foo
import xyz.cofe.q2.proto.JsonOut
import xyz.cofe.q2.query.PlanBuilder

import static xyz.cofe.q2.RootData.instance as rootData

import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json
import static ratpack.jackson.Jackson.jsonNode

String JSON_TYPE = 'application/json'


// запуск сервера
ratpack {
    serverConfig {
        port 19500
        onError( Action.throwException() )
    }
    handlers {
        get {
            render "Hello World!"
        }

        //region meta data
        prefix( 'meta' ){
            get( 'roots' ){
                def roots = [:]
                rootData.meta.dataSources.each { name, info -> roots[name] = info.dataType }
                render json(roots)
            }
        }
        //endregion

        path( 'api' ){
            byMethod {
                get {
                    new JsonOut().write(response, Foo, RootData.instance.foo)
                }
                post { ctx ->
                    parse( jsonNode() ).then { JsonNode queryNode ->
                        println "queryNode:\n $queryNode"

                        //render "ok"
                        ObjectMapper om = new ObjectMapper()
                        def query = om.treeToValue(queryNode, Map)

                        def expr = new PlanBuilder(rootData.meta).build(query)

                        // full scan
                        new JsonOut().write(response, expr.compile())
                    }
                }
            }
        }
    }
}
