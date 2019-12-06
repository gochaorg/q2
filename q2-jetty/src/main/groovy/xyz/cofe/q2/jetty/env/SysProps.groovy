package xyz.cofe.q2.jetty.env

/**
 * Системные параметры
 */
@Singleton
class SysProps {
    /**
     * Запуск jetty для отладки
     */
    final BoolSysProp debug = new BoolSysProp('xyz.cofe.q2.jetty.debug',false)
}
