import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

//////////////////////////////////////////////////////
// BANNER
//////////////////////////////////////////////////////
println "== start groovy build script =="
println """ |project: 
            |  a: $project.artifactId
            |  g: $project.groupId
            |  v: $project.version
            |phase: $mojo.lifecyclePhase
            |basedir: $project.basedir
        """.stripMargin().trim()

//////////////////////////////////////////////////////
def ENV_PATH = System.getenv().PATH
def PATH = ENV_PATH.split(Pattern.quote(System.getProperty('pah.separator', ':')))

// Поиск exe файла в путях
def findExe = { String exeName ->
    if (exeName == null) throw new IllegalArgumentException("exeName == null");

    def exeFiles = PATH.collect { String dirPath ->
        File dir = new File(dirPath)
        if (!dir.isDirectory()) return null
        return dir
    }.findAll {
        it != null
    }.collect { File dir ->
        dir.listFiles().findAll { it.isFile() && it.canExecute() }
    }.flatten().findAll { File exeFile -> exeFile.name == exeName }

    return exeFiles.size() > 0 ? exeFiles[0] : null
}

// Выполнение программы
def exec(Map opts = [:], String ... cmd) {
    if (cmd == null) throw new IllegalArgumentException("cmd==null");
    ProcessBuilder pb = new ProcessBuilder()
    pb.command(cmd)

    if( opts.dir instanceof File ){
        pb.directory(opts.dir)
    }else if( opts.dir instanceof String ){
        pb.directory(new File(opts.dir))
    }

    def reader = { InputStream input, OutputStream output ->
        Thread th = new Thread({
            byte[] buff = new byte[1024]
            while (true){
                int readed = input.read(buff)
                if( readed<0 )break
                if( readed>0 ){
                    output.write(buff,0,readed)
                }
            }
            output.flush()
        },'processOutputReader')
        th.daemon = true
        th.start()
    }
    def unionOutput = { OutputStream out1, OutputStream out2 ->
        new OutputStream() {
            @Override
            void write(int b) throws IOException {
                out1.write(b)
                out2.write(b)
            }

            @Override
            void write(byte[] b, int off, int len) throws IOException {
                out1.write(b,off,len)
                out2.write(b,off,len)
            }

            @Override
            void flush() throws IOException {
                out1.flush()
                out2.flush()
            }
        }
    }

    Charset cs = opts.cs instanceof Charset ? opts.cs : (
        opts.cs instanceof String ? Charset.forName(opts.cs) : Charset.defaultCharset()
    )

    ByteArrayOutputStream outStd = new ByteArrayOutputStream()
    ByteArrayOutputStream outErr = new ByteArrayOutputStream()

    Process proc = pb.start()
    reader( proc.inputStream, unionOutput(outStd,System.out) )
    reader( proc.errorStream, unionOutput(outErr,System.err) )

    if( opts.wait instanceof Number ){
        if( opts.timeUnit instanceof TimeUnit ){
            proc.waitFor( ((Number)opts.wait).longValue(), opts.timeUnit as TimeUnit )
        }else{
            proc.waitFor( ((Number)opts.wait).longValue(), TimeUnit.SECONDS )
        }
    }else {
        proc.waitFor()
    }

    String output = new String(outStd.toByteArray(),cs)
    String errput = new String(outErr.toByteArray(),cs)

    return [
        exitValue: proc.exitValue(),
        output: output,
        errput: errput
    ]
}

/////////////////////////////////////////////////////
// Поиск npm
def npmExe = findExe('npm')?.toString()
if (npmExe == null) {
    throw new Error("npm not found")
}

// Поиск nodejs
def nodeExe = findExe('node')?.toString()
if (nodeExe == null) {
    throw new Error("node not found")
}

/////////////////////////////////////////////////////
// Установка пакетов nodejs
def npmInstall = {
    println " npm i ".center(40,'=')
    def status = exec(npmExe, 'i', dir: project.basedir)
    if( status.exitValue!=0 ){
        throw new Error("npm return error code $status.exitValue")
    }
}

// Компиляция typescript
def compileTs = {
    println " compile ts ".center(40,'=')
    def status = exec( nodeExe, './node_modules/.bin/tsc', dir: project.basedir)
    if( status.exitValue!=0 ){
        throw new Error("tsc return error code $status.exitValue")
    }
}

npmInstall()
compileTs()