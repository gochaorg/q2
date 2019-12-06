package xyz.cofe.q2.jetty

import org.eclipse.jetty.server.Request

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Объект для фильтрации http/jetty запроса
 */
class JettyReq {
    /**
     * Адрес на который поступил запрос
     */
    String addr

    /**
     * Сам jetty запрос
     */
    Request request

    /**
     * Стандартный интерфейс запроса
     */
    HttpServletRequest httpServletRequest

    /**
     * Стандартный интерфейс ответа
     */
    HttpServletResponse httpServletResponse
}
