package xyz.cofe.q2.jetty

import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.handler.ContextHandler
import org.eclipse.jetty.server.handler.HandlerList

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.function.Predicate

/**
 * Конфигурирование Jetty сервера
 */
class ServerConf {
    final Server server

    /**
     * Конструктор
     * @param server jetty сервер
     */
    ServerConf( Server server ){
        if( server == null ) throw new IllegalArgumentException("server==null");
        this.server = server
    }

    /**
     * Конфигурирование Jetty сервера
     * @param server jetty сервер
     * @param fn конфигурация
     * @return jetty сервер
     */
    static Server configure( Server server, @DelegatesTo(ServerConf) Closure fn ){
        if( fn == null ) throw new IllegalArgumentException("fn==null")
        ServerConf conf = new ServerConf(server)
        fn.delegate = conf
        fn.resolveStrategy = Closure.DELEGATE_FIRST
        fn()
        if( !conf.handlers.empty ){
            HandlerList handlerList = new HandlerList()
            conf.handlers.findAll {it!=null}.each { handlerList.addHandler(it) }
            server.setHandler( handlerList )
        }
        return server
    }

    final List<String> contextPath = []
    final List<Handler> handlers = []

    String getContextUrl(){
        if( contextPath.empty )return ''
        return contextPath.collect { String u ->
            if( !u.startsWith('/') )return '/'+u
            return u
        }.join('')
    }

    Handler request( Predicate<JettyReq> filter, Closure fn ){
        if( filter == null ) throw new IllegalArgumentException("filter==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");
        Handler ahdlr = new AbstractHandler() {
            @Override
            void handle(
                String reqAddr,
                Request request,
                HttpServletRequest httpServletRequest,
                HttpServletResponse httpServletResponse
            ) throws IOException, ServletException {
                if( !filter.test(new JettyReq(
                    addr: reqAddr,
                    request:request,
                    httpServletRequest: httpServletRequest,
                    httpServletResponse: httpServletResponse
                )) ){
                    return
                }

                Class[] params = fn.getParameterTypes()

                if( params.length==2 ){
                    httpServletResponse.status = HttpServletResponse.SC_OK
                    fn( new HttpRequest(httpServletRequest), new HttpResponse(httpServletResponse) )
                    request.handled = true
                }

                if( params.length==1 && params[0] in [HttpServletResponse,HttpResponse] ){
                    httpServletResponse.status = HttpServletResponse.SC_OK
                    fn( new HttpResponse(httpServletResponse) )
                    request.handled = true
                }
            }
        }

        if( !contextPath.empty ){
            String url = contextUrl
            ContextHandler ch = new ContextHandler(url)
            ch.server = server
            ch.handler = ahdlr
            ahdlr = ch
        }

        handlers.add ahdlr
        return ahdlr
    }

    void context( String addr, @DelegatesTo(ServerConf) Closure fn ){
        if( addr == null ) throw new IllegalArgumentException("addr==null");
        if( fn == null ) throw new IllegalArgumentException("fn == null");
        try{
            contextPath.add addr
            fn(this)
        } finally {
            contextPath.remove( (contextPath.size()-1) as int )
        }
    }

    Handler get( String addr, Closure fn ){
        if( addr == null ) throw new IllegalArgumentException("addr==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");
        return request( { JettyReq jreq ->
            if( !jreq.addr == addr )return false
            if( !jreq.request?.method?.equalsIgnoreCase('get') )return false
            return true
        }, fn )
    }

    Handler post( String addr, Closure fn ){
        if( addr == null ) throw new IllegalArgumentException("addr==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");
        return request( { JettyReq jreq ->
            if( !jreq.addr == addr )return false
            if( !jreq.request?.method?.equalsIgnoreCase('post') )return false
            return true
        }, fn )
    }
}
