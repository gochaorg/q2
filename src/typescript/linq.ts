import { Foo, Bar } from "./model"
import { RootData } from "./RootData"

const root = new RootData()

console.log( "=== Пример простой выборки ===" )
root.foo.fetch((row)=>{
    console.log( "id="+row.id+" name="+row.name )
})

console.log( "=== Выборка с фильтрацией ===" )
root.foo
    .where( row=> row.id==1 )
    .fetch( row=> console.log(`id=${row.id}`) )

console.log( "=== Пример декатового множества, с фильтрацией ===" )
root.foo.product( root.bar )
    .where((row)=>row.a.id == row.b.fooId )
    .fetch((row)=>{
        let [f,b] = [row.a, row.b]
        console.log( "f{id="+f.id+" name="+f.name+"} b{id="+b.id+" name="+b.name+" fid="+b.fooId+"}" )
    })

    console.log( "=== left join запрос ===" )
    root.foo
        .where( row=> row.id==1 )
        .join( root.bar, 
            (barDs, fooRow)=> barDs.where( barRow=> barRow.fooId == fooRow.id ).values )
        .fetch( row=> console.log(`${row.a.name} -> ${row.b.name}`))

/////////////////////////////////////////////////
type Eval<Row,Result> = (row:Row) => Result

type Pick<T, K extends keyof T> = {
    [P in K]: T[P];
}

function sel<Row,Result>( r: Row, f : Eval<Row,Result> ) : Result {
    return f(r);
}

function sel2<
    Row,
    Result,
    Input extends { [name:string]:Eval<Row,Result> },
    >( r: Row, input: Input ) : {[k in keyof Input]:Result} | null {
    return null;
}

const row = {a:1, b:'c', c:2 }
const res1 = sel( row, r=> r.a+r.c )
const res2 = sel2( row, {x: r=>r.a, y: r=>r.b} )
if( res2 ){
    console.log( res2.x )
    console.log( res2.y )
}

type PropNames<T> = { [K in keyof T]: T[K] extends Function ? never : K }[keyof T];
type Props<T> = Pick<T, PropNames<T>>;

function sel3<
    Row,
    Result,
    Input extends { [name:string]:Eval<Row,Result> },
    Output extends {[K in keyof Input]:ReturnType<Input[K]>}
>( row: Row, input: Input ) : Output | null {
    const retz : Output = {} as Output;
    //const keyz = keyof Input;
    return retz;
}

const res3 = sel3( row, {x: r=>r.a, y: r=>r.b} )
if( res3 ){
    console.log( res3.x );
    console.log( res3.y );
}
