package com.shortcircuit.app;

import org.jetbrains.annotations.NotNull;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.J.ControlParentheses;

public class ShortCircuit extends Recipe {

    @Override
    public @NotNull String getDisplayName() {
        return "Short Circuit";
    }

    @Override
    public @NotNull String getDescription() {
        return "Change the | / & operator to || / && in if statements";
    }
    @Override
    protected @NotNull JavaIsoVisitor<ExecutionContext> getVisitor() {
        return new SayIfVisitor();
    }

    public static class SayIfVisitor extends JavaIsoVisitor<ExecutionContext> {
        @Override
        public J.@NotNull If visitIf(J.If ifStatement, @NotNull ExecutionContext context) {
            ControlParentheses<Expression> ifCondition = ifStatement.getIfCondition();
            // https://docs.openrewrite.org/concepts-and-explanations/lst-examples#expression
            Expression expression = ifCondition.getTree();
            // https://docs.openrewrite.org/concepts-and-explanations/lst-examples#binary
            J.Binary binary = (J.Binary) expression;
            J.Binary.Type operatorType = binary.getOperator();

            if (operatorType == J.Binary.Type.BitOr || operatorType == J.Binary.Type.BitAnd) {
                J.Binary binaryResult;
              switch (operatorType){
                  case BitOr -> binaryResult = binary.withOperator(J.Binary.Type.Or);
                  case BitAnd ->  binaryResult = binary.withOperator(J.Binary.Type.And);
                  default -> throw new IllegalStateException("Unexpected value: " + operatorType);
              }
                ControlParentheses<Expression> withTree = ifCondition.withTree(binaryResult);

                ifStatement = ifStatement.withIfCondition(withTree);
            }
            return ifStatement;
        }

    }
}