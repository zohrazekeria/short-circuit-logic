package com.shortcircuit.app;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.J.ControlParentheses;
import org.openrewrite.java.tree.JavaType;

public class ShortCircuit extends Recipe {

    @Override
    public String getDisplayName() {
        return "Short Circuit";
    }

    @Override
    public String getDescription() {
        return "Change the | operator to || in if statements";
    }
    @Override
    protected JavaIsoVisitor<ExecutionContext> getVisitor() {
        return new SayIfVisitor();
    }

    public class SayIfVisitor extends JavaIsoVisitor<ExecutionContext> {

        @Override
        public J.If visitIf(J.If ifStatement, ExecutionContext context) {
            ControlParentheses<Expression> ifCondition = ifStatement.getIfCondition();

            Expression expression = ifCondition.getTree();
            JavaType type = expression.getType();

            System.out.println("Type: " + type);

            J.Binary binary = (J.Binary) expression;
            J.Binary.Type operatorType = binary.getOperator();

            if (operatorType == J.Binary.Type.BitOr) {

                J.Binary binaryResult = binary.withOperator(J.Binary.Type.Or);
                
                ControlParentheses<Expression> withTree = ifCondition.withTree((Expression) binaryResult);

                ifStatement = ifStatement.withIfCondition(withTree);
            }

            return ifStatement;
        }
    }

    public class SayBinaryVisitor extends JavaIsoVisitor<ExecutionContext> {
        
        @Override
        public J.Binary visitBinary(J.Binary binary, ExecutionContext context) {
            J.Binary.Type operator = binary.getOperator();
            if (operator == J.Binary.Type.BitOr) {
                return  binary.withOperator(J.Binary.Type.Or);
            }
            return binary;
        }
    }

}