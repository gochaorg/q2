import ax from "axios"
import { Foo } from './model';

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

async function run1(){
    let resultSet : Foo[] = []
    await ax.get( 'http://localhost:19500/test' ).then( (res)=>{
        // console.log( res.data )
        const ds = res.data as Dataset
        console.log( `meta.type = ${ds.meta.type}` )
        console.log( `meta.columns:`, ds.meta.columns.map(x=>x.column) )
        resultSet = ds.data as Foo[]
    })
    console.log( "accepted: ", resultSet )
    resultSet.forEach( x => console.log(`id=${x.id} name=${x.name}`))
}
run1()
