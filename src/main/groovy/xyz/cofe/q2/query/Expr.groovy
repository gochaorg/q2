package xyz.cofe.q2.query

import xyz.cofe.q2.DataSource

/**
 * Узел плана запросов
 */
interface Expr {
    /** Компиляция */
    DataSource compile();
}
