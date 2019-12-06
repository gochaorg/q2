println "== start groovy build script =="
println """ |project: 
            |  a: $project.artifactId
            |  g: $project.groupId
            |  v: $project.version
            |
            |phase: $mojo.lifecyclePhase
            |
            |dir: $project.basedir
        """.stripMargin().trim()
//println "project ${project.getClass()} $project"
//println "path: ${System.getenv().PATH}"

//println " properties ".center(40,'=')
//properties.sort { it.key }.each { k,v -> println "$k = $v"}

//println "$mojo"
//println "$mojo.lifecyclePhase"