import { RemoteDataSource, NamedRemoteDataSource } from './RemoteDataSource';
import { Foo } from './model';
import * as esParser from "esprima"

// const f = (x:string)=>{ return x+1 };
// const s = f.toString();
// console.log( s );
// console.log( esParser.parseScript( s ) )

const foo = new NamedRemoteDataSource<Foo>( 'http://localhost:19500/api', 'foo' )
// console.log( 'fetch()' )
// foo.fetch( r=>console.log(r) )

console.log( 'where()' )
foo.where( r=>r.id==1 )
   .fetch( r=>console.log(r) )