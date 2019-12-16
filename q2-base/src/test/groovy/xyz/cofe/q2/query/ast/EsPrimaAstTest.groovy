package xyz.cofe.q2.query.ast

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import xyz.cofe.q2.it.TreeStep

/**
 * Тестирование преобразования json / esprima в ast.
 */
class EsPrimaAstTest {
    def toMap( String jsonSource ){
        ObjectMapper om = new ObjectMapper()
        def jsonNode = om.readTree jsonSource
        return om.treeToValue(jsonNode, Map)
    }

    /**
     * Примитивный оператор сложения <br>
     *
     * <code>
     *     JSON.stringify( es.parseScript( (<b>()=>1+2</b>).toString() ),null,2 )
     * </code>
     * @return json source
     */
    String getSrc1(){
        '{\n' +
            '  "type": "Program",\n' +
            '  "body": [\n' +
            '    {\n' +
            '      "type": "ExpressionStatement",\n' +
            '      "expression": {\n' +
            '        "type": "ArrowFunctionExpression",\n' +
            '        "id": null,\n' +
            '        "params": [],\n' +
            '        "body": {\n' +
            '          "type": "BinaryExpression",\n' +
            '          "operator": "+",\n' +
            '          "left": {\n' +
            '            "type": "Literal",\n' +
            '            "value": 1,\n' +
            '            "raw": "1"\n' +
            '          },\n' +
            '          "right": {\n' +
            '            "type": "Literal",\n' +
            '            "value": 2,\n' +
            '            "raw": "2"\n' +
            '          }\n' +
            '        },\n' +
            '        "generator": false,\n' +
            '        "expression": true,\n' +
            '        "async": false\n' +
            '      }\n' +
            '    }\n' +
            '  ],\n' +
            '  "sourceType": "script"\n' +
            '}'
    }

    @Test
    void test01(){
        def ast = new EsPrimaAst().parse( toMap(src1) )
        assert ast!=null

        ast.visit { TreeStep<ASTNode> ts ->
            println ".."*ts.level+" "+ts.node
        }
    }

    /**
     * Доступ к свойству <br>
     *
     * <code>
     *     JSON.stringify( es.parseScript( (<b>()=>1+a.b.c</b>).toString() ),null,2 )
     *     </code>
     * @return json source
     */
    String getSrc2(){
        '{\n' +
            '  "type": "Program",\n' +
            '  "body": [\n' +
            '    {\n' +
            '      "type": "ExpressionStatement",\n' +
            '      "expression": {\n' +
            '        "type": "ArrowFunctionExpression",\n' +
            '        "id": null,\n' +
            '        "params": [],\n' +
            '        "body": {\n' +
            '          "type": "BinaryExpression",\n' +
            '          "operator": "+",\n' +
            '          "left": {\n' +
            '            "type": "Literal",\n' +
            '            "value": 1,\n' +
            '            "raw": "1"\n' +
            '          },\n' +
            '          "right": {\n' +
            '            "type": "MemberExpression",\n' +
            '            "computed": false,\n' +
            '            "object": {\n' +
            '              "type": "MemberExpression",\n' +
            '              "computed": false,\n' +
            '              "object": {\n' +
            '                "type": "Identifier",\n' +
            '                "name": "a"\n' +
            '              },\n' +
            '              "property": {\n' +
            '                "type": "Identifier",\n' +
            '                "name": "b"\n' +
            '              }\n' +
            '            },\n' +
            '            "property": {\n' +
            '              "type": "Identifier",\n' +
            '              "name": "c"\n' +
            '            }\n' +
            '          }\n' +
            '        },\n' +
            '        "generator": false,\n' +
            '        "expression": true,\n' +
            '        "async": false\n' +
            '      }\n' +
            '    }\n' +
            '  ],\n' +
            '  "sourceType": "script"\n' +
            '}'
    }

    @Test
    void test02(){
        def ast = new EsPrimaAst().parse( toMap(src2) )
        assert ast!=null

        ast.visit { TreeStep<ASTNode> ts ->
            println ".."*ts.level+" "+ts.node
        }
    }

    /**
     * Вызов функции <br>
     *
     * <code>
     *     JSON.stringify( es.parseScript( (<b>()=>1+a(b).c</b> ).toString() ),null,2 )
     * </code>
     * @return json source
     */
    String getSrc3(){
        '{\n' +
            '  "type": "Program",\n' +
            '  "body": [\n' +
            '    {\n' +
            '      "type": "ExpressionStatement",\n' +
            '      "expression": {\n' +
            '        "type": "ArrowFunctionExpression",\n' +
            '        "id": null,\n' +
            '        "params": [],\n' +
            '        "body": {\n' +
            '          "type": "BinaryExpression",\n' +
            '          "operator": "+",\n' +
            '          "left": {\n' +
            '            "type": "Literal",\n' +
            '            "value": 1,\n' +
            '            "raw": "1"\n' +
            '          },\n' +
            '          "right": {\n' +
            '            "type": "MemberExpression",\n' +
            '            "computed": false,\n' +
            '            "object": {\n' +
            '              "type": "CallExpression",\n' +
            '              "callee": {\n' +
            '                "type": "Identifier",\n' +
            '                "name": "a"\n' +
            '              },\n' +
            '              "arguments": [\n' +
            '                {\n' +
            '                  "type": "Identifier",\n' +
            '                  "name": "b"\n' +
            '                }\n' +
            '              ]\n' +
            '            },\n' +
            '            "property": {\n' +
            '              "type": "Identifier",\n' +
            '              "name": "c"\n' +
            '            }\n' +
            '          }\n' +
            '        },\n' +
            '        "generator": false,\n' +
            '        "expression": true,\n' +
            '        "async": false\n' +
            '      }\n' +
            '    }\n' +
            '  ],\n' +
            '  "sourceType": "script"\n' +
            '}'
    }

    @Test
    void test03(){
        def ast = new EsPrimaAst().parse( toMap(src3) )
        assert ast!=null

        ast.visit { TreeStep<ASTNode> ts ->
            println ".."*ts.level+" "+ts.node
        }
    }

    /**
     * 1 аргумент и return <br>
     *
     * <code>
     * JSON.stringify( es.parseScript( ((a)=>{ return 1+2; } ).toString() ),null,2 )
     * </code>
     * @return
     */
    String getSrc4(){
        '{\n' +
            '  "type": "Program",\n' +
            '  "body": [\n' +
            '    {\n' +
            '      "type": "ExpressionStatement",\n' +
            '      "expression": {\n' +
            '        "type": "ArrowFunctionExpression",\n' +
            '        "id": null,\n' +
            '        "params": [\n' +
            '          {\n' +
            '            "type": "Identifier",\n' +
            '            "name": "a"\n' +
            '          }\n' +
            '        ],\n' +
            '        "body": {\n' +
            '          "type": "BlockStatement",\n' +
            '          "body": [\n' +
            '            {\n' +
            '              "type": "ReturnStatement",\n' +
            '              "argument": {\n' +
            '                "type": "BinaryExpression",\n' +
            '                "operator": "+",\n' +
            '                "left": {\n' +
            '                  "type": "Literal",\n' +
            '                  "value": 1,\n' +
            '                  "raw": "1"\n' +
            '                },\n' +
            '                "right": {\n' +
            '                  "type": "Literal",\n' +
            '                  "value": 2,\n' +
            '                  "raw": "2"\n' +
            '                }\n' +
            '              }\n' +
            '            }\n' +
            '          ]\n' +
            '        },\n' +
            '        "generator": false,\n' +
            '        "expression": false,\n' +
            '        "async": false\n' +
            '      }\n' +
            '    }\n' +
            '  ],\n' +
            '  "sourceType": "script"\n' +
            '}'
    }

    @Test
    void test04(){
        def ast = new EsPrimaAst().parse( toMap(src4) )
        assert ast!=null

        ast.visit { TreeStep<ASTNode> ts ->
            println ".."*ts.level+" "+ts.node
        }
    }

    /**
     * 2 аргумента и if <br>
     *
     * <code>
     * JSON.stringify( es.parseScript( ((a,b)=>{ if( a>b ){ return 1+2; } return a+b; } ).toString() ),null,2 )
     * </code>
     * @return
     */
    String getSrc5(){
        '{\n' +
            '  "type": "Program",\n' +
            '  "body": [\n' +
            '    {\n' +
            '      "type": "ExpressionStatement",\n' +
            '      "expression": {\n' +
            '        "type": "ArrowFunctionExpression",\n' +
            '        "id": null,\n' +
            '        "params": [\n' +
            '          {\n' +
            '            "type": "Identifier",\n' +
            '            "name": "a"\n' +
            '          },\n' +
            '          {\n' +
            '            "type": "Identifier",\n' +
            '            "name": "b"\n' +
            '          }\n' +
            '        ],\n' +
            '        "body": {\n' +
            '          "type": "BlockStatement",\n' +
            '          "body": [\n' +
            '            {\n' +
            '              "type": "IfStatement",\n' +
            '              "test": {\n' +
            '                "type": "BinaryExpression",\n' +
            '                "operator": ">",\n' +
            '                "left": {\n' +
            '                  "type": "Identifier",\n' +
            '                  "name": "a"\n' +
            '                },\n' +
            '                "right": {\n' +
            '                  "type": "Identifier",\n' +
            '                  "name": "b"\n' +
            '                }\n' +
            '              },\n' +
            '              "consequent": {\n' +
            '                "type": "BlockStatement",\n' +
            '                "body": [\n' +
            '                  {\n' +
            '                    "type": "ReturnStatement",\n' +
            '                    "argument": {\n' +
            '                      "type": "BinaryExpression",\n' +
            '                      "operator": "+",\n' +
            '                      "left": {\n' +
            '                        "type": "Literal",\n' +
            '                        "value": 1,\n' +
            '                        "raw": "1"\n' +
            '                      },\n' +
            '                      "right": {\n' +
            '                        "type": "Literal",\n' +
            '                        "value": 2,\n' +
            '                        "raw": "2"\n' +
            '                      }\n' +
            '                    }\n' +
            '                  }\n' +
            '                ]\n' +
            '              },\n' +
            '              "alternate": null\n' +
            '            },\n' +
            '            {\n' +
            '              "type": "ReturnStatement",\n' +
            '              "argument": {\n' +
            '                "type": "BinaryExpression",\n' +
            '                "operator": "+",\n' +
            '                "left": {\n' +
            '                  "type": "Identifier",\n' +
            '                  "name": "a"\n' +
            '                },\n' +
            '                "right": {\n' +
            '                  "type": "Identifier",\n' +
            '                  "name": "b"\n' +
            '                }\n' +
            '              }\n' +
            '            }\n' +
            '          ]\n' +
            '        },\n' +
            '        "generator": false,\n' +
            '        "expression": false,\n' +
            '        "async": false\n' +
            '      }\n' +
            '    }\n' +
            '  ],\n' +
            '  "sourceType": "script"\n' +
            '}'
    }

    @Test
    void test05(){
        def ast = new EsPrimaAst().parse( toMap(src5) )
        assert ast!=null

        ast.visit { TreeStep<ASTNode> ts ->
            println ".|"*ts.level+" "+ts.node
        }
    }

    /**
     * JSON.stringify( es.parseScript( ((a,b)=> a<b ? 1 : (a==b ? 2 : (a===b ? 3 : (a<=b ? 4 : (a>=b ? 5 : (a>b ? 6 : (a!=b ? 7 : -1)))))) ).toString() ),null,2 )
     * @return
     */
    String getSrc6(){
        '{\n' +
            '  "type": "Program",\n' +
            '  "body": [\n' +
            '    {\n' +
            '      "type": "ExpressionStatement",\n' +
            '      "expression": {\n' +
            '        "type": "ArrowFunctionExpression",\n' +
            '        "id": null,\n' +
            '        "params": [\n' +
            '          {\n' +
            '            "type": "Identifier",\n' +
            '            "name": "a"\n' +
            '          },\n' +
            '          {\n' +
            '            "type": "Identifier",\n' +
            '            "name": "b"\n' +
            '          }\n' +
            '        ],\n' +
            '        "body": {\n' +
            '          "type": "ConditionalExpression",\n' +
            '          "test": {\n' +
            '            "type": "BinaryExpression",\n' +
            '            "operator": "<",\n' +
            '            "left": {\n' +
            '              "type": "Identifier",\n' +
            '              "name": "a"\n' +
            '            },\n' +
            '            "right": {\n' +
            '              "type": "Identifier",\n' +
            '              "name": "b"\n' +
            '            }\n' +
            '          },\n' +
            '          "consequent": {\n' +
            '            "type": "Literal",\n' +
            '            "value": 1,\n' +
            '            "raw": "1"\n' +
            '          },\n' +
            '          "alternate": {\n' +
            '            "type": "ConditionalExpression",\n' +
            '            "test": {\n' +
            '              "type": "BinaryExpression",\n' +
            '              "operator": "==",\n' +
            '              "left": {\n' +
            '                "type": "Identifier",\n' +
            '                "name": "a"\n' +
            '              },\n' +
            '              "right": {\n' +
            '                "type": "Identifier",\n' +
            '                "name": "b"\n' +
            '              }\n' +
            '            },\n' +
            '            "consequent": {\n' +
            '              "type": "Literal",\n' +
            '              "value": 2,\n' +
            '              "raw": "2"\n' +
            '            },\n' +
            '            "alternate": {\n' +
            '              "type": "ConditionalExpression",\n' +
            '              "test": {\n' +
            '                "type": "BinaryExpression",\n' +
            '                "operator": "===",\n' +
            '                "left": {\n' +
            '                  "type": "Identifier",\n' +
            '                  "name": "a"\n' +
            '                },\n' +
            '                "right": {\n' +
            '                  "type": "Identifier",\n' +
            '                  "name": "b"\n' +
            '                }\n' +
            '              },\n' +
            '              "consequent": {\n' +
            '                "type": "Literal",\n' +
            '                "value": 3,\n' +
            '                "raw": "3"\n' +
            '              },\n' +
            '              "alternate": {\n' +
            '                "type": "ConditionalExpression",\n' +
            '                "test": {\n' +
            '                  "type": "BinaryExpression",\n' +
            '                  "operator": "<=",\n' +
            '                  "left": {\n' +
            '                    "type": "Identifier",\n' +
            '                    "name": "a"\n' +
            '                  },\n' +
            '                  "right": {\n' +
            '                    "type": "Identifier",\n' +
            '                    "name": "b"\n' +
            '                  }\n' +
            '                },\n' +
            '                "consequent": {\n' +
            '                  "type": "Literal",\n' +
            '                  "value": 4,\n' +
            '                  "raw": "4"\n' +
            '                },\n' +
            '                "alternate": {\n' +
            '                  "type": "ConditionalExpression",\n' +
            '                  "test": {\n' +
            '                    "type": "BinaryExpression",\n' +
            '                    "operator": ">=",\n' +
            '                    "left": {\n' +
            '                      "type": "Identifier",\n' +
            '                      "name": "a"\n' +
            '                    },\n' +
            '                    "right": {\n' +
            '                      "type": "Identifier",\n' +
            '                      "name": "b"\n' +
            '                    }\n' +
            '                  },\n' +
            '                  "consequent": {\n' +
            '                    "type": "Literal",\n' +
            '                    "value": 5,\n' +
            '                    "raw": "5"\n' +
            '                  },\n' +
            '                  "alternate": {\n' +
            '                    "type": "ConditionalExpression",\n' +
            '                    "test": {\n' +
            '                      "type": "BinaryExpression",\n' +
            '                      "operator": ">",\n' +
            '                      "left": {\n' +
            '                        "type": "Identifier",\n' +
            '                        "name": "a"\n' +
            '                      },\n' +
            '                      "right": {\n' +
            '                        "type": "Identifier",\n' +
            '                        "name": "b"\n' +
            '                      }\n' +
            '                    },\n' +
            '                    "consequent": {\n' +
            '                      "type": "Literal",\n' +
            '                      "value": 6,\n' +
            '                      "raw": "6"\n' +
            '                    },\n' +
            '                    "alternate": {\n' +
            '                      "type": "ConditionalExpression",\n' +
            '                      "test": {\n' +
            '                        "type": "BinaryExpression",\n' +
            '                        "operator": "!=",\n' +
            '                        "left": {\n' +
            '                          "type": "Identifier",\n' +
            '                          "name": "a"\n' +
            '                        },\n' +
            '                        "right": {\n' +
            '                          "type": "Identifier",\n' +
            '                          "name": "b"\n' +
            '                        }\n' +
            '                      },\n' +
            '                      "consequent": {\n' +
            '                        "type": "Literal",\n' +
            '                        "value": 7,\n' +
            '                        "raw": "7"\n' +
            '                      },\n' +
            '                      "alternate": {\n' +
            '                        "type": "UnaryExpression",\n' +
            '                        "operator": "-",\n' +
            '                        "argument": {\n' +
            '                          "type": "Literal",\n' +
            '                          "value": 1,\n' +
            '                          "raw": "1"\n' +
            '                        },\n' +
            '                        "prefix": true\n' +
            '                      }\n' +
            '                    }\n' +
            '                  }\n' +
            '                }\n' +
            '              }\n' +
            '            }\n' +
            '          }\n' +
            '        },\n' +
            '        "generator": false,\n' +
            '        "expression": true,\n' +
            '        "async": false\n' +
            '      }\n' +
            '    }\n' +
            '  ],\n' +
            '  "sourceType": "script"\n' +
            '}'
    }

    @Test
    void test06(){
        def ast = new EsPrimaAst().parse( toMap(src6) )
        assert ast!=null

        ast.visit { TreeStep<ASTNode> ts ->
            println ".|"*ts.level+" "+ts.node
        }
    }

    /**
     * JSON.stringify( es.parseScript( ((a,b,c)=>( a || b && !(c) ) ? 1 : 0  ).toString() ),null,2 )
     * @return
     */
    String getSrc7(){
        '{\n' +
            '  "type": "Program",\n' +
            '  "body": [\n' +
            '    {\n' +
            '      "type": "ExpressionStatement",\n' +
            '      "expression": {\n' +
            '        "type": "ArrowFunctionExpression",\n' +
            '        "id": null,\n' +
            '        "params": [\n' +
            '          {\n' +
            '            "type": "Identifier",\n' +
            '            "name": "a"\n' +
            '          },\n' +
            '          {\n' +
            '            "type": "Identifier",\n' +
            '            "name": "b"\n' +
            '          },\n' +
            '          {\n' +
            '            "type": "Identifier",\n' +
            '            "name": "c"\n' +
            '          }\n' +
            '        ],\n' +
            '        "body": {\n' +
            '          "type": "ConditionalExpression",\n' +
            '          "test": {\n' +
            '            "type": "LogicalExpression",\n' +
            '            "operator": "||",\n' +
            '            "left": {\n' +
            '              "type": "Identifier",\n' +
            '              "name": "a"\n' +
            '            },\n' +
            '            "right": {\n' +
            '              "type": "LogicalExpression",\n' +
            '              "operator": "&&",\n' +
            '              "left": {\n' +
            '                "type": "Identifier",\n' +
            '                "name": "b"\n' +
            '              },\n' +
            '              "right": {\n' +
            '                "type": "UnaryExpression",\n' +
            '                "operator": "!",\n' +
            '                "argument": {\n' +
            '                  "type": "Identifier",\n' +
            '                  "name": "c"\n' +
            '                },\n' +
            '                "prefix": true\n' +
            '              }\n' +
            '            }\n' +
            '          },\n' +
            '          "consequent": {\n' +
            '            "type": "Literal",\n' +
            '            "value": 1,\n' +
            '            "raw": "1"\n' +
            '          },\n' +
            '          "alternate": {\n' +
            '            "type": "Literal",\n' +
            '            "value": 0,\n' +
            '            "raw": "0"\n' +
            '          }\n' +
            '        },\n' +
            '        "generator": false,\n' +
            '        "expression": true,\n' +
            '        "async": false\n' +
            '      }\n' +
            '    }\n' +
            '  ],\n' +
            '  "sourceType": "script"\n' +
            '}'
    }

    @Test
    void test07(){
        def ast = new EsPrimaAst().parse( toMap(src7) )
        assert ast!=null

        ast.visit { TreeStep<ASTNode> ts ->
            println ".|"*ts.level+" "+ts.node
        }
    }

    /**
     *  JSON.stringify( es.parseScript( ((a,b,c)=>{ if( a || b ) { return 1 } else { return -1 } }  ).toString() ),null,2 )
     * @return
     */
    String getSrc8(){
        '{\n' +
            '  "type": "Program",\n' +
            '  "body": [\n' +
            '    {\n' +
            '      "type": "ExpressionStatement",\n' +
            '      "expression": {\n' +
            '        "type": "ArrowFunctionExpression",\n' +
            '        "id": null,\n' +
            '        "params": [\n' +
            '          {\n' +
            '            "type": "Identifier",\n' +
            '            "name": "a"\n' +
            '          },\n' +
            '          {\n' +
            '            "type": "Identifier",\n' +
            '            "name": "b"\n' +
            '          },\n' +
            '          {\n' +
            '            "type": "Identifier",\n' +
            '            "name": "c"\n' +
            '          }\n' +
            '        ],\n' +
            '        "body": {\n' +
            '          "type": "BlockStatement",\n' +
            '          "body": [\n' +
            '            {\n' +
            '              "type": "IfStatement",\n' +
            '              "test": {\n' +
            '                "type": "LogicalExpression",\n' +
            '                "operator": "||",\n' +
            '                "left": {\n' +
            '                  "type": "Identifier",\n' +
            '                  "name": "a"\n' +
            '                },\n' +
            '                "right": {\n' +
            '                  "type": "Identifier",\n' +
            '                  "name": "b"\n' +
            '                }\n' +
            '              },\n' +
            '              "consequent": {\n' +
            '                "type": "BlockStatement",\n' +
            '                "body": [\n' +
            '                  {\n' +
            '                    "type": "ReturnStatement",\n' +
            '                    "argument": {\n' +
            '                      "type": "Literal",\n' +
            '                      "value": 1,\n' +
            '                      "raw": "1"\n' +
            '                    }\n' +
            '                  }\n' +
            '                ]\n' +
            '              },\n' +
            '              "alternate": {\n' +
            '                "type": "BlockStatement",\n' +
            '                "body": [\n' +
            '                  {\n' +
            '                    "type": "ReturnStatement",\n' +
            '                    "argument": {\n' +
            '                      "type": "UnaryExpression",\n' +
            '                      "operator": "-",\n' +
            '                      "argument": {\n' +
            '                        "type": "Literal",\n' +
            '                        "value": 1,\n' +
            '                        "raw": "1"\n' +
            '                      },\n' +
            '                      "prefix": true\n' +
            '                    }\n' +
            '                  }\n' +
            '                ]\n' +
            '              }\n' +
            '            }\n' +
            '          ]\n' +
            '        },\n' +
            '        "generator": false,\n' +
            '        "expression": false,\n' +
            '        "async": false\n' +
            '      }\n' +
            '    }\n' +
            '  ],\n' +
            '  "sourceType": "script"\n' +
            '}'
    }

    @Test
    void test08(){
        def ast = new EsPrimaAst().parse( toMap(src8) )
        assert ast!=null

        ast.tree.each { ts ->
            println ".|"*ts.level+" "+ts.node
        }
    }
}
