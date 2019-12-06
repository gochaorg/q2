package xyz.cofe.q2.jetty

import groovy.text.GStringTemplateEngine
import groovy.text.Template
import groovy.util.logging.Log
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.handler.AbstractHandler

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Логирование запросов
 */
class HttpLog implements Includable {
    private Template template = new GStringTemplateEngine().createTemplate(
        '''${remoteAddr} :${remotePort} ${method} ${requestURI} ${queryString}'''
    )

    private Logger log = Logger.getLogger('HTTP')
    private Level level = Level.INFO

    HttpLog(){}

    @Override
    void include( ServerConf conf ){
        if( conf==null )return
        conf.handlers.add(0,createLogHandler())
    }

    /**
     * Создание обработчика для логирования http запросов
     * @return обработчик
     */
    Handler createLogHandler(){
        new AbstractHandler() {
            @Override
            void handle( String target,
                         Request baseRequest,
                         HttpServletRequest request,
                         HttpServletResponse response
            ) throws IOException, ServletException{
                StringWriter msg = new StringWriter()

                Map props = [:]
                baseRequest.metaClass.properties
                    .findAll { !(it.name in [
                        'asyncContext','asyncStarted','contentParameters','contentRead',
                        'httpChannel', 'httpChannelState', 'httpFields', 'httpInput',
                        'inputState', 'inputStream', 'metaData', 'parts', 'push', 'pushBuilder', 'reader',
                        'response', 'session', 'sessionHandler', 'trailers',
                        'characterEncodingUnchecked',
                        'URIPathQuery',
                    ]) }.each { prop ->
                        props[prop.name] = prop.getProperty(baseRequest)
                    }
                template.make(props).writeTo(msg)
//                template.make(baseRequest.properties).writeTo(msg)
                log.log(level,msg.toString())
                //log.log(Level.INFO,"$baseRequest.method $baseRequest.requestURI")
            }
        }
    }
}
