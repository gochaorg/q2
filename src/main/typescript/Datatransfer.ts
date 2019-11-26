import ax from "axios"
import { Foo } from './model'
import * as parser from "esprima"

interface Dataset {
    meta: {
        type: string,
        columns: {
            column: {
                name: string
                type: string
            }
        }[]
    }
    data: {
        [name: string]:any
    }[]
}

async function fetchData<T>( 
    url:string, 
    data:any,
    consumer:{
        ok:(dataset:T[])=>any
    }
){
    ax.post( url, JSON.stringify(data), {
        headers: {
            'accept': 'application/json',
            'content-type': 'application/json'
        }
    }).then( (res)=>{
        if( res.status>=200 && res.status<300 ){
            const ds = res.data as Dataset
            let resultSet : T[] = []

            console.log( `meta.columns:`, ds.meta.columns.map(x=>x.column) )
            
            resultSet = ds.data as T[]
            consumer.ok( resultSet )
        }else{
            console.error('bad status',res.status,' result=',res)
        }
    }).catch( (err)=>{
        console.error( "error: ",err )
    })
}

const filter1 = ( r:Foo ) => r.id == 1;
const payLoad = {filter: parser.parseScript(filter1.toString()) }
fetchData( 'http://localhost:19500/t2/g', payLoad, 
    { 
        ok:(data:Foo[])=>{
            console.log('data:',data)
        }
    }
)
