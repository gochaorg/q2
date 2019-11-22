@Grapes([
  @Grab('io.ratpack:ratpack-groovy:1.6.1'),
  @Grab('org.slf4j:slf4j-simple:1.7.25')
])
import static ratpack.groovy.Groovy.ratpack

ratpack {
    serverConfig { conf ->
        conf.port( 19000 )
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