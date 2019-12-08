import { Foo, Bar } from "../model/model"
import { RootData } from "../model/RootData"

const root = new RootData()

console.log( "=== Пример простой выборки ===" )
root.foo.pick((row)=>{
    console.log( "id="+row.id+" name="+row.name )
})

console.log( "=== Выборка с фильтрацией ===" )
root.foo
    .where( row=> row.id==1 )
    .pick(row=> console.log(`id=${row.id}`) )

console.log( "=== Пример декатового множества, с фильтрацией ===" )
root.foo.product( root.bar )
    .where((row)=>row.a.id == row.b.fooId )
    .pick((row)=>{
        let [f,b] = [row.a, row.b]
        console.log( "f{id="+f.id+" name="+f.name+"} b{id="+b.id+" name="+b.name+" fid="+b.fooId+"}" )
    })

console.log( "=== left join запрос ===" )
root.foo
    .where( row=> row.id==1 )
    .join( root.bar, 
        (barDs, fooRow)=> barDs.where( barRow=> barRow.fooId == fooRow.id ).values )
    .pick(row=> console.log(`${row.a.name} -> ${row.b.name}`))

console.log( "=== select запрос ===" )
root.foo
    .select({ col1: r=> r.id+r.id, col2: r=> r.name+r.name })
    .pick(row => {console.log(`${row.col1} / ${row.col2}`) });
