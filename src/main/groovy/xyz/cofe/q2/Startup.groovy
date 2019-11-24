package xyz.cofe.q2

import xyz.cofe.q2.model.Foo

import static ratpack.groovy.Groovy.ratpack

ratpack {
    serverConfig {
        port 19500
    }
    handlers {
        get {
            render "Hello World!"
        }
//        get(":name") {
//            render "Hello $pathTokens.name!"
//        }
        get( "test" ){
            new JsonOut().write(response, Foo, RootData.instance.foo )
        }
    }
}
