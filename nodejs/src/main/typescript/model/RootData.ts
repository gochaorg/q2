import { Foo, Bar } from "./model"
import { LocalDataSource } from "../ds/local/LocalDataSource"

let src1:Foo[] = [
    {id:1,name:"fooA"},
    {id:2,name:"fooB"},
    {id:3,name:"fooC"}
]

let src2:Bar[] = [
    { id:3, name:"barA", fooId:1}, { id:4,name:"barB", fooId:1},
    { id:5,name:"barC", fooId:2}, { id:6,name:"barD", fooId:2},
    { id:7,name:"barE", fooId:1}, { id:8,name:"barF", fooId:5},
    { id:9,name:"barG", fooId:1}, {id:10,name:"barH", fooId:5},
    { id:11,name:"barI", fooId:2}, {id:12,name:"barJ", fooId:3},
    ]

/**
 * Модель данных
 */
export class RootData {
    readonly foo = new LocalDataSource(src1)
    readonly bar = new LocalDataSource(src2)
}
