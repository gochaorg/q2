package xyz.cofe.q2

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import xyz.cofe.q2.query.EsPrimaCompiler

class CompileEsPrimaTest1 {
    String getUnaryFilterSource(){
        '''
        {
          "filter": {
            "type": "Program",
            "body": [{
              "type": "ExpressionStatement",
              "expression": {
                "type": "ArrowFunctionExpression",
                "id": null,
                "params": [{
                  "type": "Identifier",
                  "name": "r"
                }],
                "body": {
                  "type": "BinaryExpression",
                  "operator": "==",
                  "left": {
                    "type": "MemberExpression",
                    "computed": false,
                    "object": {
                      "type": "Identifier",
                      "name": "r"
                    },
                    "property": {
                      "type": "Identifier",
                      "name": "id"
                    }
                  },
                  "right": {
                    "type": "Literal",
                    "value": 1,
                    "raw": "1"
                  }
                },
                "generator": false,
                "expression": true,
                "async": false
              }
            }],
            "sourceType": "script"
          }
        }
        '''
    }
    JsonNode getUnaryFilterJsonNode(){
        ObjectMapper om = new ObjectMapper()
        om.readTree unaryFilterSource
    }

    @Test
    void unaryFilterTest(){
        JsonNode node = unaryFilterJsonNode
        println "node $node"

        ObjectMapper om = new ObjectMapper()
        def map = om.treeToValue(node, Map)
        println "map $map"

        if( map.filter instanceof Map ){
            StringWriter out = new StringWriter()

            EsPrimaCompiler compiler = new EsPrimaCompiler()
            if( compiler.compile(out, map.filter as Map) ){
                println "source success translated:"
                println out

                GroovyShell gs = new GroovyShell()
                def res = gs.evaluate(out.toString())
                println res
            }else{
                println "failure compile:"
                println out
            }
        }
    }

    String getBinaryFilterSource(){
        return '''
        {
            "filter": {
                "code": "(foo, bar) => foo.id == bar.fooId",
                "ast": {
                    "tree": {
                        "type": "Program",
                        "body": [{
                            "type": "ExpressionStatement",
                            "expression": {
                                "type": "ArrowFunctionExpression",
                                "id": null,
                                "params": [{
                                    "type": "Identifier",
                                    "name": "foo"
                                }, {
                                    "type": "Identifier",
                                    "name": "bar"
                                }],
                                "body": {
                                    "type": "BinaryExpression",
                                    "operator": "==",
                                    "left": {
                                        "type": "MemberExpression",
                                        "computed": false,
                                        "object": {
                                            "type": "Identifier",
                                            "name": "foo"
                                        },
                                        "property": {
                                            "type": "Identifier",
                                            "name": "id"
                                        }
                                    },
                                    "right": {
                                        "type": "MemberExpression",
                                        "computed": false,
                                        "object": {
                                            "type": "Identifier",
                                            "name": "bar"
                                        },
                                        "property": {
                                            "type": "Identifier",
                                            "name": "fooId"
                                        }
                                    }
                                },
                                "generator": false,
                                "expression": true,
                                "async": false
                            }
                        }],
                        "sourceType": "script"
                    },
                    "parser": "esprima"
                }
            }
        }
        '''
    }
    Map getBinaryFilterMap(){
        ObjectMapper om = new ObjectMapper()
        def jsonNode = om.readTree binaryFilterSource
        def map = om.treeToValue(jsonNode, Map)
        return map
    }

    @Test
    void binaryFilterTest(){
        println " binaryFilterTest ".center(40,'=')

        def map = binaryFilterMap
        println "map $map"

        if( map.filter?.ast?.tree ){
            StringWriter out = new StringWriter()

            EsPrimaCompiler compiler = new EsPrimaCompiler()
            if( compiler.compile(out, map.filter?.ast?.tree as Map) ){
                println "source success translated:"
                println out

                GroovyShell gs = new GroovyShell()
                def res = gs.evaluate(out.toString())
                println res
            }else{
                println "failure compile:"
                println out
            }
        }
    }
}
