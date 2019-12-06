package xyz.cofe.q2.jetty

import org.junit.Test

class CmdLineTest {
    @Test
    void test01(){
        CmdLine cmdLine = new CmdLine("-k", "1")
        println cmdLine.k
        println ((cmdLine.port ?: 110011) as int)

        CmdLine cmdLine2 = new CmdLine("-port", "1334")
        println cmdLine2.k
        println ((cmdLine2.port ?: 110011) as int)
    }
}
