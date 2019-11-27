import { RemoteDataSource } from './RemoteDataSource';
import { Foo } from './model';

const foo = new RemoteDataSource<Foo>( 'http://localhost:19500/api', 'foo' )
foo.fetch( r=>console.log(r) )