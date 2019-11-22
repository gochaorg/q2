/**
 * Описывает манипуляции с однотипным источником данных
 */
export class DataSource<T> {
    constructor( values:T[] ){
        this.values = values
    }
    readonly values:T[]

    /**
     * Прямое извлечение данных
     * @param consumer получатель данных
     */
    fetch( consumer:(row:T)=>any ){
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
    where( filter:(row:T)=>boolean ):DataSource<T> {
        let res : T[] = []
        for( let i=0; i<this.values.length;i++ ){
            if( filter(this.values[i]) ){
                res.push(this.values[i])
            }
        }
        return new DataSource(res)
    }

    /**
     * Декартово множество
     * @param ds множество с которым происходит пересечение
     * @param consumer получатель
     */
    product<E>( ds : DataSource<E> ) : DataSource<{a:T,b:E}> {
        if( ds===undefined )throw new Error("invalid argument ds")
        let lst : {a:T,b:E}[] = []
        for( let i=0; i<this.values.length;i++ ){
            for( let j=0; j<ds.values.length; j++ ){
                let row = { a:this.values[i], b:ds.values[j] }
                lst.push( row )
            }
        }
        return new DataSource(lst);
    }

    /** 
     * Левое соединение таблиц
     * @param с какой таблице происходит соединение
     * @param функция возвращающая на каждую левую строку 0 или более строк из правой таблицы
     */
    join<E>( ds : DataSource<E>, fetching:(ds:DataSource<E>, a:T)=>E[] ) : DataSource<{a:T,b:E}> {
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
        return new DataSource(lst)
    }
}