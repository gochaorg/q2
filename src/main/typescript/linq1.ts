import { RemoteDataSource, NamedRemoteDataSource } from './RemoteDataSource';
import { Foo, Bar } from './model';
import * as esParser from "esprima"

// const f = (x:string)=>{ return x+1 };
// const s = f.toString();
// console.log( s );
// console.log( esParser.parseScript( s ) )

const foo = new NamedRemoteDataSource<Foo>( 'http://localhost:19500/api', 'foo' )
const bar = new NamedRemoteDataSource<Bar>( 'http://localhost:19500/api', 'bar' )
// console.log( 'fetch()' )
// foo.fetch( r=>console.log(r) )

// console.log( 'where()' )
// foo.where( r=>r.id==1 )
//    .fetch( r=>console.log(r) )

// console.log('join()')
// foo
//    .where( foo=> foo.id==1 )
//    .join( bar, (foo,bar)=> foo.id==bar.fooId )
//    .fetch( r=>
//       console.log( `${r.a.name} <- ${r.b.name}` )
//    )

console.log('select()')
foo.select({
   id: foo => foo.id,
   id2: foo => foo.id+foo.id,
   name: foo => foo.name,
   name2: foo => foo.name+foo.name
}).fetch( row=>{
   console.log( 'id=', row.id, ' id2=',row.id2, ' name=',row.name, ' name2=',row.name2 )
})