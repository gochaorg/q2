import { RemoteDataSource, NamedDataSource } from '../ds/remote/RemoteDataSource';
import { Foo, Bar } from '../model/model';
import * as esParser from "esprima"

RemoteDataSource.debug = true

const foo = new NamedDataSource<Foo>( 'http://localhost:19500/api', 'foo' )
const bar = new NamedDataSource<Bar>( 'http://localhost:19500/api', 'bar' )

// Демонстрация
async function demo() {
   console.log('== pick() ==')
   await foo.pick(row => console.log(row))

   console.log('== where() ==')
   await foo.where(row => row.id == 1)
       .pick(row => console.log(row))

   console.log('== join() ==')
   await foo
       .where(foo => foo.id == 1)
       .join(bar, (foo, bar) => foo.id == bar.fooId)
       .pick(row =>
           console.log(`${row.a.name} <- ${row.b.name}`)
       )

   console.log('== select() ==')
   await foo.select({
      id: foo => foo.id,
      id2: foo => foo.id + foo.id,
      name: foo => foo.name,
      name2: foo => foo.name + foo.name
   }).pick(row => {
      console.log('id=', row.id, ' id2=', row.id2, ' name=', row.name, ' name2=', row.name2)
   })
}

demo()