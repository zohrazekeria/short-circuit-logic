package com.shortcircuit.app;

import org.jetbrains.annotations.NotNull;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType;

public class ShortCircuit extends Recipe {

    @Override
    public @NotNull String getDisplayName() {
        return "Short Circuit";
    }

    @Override
    public @NotNull String getDescription() {
        return "Change the | / & operator to || / && when an expression has Boolean-like operand";
    }

    @Override
    protected @NotNull JavaIsoVisitor<ExecutionContext> getVisitor() {
        return new SayBinaryVisitor();
    }

    private boolean isBinaryBoolean(J.Binary binary) {
        JavaType leftType = binary.getLeft().getType();
        JavaType rightType = binary.getRight().getType();

        if (leftType != null && rightType != null) {
            return leftType.toString().equalsIgnoreCase("boolean") && rightType.toString().equalsIgnoreCase("boolean");
        }
        return false;
    }
    public class SayBinaryVisitor extends JavaIsoVisitor<ExecutionContext> {
        @Override
        public J.Binary visitBinary(J.Binary binary, ExecutionContext context) {
            if (isBinaryBoolean(binary)) {
                switch (binary.getOperator()) {
                    case BitOr -> {
                        return binary.withOperator(J.Binary.Type.Or);
                    }
                    case BitAnd -> {
                        return binary.withOperator(J.Binary.Type.And);
                    }
                    default -> {
                        return super.visitBinary(binary, context);
                    }
                }
            }

            return super.visitBinary(binary, context);
        }
    }
}
