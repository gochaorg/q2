import { DataSource } from './DataSource';

/**
 * Описывает манипуляции с однотипным источником данных
 */
export class LocalDataSource<T> implements DataSource<T> {
    constructor( values:T[] ){
        this.values = values
    }
    readonly values:T[]

    /**
     * Прямое извлечение данных
     * @param consumer получатель данных
     */
    pick(consumer:(row:T)=>any ){
        if(consumer){
            for( let i=0; i<this.values.length;i++ ){
                consumer(this.values[i])
            }
        }else{
            throw new Error('argument consumer undefined')
        }
    }

    /**
     * Фильтрация
     * @param filter функция фильтр
     */
    where( filter:(row:T)=>boolean ):LocalDataSource<T> {
        let res : T[] = []
        for( let i=0; i<this.values.length;i++ ){
            if( filter(this.values[i]) ){
                res.push(this.values[i])
            }
        }
        return new LocalDataSource(res)
    }

    /**
     * Декартово множество
     * @param ds множество с которым происходит пересечение
     * @param consumer получатель
     */
    product<E>( ds : LocalDataSource<E> ) : LocalDataSource<{a:T,b:E}> {
        if( ds===undefined )throw new Error("invalid argument ds")
        let lst : {a:T,b:E}[] = []
        for( let i=0; i<this.values.length;i++ ){
            for( let j=0; j<ds.values.length; j++ ){
                let row = { a:this.values[i], b:ds.values[j] }
                lst.push( row )
            }
        }
        return new LocalDataSource(lst);
    }

    /** 
     * Левое соединение таблиц
     * @param с какой таблице происходит соединение
     * @param функция возвращающая на каждую левую строку 0 или более строк из правой таблицы
     */
    join<E>( ds : LocalDataSource<E>, fetching:(ds:LocalDataSource<E>, a:T)=>E[] ) : LocalDataSource<{a:T,b:E}> {
        let lst : {a:T,b:E}[] = []
        for( let i=0; i<this.values.length;i++ ){
            let a = this.values[i]
            let arrOfB = fetching(ds,a)
            if( arrOfB == null || arrOfB.length<1 ){
                lst.push( {a:a, b:arrOfB[0]} )
            }else{
                for( let j=0; j<arrOfB.length; j++ ){
                    lst.push( {a:a, b:arrOfB[j]} )
                }
            }
        }
        return new LocalDataSource(lst)
    }

    /** 
     * Выборка и преобразование полученных значений в целевые
     * @param input Описывает как преобразовывать входной объект
     */
    select<Row,Col,Input extends { [name:string]:(row:T)=>Col },Output extends {[K in keyof Input]:ReturnType<Input[K]>}>(
        input:Input
    ):LocalDataSource<Output> {
        const rezultRows : {[n:string]:any}[] = [];
        for( const srcRow of this.values ){
            const row : {[n:string]:any} = {}
            for( const tKey in input ){
                row[tKey] = input[tKey](srcRow)
            }
            rezultRows.push(row)
        }
        const ds = new LocalDataSource(rezultRows as Output[])
        return ds;
    }
}