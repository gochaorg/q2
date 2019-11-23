export enum StoreState {
    /** Строка присоединена к таблице, и помечена для удаления */
    Deleted,

    /** Строка не присоединена к таблице */
    Detached,

    /** Строка присоединена к таблице и содержит не изменные данные */
    Fixed,

    /** Строка присоединена к таблице, и помечена для добавления */
    Inserted,

    /** Строка присоединена к таблице и содержит изменные данные */
    Updated
}

/**
 * Сущность
 */
export class Entity {
    private __state : StoreState = StoreState.Detached
    get _state():StoreState { return this.__state }
    protected setState(ss:StoreState):void { this.__state = ss}
}
