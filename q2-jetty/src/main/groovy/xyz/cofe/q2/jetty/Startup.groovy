package xyz.cofe.q2.jetty

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler
import xyz.cofe.q2.jetty.env.SysProps

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Запуск вебсервера
 */
class Startup {
    /**
     * Входная точка
     * @param args аргументы сервера
     */
    public static void main(String[] args){
        new Startup().start(args)
    }

    /**
     * Запуск сервера
     * @param args аргументы командной строки
     */
    void start(String[] args){
        cmdLine = new CmdLine(args)
        if( debug ){
            System.setProperty(SysProps.instance.debug.name,'true')
        }
        server.start()
    }

    /**
     * Парсинг командной строки
     */
    private CmdLine cmdLine = new CmdLine("")

    /**
     * Номер порта, по умолчанию 19500
     */
    @Lazy int port = { (cmdLine.port ?: 19500) as int }()

    /**
     * Запуск в режиме отладки
     */
    @Lazy boolean debug = { (cmdLine.debug ?: false) as boolean }()

    /**
     * Http сервер
     */
    @Lazy Server server = {
        ServerConf.configure( new Server(port) ){
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
        }
    }()
}
