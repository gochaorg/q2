package xyz.cofe.q2

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test

class CompileEsPrimaTest1 {
    String getTest1JsonSource(){
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

    JsonNode getTest1JsonNode(){
        ObjectMapper om = new ObjectMapper()
        om.readTree test1JsonSource
    }

    boolean compileExpression( Appendable out, Map exp ){
        if( exp.type == 'BinaryExpression'
        &&  exp.operator != null
        &&  exp.left != null
        &&  exp.right != null
        ){
            StringWriter left = new StringWriter()
            if( !compileExpression(left, exp.left) ){
                out.println "/* left expression compile fail */"
                return false
            }

            StringWriter right = new StringWriter()
            if( !compileExpression(right, exp.right) ){
                out.println "/* right expression compile fail */"
                return false
            }

            switch( exp.operator ){
                case ~/==|<=|>=|!=|<|>|\+|-|\*|\//:
                    out << left << ' ' << exp.operator << ' ' << right
                    break
                default:
                    out.println "/* undefined binary operator: $exp.operator */"
                    return false
            }
            return true
        }

        if( exp.type=='MemberExpression' ){
            if( exp.object==null ){
                out.println "/* not defined object of MemberExpression */"
                return false
            }
            if( exp.object.type!='Identifier' ){
                out.println "/* object.type !='Identifier' of MemberExpression */"
                return false
            }
            if( !(exp.object.name instanceof String) ){
                out.println "/* object.name not string of MemberExpression */"
                return false
            }
            if( exp.property==null ){
                out.println "/* not defined property of MemberExpression */"
                return false
            }
            if( exp.property.type!='Identifier' ){
                out.println "/* property.type !='Identifier' of MemberExpression */"
                return false
            }
            if( !(exp.property.name instanceof String) ){
                out.println "/* property.name not string of MemberExpression */"
                return false
            }
            out << "${exp.object.name}.${exp.property.name}"
            return true
        }

        if( exp.type == 'Literal' ){
            if( exp.value != null ){
                out << exp.value
                return true
            }
            out.println "/* undefined Literal value */"
            return false
        }

        out.println "/* expression unknow */"
        return false
    }

    boolean compileArrowFn( Appendable out, Map arrowFn ){
        out.println "/* arrow fn detected */"
        out << "{ "

        // compile params
        boolean paramsCompileSucc = true
        out << arrowFn.params?.collect { param ->
            if( param.type == 'Identifier' && param.name ){
                return param.name
            }else{
                paramsCompileSucc = false
            }
        }.join(', ')

        if( !paramsCompileSucc )return false

        out << " -> \n"

        if( arrowFn.body==null ){
            out.println "/* fail - body not defined */"
            return false
        }

        if( !compileExpression(out,arrowFn.body) ){
            out.println "/* fail - body not compiled */"
            return false
        }

        out.println ''
        out.println "}"
        return true
    }

    boolean compile( Appendable out, Map m ){
        if( m.type == 'Program'
        &&  m.body?.get(0)?.type == 'ExpressionStatement'
        &&  m.body?.get(0)?.expression?.type == 'ArrowFunctionExpression'
        ){
            def arrowFn = m.body?.get(0)?.expression
            if( !compileArrowFn( out, arrowFn ) ){
                out.println "/* fail compile arrow fn body */"
                return false
            }

            return true
        }

        return false
    }

    @Test
    void test1(){
        JsonNode node = test1JsonNode
        println "node $node"

        ObjectMapper om = new ObjectMapper()
        def map = om.treeToValue(node, Map)
        println "map $map"

        if( map.filter ){
            StringWriter out = new StringWriter()
            if( compile(out, map.filter) ){
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
