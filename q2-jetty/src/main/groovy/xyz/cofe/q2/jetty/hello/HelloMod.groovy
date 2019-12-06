package xyz.cofe.q2.jetty.hello

import com.fasterxml.jackson.databind.ObjectMapper
import xyz.cofe.q2.jetty.HttpRequest
import xyz.cofe.q2.jetty.HttpResponse
import xyz.cofe.q2.jetty.Module

/**
 * Модуль приветсвия
 */
class HelloMod extends Module {
    @Override
    def runScript(){
        context('/p') {
            get( '/' ) { HttpRequest req, HttpResponse res ->
                res.html {
                    body {
                        h1('Hello/Привет!')
                    }
                }
            }
        }
        get '/', { HttpRequest req, HttpResponse res ->
            res.writer.println "Ok, i'am fine"
        }
        post '/json', { HttpRequest req, HttpResponse res ->
            if( req.contentType?.equalsIgnoreCase('application/json') && req.characterEncoding!=null ){
                ObjectMapper om = new ObjectMapper()
                def map = om.readValue(req.reader, Map)
                println map

                res.writer.println "success $map"
            }
        }
    }
}
