package xyz.cofe.q2

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import ratpack.func.Action
import ratpack.http.Status
import xyz.cofe.q2.model.Foo

import java.lang.reflect.Field

import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json
import static ratpack.jackson.Jackson.jsonNode

String JSON_TYPE = 'application/json'

// Создание метаданных
println "build meta data"
def metaData = [:]

RootData.instance.getClass().getDeclaredFields().findAll {
    DataSource.isAssignableFrom( it.type )
}.collect { field ->
    def gt = field.getGenericType()
    if( gt instanceof java.lang.reflect.ParameterizedType ){
        def pt = gt as java.lang.reflect.ParameterizedType
        def tparams = pt.actualTypeArguments
        if( tparams.length==1 )return [
            name:field.name,
            type:tparams[0],
            field: field
        ]
    }
    return [name:field.name, type:gt.class, field: field]
}.each {
    metaData[it.name] = [
        name: it.name,
        type: it.type,
        field: it.field,
        value: RootData.instance[it.name]
    ]
}

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
                metaData.each { name, info -> roots[name] = info.type.name }
                render json()
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

                        if( query.type == 'From' ){
                            if( !metaData.containsKey(query.name) ){
                                response.status(404)
                                response.send("$query.name not found")
                                return
                            }

                            // full scan
                            new JsonOut().write(response, metaData[query.name].type, metaData[query.name].value)
                        }

                        response.status(Status.BAD_REQUEST)
                        response.send("undefined query type $query.type")
                    }
                }
            }
        }
    }
}
