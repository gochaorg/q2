/**
 * Источник данных
 * @param T тип данных в источнике
 */
export interface DataSource<T> {
    /**
     * Извлечение данных из источника
     * @param consumer получатель данных
     */
    pick(consumer:(row:T)=>any ):any
}