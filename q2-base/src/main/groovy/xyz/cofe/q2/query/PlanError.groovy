package xyz.cofe.q2.query
/**
 * Ошибочный план
 */
class PlanError extends Error {
    /**
     * Конструктор
     * @param message текст сообщения
     * @param expr шаг на котором сломался план
     */
    PlanError(String message, DsExpr expr){
        super(message)
        this.expr = expr
    }

    /**
     * шаг на котором сломался план
     */
    DsExpr expr
}
