package xyz.cofe.q2

import org.junit.Test
import jdk.nashorn.api.tree.*

class NashornParserTest1 {
    final String js = "( r ) => r.id == 1"
    @Test
    public void test01(){
        def parser = Parser.create('--language=es6')
        def compUnit = parser.parse('test.js', js, new DiagnosticListener() {
            @Override
            void report( Diagnostic diagnostic ){
                println "diagnostic: $diagnostic"
            }
        })
        println compUnit
        if( compUnit!=null ){
            SimpleTreeVisitorES6 visitor = new SimpleTreeVisitorES6(){
                @Override
                Object visitCompilationUnit( CompilationUnitTree node, Object r ){
                    println "CompilationUnit $node"
                }

                @Override
                Object visitModule( ModuleTree node, Object o ){
                    println "Module $node"
                }

                @Override
                Object visitExportEntry( ExportEntryTree node, Object o ){
                    println "ExportEntry $node"
                }

                @Override
                Object visitImportEntry( ImportEntryTree node, Object o ){
                    println "ImportEntry $node"
                }

                @Override
                Object visitClassDeclaration( ClassDeclarationTree node, Object o ){
                    println "ClassDeclaration $node"
                }

                @Override
                Object visitClassExpression( ClassExpressionTree node, Object o ){
                    println "ClassExpression $node"
                }

                @Override
                Object visitForOfLoop( ForOfLoopTree node, Object o ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitYield( YieldTree node, Object o ){
                    println "Yield $node"
                }

                @Override
                Object visitSpread( SpreadTree node, Object o ){
                    println "Spread $node"
                }

                @Override
                Object visitTemplateLiteral( TemplateLiteralTree node, Object o ){
                    println "TemplateLiteral $node"
                }

                @Override
                Object visitVariable( VariableTree node, Object r ){
                    println "Variable $node"
                }

                @Override
                Object visitAssignment( AssignmentTree node, Object r ){
                    println "Assignment $node"
                }

                @Override
                Object visitCompoundAssignment( CompoundAssignmentTree node, Object r ){
                    println "CompoundAssignment $node"
                }

                @Override
                Object visitBinary( BinaryTree node, Object r ){
                    println "Binary $node"
                }

                @Override
                Object visitBlock( BlockTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitBreak( BreakTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitCase( CaseTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitCatch( CatchTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitConditionalExpression( ConditionalExpressionTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitContinue( ContinueTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitDebugger( DebuggerTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitDoWhileLoop( DoWhileLoopTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitErroneous( ErroneousTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitExpressionStatement( ExpressionStatementTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitForLoop( ForLoopTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitForInLoop( ForInLoopTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitFunctionCall( FunctionCallTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitFunctionDeclaration( FunctionDeclarationTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitFunctionExpression( FunctionExpressionTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitIdentifier( IdentifierTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitIf( IfTree node, Object r ){
                    println "ForOfLoop $node"
                }

                @Override
                Object visitArrayAccess( ArrayAccessTree node, Object r ){
                    println "ArrayAccess $node"
                }

                @Override
                Object visitArrayLiteral( ArrayLiteralTree node, Object r ){
                    println "ArrayLiteral $node"
                }

                @Override
                Object visitLabeledStatement( LabeledStatementTree node, Object r ){
                    println "LabeledStatement $node"
                }

                @Override
                Object visitLiteral( LiteralTree node, Object r ){
                    println "Literal $node"
                }

                @Override
                Object visitParenthesized( ParenthesizedTree node, Object r ){
                    println "Parenthesized $node"
                }

                @Override
                Object visitReturn( ReturnTree node, Object r ){
                    println "Return $node"
                }

                @Override
                Object visitMemberSelect( MemberSelectTree node, Object r ){
                    println "MemberSelect $node"
                }

                @Override
                Object visitNew( NewTree node, Object r ){
                    println "New $node"
                }

                @Override
                Object visitObjectLiteral( ObjectLiteralTree node, Object r ){
                    println "ObjectLiteral $node"
                }

                @Override
                Object visitProperty( PropertyTree node, Object r ){
                    println "Property $node"
                }

                @Override
                Object visitRegExpLiteral( RegExpLiteralTree node, Object r ){
                    println "RegExpLiteral $node"
                }

                @Override
                Object visitEmptyStatement( EmptyStatementTree node, Object r ){
                    println "EmptyStatement $node"
                }

                @Override
                Object visitSwitch( SwitchTree node, Object r ){
                    println "Switch $node"
                }

                @Override
                Object visitThrow( ThrowTree node, Object r ){
                    println "Throw $node"
                }

                @Override
                Object visitTry( TryTree node, Object r ){
                    println "Try $node"
                }

                @Override
                Object visitInstanceOf( InstanceOfTree node, Object r ){
                    println "InstanceOf $node"
                }

                @Override
                Object visitUnary( UnaryTree node, Object r ){
                    println "Unary $node"
                }

                @Override
                Object visitWhileLoop( WhileLoopTree node, Object r ){
                    println "WhileLoop $node"
                }

                @Override
                Object visitWith( WithTree node, Object r ){
                    println "With $node"
                }

                @Override
                Object visitUnknown( Tree node, Object o ){
                    println "Unknown $node"
                }
            }

            //compUnit.accept( visitor, null )
            visitor.visitCompilationUnit(compUnit, null)
        }
    }
}
