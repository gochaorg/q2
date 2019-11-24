import * as parser from "esprima";
import { Foo } from './model';

const filter1 = ( r:Foo ) => r.id == 1;

console.log( 
    JSON.stringify(
        parser.parseScript( filter1.toString() )
    )
)