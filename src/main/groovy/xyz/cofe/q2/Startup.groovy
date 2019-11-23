package xyz.cofe.q2

import static ratpack.groovy.Groovy.ratpack

ratpack {
    serverConfig {
        port 19500
    }
    handlers {
        get {
            render "Hello World!"
        }
        get(":name") {
            render "Hello $pathTokens.name!"
        }
    }
}
