package xyz.cofe.q2

import com.fasterxml.jackson.databind.JsonNode
import ratpack.func.Action
import xyz.cofe.q2.model.Foo

import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.jsonNode

String JSON_TYPE = 'application/json'

ratpack {
    serverConfig {
        port 19500
        onError( Action.throwException() )
    }
    handlers {
        get {
            render "Hello World!"
        }

        path( 'test' ){
            byMethod {
                get {
                    new JsonOut().write(response, Foo, RootData.instance.foo)
                }
                post { ctx ->
                    render parse( jsonNode() ).map { JsonNode n ->
                        println "n ${n.getClass().name} $n"
                        "ok"
                    }
                }
            }
        }
    }
}
