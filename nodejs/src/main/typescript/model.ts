export interface Foo {
    id : number
    name : string    
}

export interface Bar {
    id : number
    name : string
    fooId : number
}

////////////////////////////////////////////////
// Данная модель была до всяких телодвижений
// export class Foo {
//     constructor(id:number=-1, name:string='foo'){
//         this._id = id
//         this._name = name
//     }

//     _id : number = 0
//     get id():number { return this._id; }
//     set id(newval:number){ this._id = newval; }

//     _name : string = 'none'
//     get name():string { return this._name }
//     set name(v:string) { this._name = v }
// }

// export class Bar {
//     constructor(id:number=-1, name:string='foo', fooId:number=-2){
//         this._id = id
//         this._name = name
//         this._fooId = fooId
//     }

//     private _id : number = 0
//     get id():number { return this._id; }
//     set id(newval:number){ this._id = newval; }

//     private _name : string = 'none'
//     get name():string { return this._name }
//     set name(v:string) { this._name = v }

//     private _fooId : number = -1
//     get fooId():number { return this._fooId }
//     set fooId(fid:number){ this._fooId = fid }
// }
