package xyz.cofe.q2.query

/**
 * Компиляция ast дерева, парсер - esprima
 */
class EsPrimaCompiler {
    /**
     * Компиляция esprima дерева ast выражения (BinaryExpression, MemberExpression)
     * в аналогичное groovy
     * @param out код groovy
     * @param exp дерево ast
     * @return успешность компиляции
     */
    boolean compileExpression( Appendable out, Map exp ){
        if( out == null ) throw new IllegalArgumentException("out==null");
        if( exp == null ) throw new IllegalArgumentException("exp==null");

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

    /**
     * Компиляция esprima дерева ast выражения ArrowFunctionExpression
     * в аналогичное groovy
     * @param out код groovy
     * @param arrowFn дерево ast
     * @return успешность компиляции
     */
    boolean compileArrowFn( Appendable out, Map arrowFn ){
        if( out == null ) throw new IllegalArgumentException("out==null");
        if( arrowFn == null ) throw new IllegalArgumentException("arrowFn==null");

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

    /**
     * Копиляция esprima дерева ast
     * в аналогичное groovy
     * @param out код groovy
     * @param m дерево ast
     * @return успешность компиляции
     */
    boolean compile( Appendable out, Map m ){
        if( out == null ) throw new IllegalArgumentException("out==null");
        if( m == null ) throw new IllegalArgumentException("m==null");

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

    /**
     * Копиляция esprima дерева ast
     * в функцию java/groovy
     * @param esPrimaAst дерево ast
     * @return функция
     */
    Closure compile( Map esPrimaAst ){
        if( esPrimaAst == null ) throw new IllegalArgumentException("esPrimaAst==null");

        StringWriter groovyCode = new StringWriter()
        if( !compile(groovyCode,esPrimaAst) ){
            throw new Error("can't compile esPrimaAst")
        }

        GroovyShell gs = new GroovyShell()
        def res = gs.evaluate(groovyCode.toString())
        if( res instanceof Closure ){
            return res
        }

        throw new Error("GroovyShell return not Closure: $res")
    }
}
