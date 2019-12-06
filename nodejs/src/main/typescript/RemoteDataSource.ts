import ax from "axios";
import { DataSource } from './DataSource';
import { fetchData } from "./Datatransfer"
import * as esParser from "esprima"

/** Некий обощенный запрос к api */
export interface Expr {
    /** Компиляция запроса для последующц отправки на сервер */
    compile():any
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
    async fetch( consumer:(row:T)=>any ){
        const payLoad = this.expression.compile()
        await fetchData<T>( this.api, payLoad,
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

    /**
     * Создание join соединения (по аналогии с sql)
     * @param ds источник данных который будет присоединен
     * @param link условие соединения
     */
    join<E>( ds:RemoteDataSource<E>, link:(a:T, b:E)=>boolean ) : RemoteDataSource<{a:T,b:E}> {
        return new JoinDataSource( this, ds, link.toString() )
    }

    /**
     * Выборка колонок (по аналогии sql), т.е. аналогичная операция map - преобразование данных строки в нужные величины
     * @param input входные данные
     */
    select<Col,
    Input extends { [name:string]:(row:T)=>Col },
    Output extends {[K in keyof Input]:ReturnType<Input[K]>}
    >( input:Input ):RemoteDataSource<Output> {
        return new SelectDataSource( this, input )
    }
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

/**
 * Описывает манипуляции с однотипным источником данных
 */
export class NamedRemoteDataSource<T> extends RemoteDataSource<T> implements DataSource<T> {
    /** расположение api */
    readonly api:string

    /** имя источника данных */
    readonly name:string

    /**
     * Конструктор
     * @param api расположение api
     * @param name имя источника данных
     */
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

    /** Создание запроса */
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
 * Фильтрация источника данных
 */
class WhereDataSource<T> extends RemoteDataSource<T> {
    /** расположение api */
    readonly api:string
    readonly ds:RemoteDataSource<T>
    readonly jsArrowFnSource:string

    constructor( dataSource: RemoteDataSource<T>, jsArrowFnSource:string ){
        super()
        this.ds = dataSource;
        this.api = dataSource.api;
        this.jsArrowFnSource = jsArrowFnSource;
    }

    /** Создание запроса */
    get expression():Expr {
        return new Where(this.ds.expression, this.jsArrowFnSource)
    }
}

/** Запрос соединения данных */
export class Join implements Expr {
    /** Исходный запрос */
    readonly ds:Expr

    /** Исходный запрос */
    readonly joinData:Expr

    /** исходный код (js) стрелочной функции соединения */
    readonly jsArrowFnSource:string

    constructor( ds:Expr, joinData:Expr, jsArrowFnSource:string ){
        this.ds = ds
        this.joinData = joinData
        this.jsArrowFnSource = jsArrowFnSource
    }

    /** Создание запроса */
    compile(){
        return { 
            type: 'Join', 
            source: this.ds.compile(),
            join: this.joinData.compile(),
            filter: {
                code: this.jsArrowFnSource,
                ast: {
                    tree: esParser.parseScript( this.jsArrowFnSource ),
                    parser: 'esprima'
                }
            },
        }
    }
}

/**
 * Фильтрация источника данных
 */
class JoinDataSource<T,E> extends RemoteDataSource<{a:T,b:E}> {
    /** расположение api */
    readonly api:string
    readonly ds:RemoteDataSource<T>
    readonly joinDs:RemoteDataSource<E>
    readonly jsArrowFnSource:string

    constructor( ds: RemoteDataSource<T>, joinDs: RemoteDataSource<E>, jsArrowFnSource:string ){
        super()
        this.ds = ds;
        this.joinDs = joinDs;
        this.api = ds.api;
        this.jsArrowFnSource = jsArrowFnSource;
    }

    get expression():Expr {
        return new Join(
            this.ds.expression, 
            this.joinDs.expression,
            this.jsArrowFnSource
        )
    }
}

/**
 * Выражение преобразования из исходного формата в целевой
 */
export class Select implements Expr {
    readonly ds:Expr
    readonly mapping:{
        [name:string]:string
    }

    constructor( ds:Expr, mapping:{ [name:string]:string } ){
        this.ds = ds
        this.mapping = mapping
    }

    compile(){
        const mapping : {[name:string]:{
            code: string,
            ast: {
                tree: any,
                parser: string
            }
        }} = {}

        const keys = Object.keys(this.mapping)
        keys.forEach( key=>{
            mapping[key] = {
                code: this.mapping[key],
                ast: {
                    tree: esParser.parseScript(this.mapping[key]),
                    parser: 'esprima'
                }
            }
        })

        return {
            type: 'Select',
            source: this.ds.compile(),
            mapping: mapping
        }
    }
}

/**
 * Преобразование данных из исходного формата в целевой
 */
class SelectDataSource<
    T,
    Col,
    Input extends { [name:string]:(row:T)=>Col },
    Output extends {[K in keyof Input]:ReturnType<Input[K]>}
    > extends RemoteDataSource<Output> 
{
    /** расположение api */
    readonly api:string

    /** правила преобразования входных данных */
    readonly mapping:Input

    /** истоник данных */
    readonly ds:RemoteDataSource<T>

    /**
     * Конструктор
     * @param ds истоничк данных
     * @param input правила преобразования входных данных
     */
    constructor( ds:RemoteDataSource<T>, input:Input ){
        super()
        this.api = ds.api
        this.mapping = input
        this.ds = ds
    }

    /** Создание запроса */
    get expression():Expr {
        const map : { [name:string] : string } = {}
        Object.keys(this.mapping).forEach( key=> {
            map[key] = this.mapping[key].toString()
        })
        return new Select(this.ds.expression, map)
    }
}