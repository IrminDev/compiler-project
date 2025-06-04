package com.github.irmindev.model.statements;

public interface StatementVisitor<T> {
    T visit(StatementBlock statement);
    T visit(StatementBoolean statement);
    T visit(StatementChar statement);
    T visit(StatementDouble statement);
    T visit(StatementExpression statement);
    T visit(StatementFunction statement);
    T visit(StatementIf statement);
    T visit(StatementInteger statement);
    T visit(StatementLoop statement);
    T visit(StatementPrint statement);
    T visit(StatementReturn statement);
    T visit(StatementString statement);
    T visit(StatementVar statement);
} 
