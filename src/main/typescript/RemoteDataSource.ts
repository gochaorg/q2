import ax from "axios";
import { DataSource } from './DataSource';
import { fetchData } from "./Datatransfer"
import esParser from "esprima"

export interface Expr {
    compile():any
}

export class From implements Expr {
    readonly name:string
    constructor( name:string ){
        this.name = name
    }    
    compile(){
        return { type: 'From', name: this.name }
    }
    where(jsArrowFnSource:string):Expr{
        return new Where(this,jsArrowFnSource)
    }
}

export class Where implements Expr {
    readonly ds:Expr
    readonly jsArrowFnSource:string
    constructor( ds:Expr, jsArrowFnSource:string ){
        this.ds = ds
        this.jsArrowFnSource = jsArrowFnSource
    }
    compile() {
        return { 
            type: 'Where', 
            sourceCode: this.jsArrowFnSource,
            ast: esParser.parseScript( this.jsArrowFnSource ),
            dataSource: this.ds.compile()
        }
    }
}

export class RemoteDataSource<T> {

}

/**
 * Описывает манипуляции с однотипным источником данных
 */
export class NamedRemoteDataSource<T> implements DataSource<T> {
    readonly api:string
    readonly name:string    

    constructor(api:string,name:string){
        this.api = api
        this.name = name
    }

    expression():Expr {
        return new From(this.name)
    }

    /**
     * Прямое извлечение данных
     * @param consumer получатель данных
     */
    fetch( consumer:(row:T)=>any ){
        const payLoad = this.expression().compile()
        fetchData<T>( this.api, payLoad, 
            {
                ok: rows=>{
                    rows.forEach( row=>consumer(row) )
                }
            } 
        )
    }
}

class WhereDataSource<T> implements DataSource<T> {
    constructor( dataSource: DataSource<T> ){
    }

    fetch( consumer:(row:T)=>any ){
    }
}
