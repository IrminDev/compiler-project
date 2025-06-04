package com.github.irmindev.model.expressions;

public interface ExpressionVisitor<T> {
    T visit(ExpressionArit expression);
    T visit(ExpressionAssign expression);
    T visit(ExpressionCallFunction expression);
    T visit(ExpressionGrouping expression);
    T visit(ExpressionLiteral expression);
    T visit(ExpressionLogical expression);
    T visit(ExpressionRelational expression);
    T visit(ExpressionUnary expression);
    T visit(ExpressionVariable expression);
}
