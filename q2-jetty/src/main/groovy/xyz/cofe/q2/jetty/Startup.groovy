package xyz.cofe.q2.jetty

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler
import xyz.cofe.q2.jetty.env.SysProps
import xyz.cofe.q2.jetty.hello.HelloMod

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Запуск вебсервера
 */
class Startup {
    //region command line startup
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
        JUL.singleLineSimpleFormat()
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
    //endregion

    /**
     * Http сервер и его конфигурация
     */
    @Lazy Server server = {
        ServerConf.configure( new Server(port) ){
            include(new HttpLog())
            include HelloMod
        }
    }()
}
