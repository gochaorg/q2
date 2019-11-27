package xyz.cofe.q2

import com.fasterxml.jackson.databind.JsonNode
import ratpack.func.Action
import xyz.cofe.q2.model.Foo
import xyz.cofe.q2.proto.JsonOut

import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.jsonNode

//String JSON_TYPE = 'application/json; charset=utf-8'
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

        prefix( 't2' ){
            path( 'g' ){
                byContent {
                    html {
                        byMethod {
                            get {
                                render 'ok'
                            }
                            post {
                                render 'zz1'
                            }
                        }
                    }
                    type( JSON_TYPE ){
                        byMethod {
                            post {
                                parse( jsonNode() ).then { node ->
                                    println "node ${node.getClass().name} $node"
                                    new JsonOut().write(response, Foo, RootData.instance.foo)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
