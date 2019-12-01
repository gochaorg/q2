/*
Лицензия MIT

Copyright © 2019 Камнев Георгий Палович (nt.gocha@gmail.com)

Данная лицензия разрешает лицам, получившим копию данного программного обеспечения
и сопутствующей документации (в дальнейшем именуемыми «Программное Обеспечение»),
безвозмездно использовать Программное Обеспечение без ограничений,
включая неограниченное право на использование, копирование, изменение, слияние,
публикацию, распространение, сублицензирование и/или продажу копий
Программного Обеспечения,
а также лицам, которым предоставляется данное Программное Обеспечение,
при соблюдении следующих условий:

Указанное выше уведомление об авторском праве и данные условия должны быть включены
во все копии или значимые части данного Программного Обеспечения.

ДАННОЕ ПРОГРАММНОЕ ОБЕСПЕЧЕНИЕ ПРЕДОСТАВЛЯЕТСЯ «КАК ЕСТЬ»,
БЕЗ КАКИХ-ЛИБО ГАРАНТИЙ, ЯВНО ВЫРАЖЕННЫХ ИЛИ ПОДРАЗУМЕВАЕМЫХ,
ВКЛЮЧАЯ ГАРАНТИИ ТОВАРНОЙ ПРИГОДНОСТИ, СООТВЕТСТВИЯ ПО ЕГО КОНКРЕТНОМУ НАЗНАЧЕНИЮ И ОТСУТСТВИЯ НАРУШЕНИЙ,
НО НЕ ОГРАНИЧИВАЯСЬ ИМИ. НИ В КАКОМ СЛУЧАЕ АВТОРЫ ИЛИ ПРАВООБЛАДАТЕЛИ НЕ НЕСУТ ОТВЕТСТВЕННОСТИ
ПО КАКИМ-ЛИБО ИСКАМ, ЗА УЩЕРБ ИЛИ ПО ИНЫМ ТРЕБОВАНИЯМ,
В ТОМ ЧИСЛЕ, ПРИ ДЕЙСТВИИ КОНТРАКТА, ДЕЛИКТЕ ИЛИ ИНОЙ СИТУАЦИИ,
ВОЗНИКШИМ ИЗ-ЗА ИСПОЛЬЗОВАНИЯ ПРОГРАММНОГО ОБЕСПЕЧЕНИЯ ИЛИ ИНЫХ ДЕЙСТВИЙ С ПРОГРАММНЫМ ОБЕСПЕЧЕНИЕМ.
*/

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
