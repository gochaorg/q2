package xyz.cofe.q2.jetty

/**
 * Модуль для конфигурации jetty сервера
 */
abstract class Module extends Script implements Includable {
    @Delegate ServerConf serverConf

    abstract def runScript()
    def preRun() {
    }

    def postRun() {
    }

    def run() {
        preRun()
        try {
            runScript()
        } finally {
            postRun()
        }
    }

    @Override
    void include( ServerConf conf ){
        if( conf == null ) throw new IllegalArgumentException("conf==null");
        this.@serverConf = conf
        run()
    }
}
