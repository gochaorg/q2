import { Foo, Bar } from "./model"
import { DataSource } from "./DataSource"

let src1:Foo[] = [
    {id:1,name:"fooA"},
    {id:2,name:"fooB"},
    {id:3,name:"fooC"}
]

let src2:Bar[] = [
    new Bar( 3,"barA",1), new Bar( 4,"barB",1),
    new Bar( 5,"barC",2), new Bar( 6,"barD",2),
    new Bar( 7,"barE",1), new Bar( 8,"barF",5),
    new Bar( 9,"barG",1), new Bar(10,"barH",5),
    new Bar(11,"barI",2), new Bar(12,"barJ",3),
    ]

/**
 * Модель данных
 */
export class RootData {
    readonly foo = new DataSource(src1)
    readonly bar = new DataSource(src2)
}
