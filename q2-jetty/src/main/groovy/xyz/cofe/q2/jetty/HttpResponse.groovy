package xyz.cofe.q2.jetty

import groovy.util.logging.Log
import groovy.xml.MarkupBuilder
import xyz.cofe.q2.jetty.env.SysProps

import javax.servlet.http.HttpServletResponse
import java.util.logging.Level

/**
 * Обработка http ответа
 */
@Log
class HttpResponse implements HttpServletResponse {
    // сюда делегируются все вызовы по умолчанию
    @Delegate
    HttpServletResponse httpServletResponse

    /**
     * Конструктор
     * @param res объект - ответ, которому делегируются вызовы
     */
    HttpResponse( HttpServletResponse res ){
        if( res == null ) throw new IllegalArgumentException("res==null");
        this.httpServletResponse = res
    }

    /**
     * Вывод/рендер html
     * @param htmlOutputFn - функция принимающая {@link MarkupBuilder}
     */
    void html( @DelegatesTo(MarkupBuilder) Closure htmlOutputFn ){
        if( htmlOutputFn == null ) throw new IllegalArgumentException("htmlOutputFn==null");
        StringWriter str = new StringWriter()
        try {
            def mkBldr = new MarkupBuilder(str)
            htmlOutputFn.delegate = mkBldr
            htmlOutputFn.resolveStrategy = Closure.DELEGATE_FIRST
            def mk = mkBldr.mkp

            mk.yieldUnescaped( '<!DOCTYPE html>\n' )
            mk.yieldUnescaped( '<html>\n' )
            htmlOutputFn(mkBldr)
            mk.yieldUnescaped( '</html>\n' )

            setStatus(SC_OK)
            setContentType('text/html')
            writer.print str.toString()
        } catch( Throwable err ) {
            setStatus(SC_INTERNAL_SERVER_ERROR)
            log.log(Level.SEVERE,'html render error',err)
            if( SysProps.instance.debug ){
                contentType = 'text/plain'
                writer.print err
            }
        }
    }
}
