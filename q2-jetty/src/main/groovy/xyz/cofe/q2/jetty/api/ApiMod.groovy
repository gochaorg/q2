package xyz.cofe.q2.jetty.api

import com.fasterxml.jackson.databind.ObjectMapper
import xyz.cofe.q2.jetty.HttpRequest
import xyz.cofe.q2.jetty.HttpResponse
import xyz.cofe.q2.jetty.Includable
import xyz.cofe.q2.jetty.Module
import xyz.cofe.q2.jetty.ServerConf
import xyz.cofe.q2.proto.JsonOut
import xyz.cofe.q2.query.PlanBuilder
import static xyz.cofe.q2.RootData.instance as rootData

/**
 * Модуль q2 api
 */
class ApiMod extends Module {
    // mime/content-type для json
    final static JSON_MIME = 'application/json'

    @Override
    def runScript(){
        post '/api', { HttpRequest req, HttpResponse res ->
            // проверяем что прилетает json запрос
            if( req.contentType?.equalsIgnoreCase(JSON_MIME) && req.characterEncoding!=null ){

                // простая карта
                def query = new ObjectMapper().readValue(req.reader, Map)

                // план запроса
                def expr = new PlanBuilder(rootData.meta).build(query)

                // генерируем результат сразу в поток
                res.contentType = JSON_MIME
                res.setStatus( HttpResponse.SC_OK )
                new JsonOut().write(res.writer, expr.compile())
            }
        }
    }
}
