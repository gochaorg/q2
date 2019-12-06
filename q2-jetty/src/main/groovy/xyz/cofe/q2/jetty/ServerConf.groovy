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
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Predicate

/**
 * Конфигурирование Jetty сервера
 */
class ServerConf {
    /**
     * Ссылка на сам сервер
     */
    final Server server

    /**
     * Конструктор
     * @param server jetty сервер
     */
    ServerConf( Server server ){
        this.server = server
        if( server == null ) throw new IllegalArgumentException("server==null");
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

    /**
     * URL Контекста
     */
    final List<String> contextPath = new CopyOnWriteArrayList<>()

    /**
     * Обработчики запросов
     */
    final List<Handler> handlers = new CopyOnWriteArrayList<>()

    /**
     * Возвращает url контекст-а {@link #contextPath} в номрмальной форме ( '/some/a/b/c' )
     * @return url контекста или пустая строка
     */
    String getContextUrl(){
        if( contextPath.empty )return ''
        return contextPath.collect { String u ->
            if( u==null )return ''

            while( true ){
                if( u.endsWith('/') ) {
                    u = u.substring(0, u.length() - 1)
                    continue
                }
                break
            }

            if( u.length()==0 )return ''
            if( u=='/' )return ''
            if( !u.startsWith('/') )return '/'+u
            return u
        }.join('')
    }

    /**
     * Создает обработчик запроса.
     * При создании учитывается текущий контекст
     * (1. {@link #contextPath},
     *  2. {@link #context(java.lang.String, groovy.lang.Closure)}
     * )
     *
     * @param filter фильтрация запроса
     * @param fn обработка запроса. Функция вида:
     * <ul>
     *     <li> fn( HttpRequest req, HttpResponse res )
     *     <li> fn( HttpServletResponse|HttpResponse res )
     * </ul>
     * @return обработчик
     */
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

    /**
     * Создает контекст для выполняния запросов в рамках данного контекста.
     * <ol>
     *     <li> Добавляет к текущему указанный контекст
     *     <li> вызывает функцию fn
     *     <li> Удаляет добавленный контекст
     * </ol>
     * @param addr контекст
     * @param fn функция которая настраивает обработку запросов
     */
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

    /**
     * Добавляет обработку <b>get</b> запросов по указанному адресу
     * @param addr адрес
     * @param fn функция обработки, см {@link #request(java.util.function.Predicate, groovy.lang.Closure)}
     * @return Обработчик
     */
    Handler get( String addr, Closure fn ){
        if( addr == null ) throw new IllegalArgumentException("addr==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");
        return request( { JettyReq jreq ->
            if( !(jreq.addr == addr) )return false
            if( !(jreq.request?.method?.equalsIgnoreCase('get')) )return false
            return true
        }, fn )
    }

    /**
     * Добавляет обработку <b>post</b> запросов по указанному адресу
     * @param addr адрес
     * @param fn функция обработки, см {@link #request(java.util.function.Predicate, groovy.lang.Closure)}
     * @return Обработчик
     */
    Handler post( String addr, Closure fn ){
        if( addr == null ) throw new IllegalArgumentException("addr==null");
        if( fn == null ) throw new IllegalArgumentException("fn==null");
        return request( { JettyReq jreq ->
            if( !(jreq.addr == addr) )return false
            if( !(jreq.request?.method?.equalsIgnoreCase('post')) )return false
            return true
        }, fn )
    }

    /**
     * Добавление модуля
     * @param moduleClassname класс модуля
     */
    void include( Class<? extends Includable> moduleClassname ){
        if( moduleClassname == null ) throw new IllegalArgumentException("moduleClassname==null");
        def obj = moduleClassname.getDeclaredConstructor().newInstance()
        if( obj instanceof Includable ){
            include(obj as Includable)
        }
    }

    /**
     * Добавление модуля
     * @param module модуль
     */
    void include( Includable module ){
        if( module == null ) throw new IllegalArgumentException("module==null");
        module.include(this)
    }
}
