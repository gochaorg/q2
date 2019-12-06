package xyz.cofe.q2.jetty

/**
 * Поддержка подключаемого модуля
 */
interface Includable {
    void include( ServerConf conf )
}
