import ax from "axios"
import { Foo } from '../model/model'
import * as parser from "esprima"

/**
 * Здесь определяем ответ который содержит табличные данные
 */
export interface Dataset {
    // Мета информация о таблице
    meta: {
        // Информация о колонках
        columns: {
            // информация о конкретной колонке
            column: {
                // Имя колоноки
                name: string

                // В теории здесь должен быть тип данных, но пока его нет :(
                type: string
            }
        }[]
    }

    // данные таблицы
    data: {
        // содержит массив из ключей/данные
        [name: string]:any
    }[]
}

/**
 * Выполнение запроса к api для получения таблицчных данных
 * @param url адрес api
 * @param data json запрос
 * @param consumer получатель запроса
 */
export async function fetchData<T>( 
    url:string, 
    data:any,
    consumer:{
        ok:(dataset:T[])=>any
    }
){
    await ax.post( url, JSON.stringify(data), {
        headers: {
            'accept': 'application/json',
            'content-type': 'application/json'
        }
    }).then( (res)=>{
        if( res.status>=200 && res.status<300 ){
            const ds = res.data as Dataset
            let resultSet : T[] = ds.data as T[]
            consumer.ok( resultSet )
        }else{
            console.error('bad status',res.status,' result=',res)
        }
    }).catch( (err)=>{
        console.error( "error: ",err )
    })
}
