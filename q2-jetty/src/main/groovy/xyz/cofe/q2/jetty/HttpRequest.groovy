package xyz.cofe.q2.jetty

import javax.servlet.http.HttpServletRequest

/**
 * Обработка http запроса
 */
class HttpRequest implements HttpServletRequest {
    // сюда делегируются все вызовы по умолчанию
    @Delegate HttpServletRequest httpServletRequest

    /**
     * Конструктор
     * @param res объект - ответ, которому делегируются вызовы
     */
    HttpRequest( HttpServletRequest req ){
        if( req == null ) throw new IllegalArgumentException("req==null");
        this.httpServletRequest = req
    }
}
