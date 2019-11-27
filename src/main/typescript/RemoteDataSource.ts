import ax from "axios";
import { DataSource } from './DataSource';
import { fetchData } from "./Datatransfer"
import * as esParser from "esprima"

/** Некий обощенный запрос к api */
export interface Expr {
    /** Компиляция запроса для последующц отправки на сервер */
    compile():any
}

/** Запрос данных из источника */
export class From implements Expr {
    /** Имя источника данных */
    readonly name:string

    /** Конструктор
     * @param name имя источника
     */
    constructor( name:string ){
        this.name = name
    }

    /** Компиляция запроса для последующц отправки на сервер */
    compile(){
        return { type: 'From', name: this.name }
    }

    /** Создание запрос с фильтрацией данных
     * @param jsArrowFnSource исходный код (js) стрелочной функции фильтра
     */
    where(jsArrowFnSource:string):Expr{
        return new Where(this,jsArrowFnSource)
    }
}

/** Запрос с фильтром */
export class Where implements Expr {
    /** Исходный запрос */
    readonly ds:Expr

    /** исходный код (js) стрелочной функции фильтра */
    readonly jsArrowFnSource:string

    /** Конструктор
     * @param ds исходный набор данных
     * @param jsArrowFnSource исходный код (js) стрелочной функции фильтра
     */
    constructor( ds:Expr, jsArrowFnSource:string ){
        this.ds = ds
        this.jsArrowFnSource = jsArrowFnSource
    }

    compile() {
        return { 
            type: 'Where', 
            filter: {
                code: this.jsArrowFnSource,
                ast: {
                    tree: esParser.parseScript( this.jsArrowFnSource ),
                    parser: 'esprima'
                }
            },
            dataSource: this.ds.compile()
        }
    }
}

/**
 * Интерфейс для создания запроса
 */
export interface ExpressionBuilder {
    expression: Expr
}

/** Интерфейс для вызова api */
export interface ApiCall {
    api: string
}

/**
 * Удаленный(внешний) источник данных
 */
export abstract class RemoteDataSource<T> implements ExpressionBuilder, ApiCall, DataSource<T> {
    /** Создание запроса */
    abstract get expression():Expr

    /** адрес */
    abstract get api(): string

    /**
     * Прямое извлечение данных
     * @param consumer получатель данных
     */
    fetch( consumer:(row:T)=>any ){
        const payLoad = this.expression.compile()
        fetchData<T>( this.api, payLoad, 
            {
                ok: rows=>{
                    rows.forEach( row=>consumer(row) )
                }
            } 
        )
    }

    /**
     * Создание запроса с фильтром
     * @param filter фильтр
     */
    where( filter:(row:T)=>boolean ):RemoteDataSource<T> {
        return new WhereDataSource<T>( this, filter.toString() )
    }
}

/**
 * Описывает манипуляции с однотипным источником данных
 */
export class NamedRemoteDataSource<T> extends RemoteDataSource<T> implements DataSource<T> {
    readonly api:string
    readonly name:string    

    constructor(api:string,name:string){
        super()
        this.api = api
        this.name = name
    }

    /** Создание запроса */
    get expression():Expr {
        return new From(this.name)
    }
}

/**
 * Фильтрация источника данных
 */
class WhereDataSource<T> extends RemoteDataSource<T> {
    readonly api:string
    readonly ds:RemoteDataSource<T>
    readonly jsArrowFnSource:string

    constructor( dataSource: RemoteDataSource<T>, jsArrowFnSource:string ){
        super()
        this.ds = dataSource;
        this.api = dataSource.api;
        this.jsArrowFnSource = jsArrowFnSource;
    }

    get expression():Expr {
        return new Where(this.ds.expression, this.jsArrowFnSource)
    }
}
