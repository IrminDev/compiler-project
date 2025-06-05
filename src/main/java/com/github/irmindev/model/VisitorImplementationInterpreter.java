package com.github.irmindev.model;

import com.github.irmindev.model.expressions.Expression;
import com.github.irmindev.model.expressions.ExpressionArit;
import com.github.irmindev.model.expressions.ExpressionUnary;
import com.github.irmindev.model.expressions.ExpressionGrouping;
import com.github.irmindev.model.expressions.ExpressionLogical;
import com.github.irmindev.model.expressions.ExpressionLiteral;
import com.github.irmindev.model.expressions.ExpressionRelational;
import com.github.irmindev.model.expressions.ExpressionVisitor;
import com.github.irmindev.model.statements.StatementVisitor;

public class VisitorImplementationInterpreter implements ExpressionVisitor<Object>, StatementVisitor<Void> {
    private final Environment global;
    private final Environment environment;

    public VisitorImplementationInterpreter(Environment global, Environment environment) {
        this.global = global;
        this.environment = environment;
    }

    private Object evaluate(Expression expression) {
        return expression.accept(this);
    }
    
    @Override
    public Object visit(ExpressionUnary expr) {
        Object right = evaluate(expr.getRight());
    
        switch (expr.getOperator().getType()) {
            case MINUS: // '-'
                if (right instanceof Double) {
                    return -((Double) right);
                }
                throw new RuntimeException("El operando debe ser un número.");
            case BANG: // '!'
                return !isTruthy(right);
            default:
                throw new RuntimeException("Operador unario desconocido.");
        }
    }
    
    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (Boolean) object;
        return true;
    }
    
    @Override
    public Object visit(ExpressionArit expr) {
        Object left = evaluate(expr.getLeft());
        Object right = evaluate(expr.getRight());

        switch (expr.getOperator().getType()) {
            case PLUS: // '+'	
                if (left instanceof Double && right instanceof Double) {
                    return (Double) left + (Double) right;
                }
                throw new RuntimeException("Los operandos de '+' deben ser números.");
            case MINUS: // '-'
                if (left instanceof Double && right instanceof Double) {
                    return (Double) left - (Double) right;
                }
                throw new RuntimeException("Los operandos de '-' deben ser números.");
            case STAR: // '*'
                if (left instanceof Double && right instanceof Double) {
                    return (Double) left * (Double) right;
                }
                throw new RuntimeException("Los operandos de '*' deben ser números.");
            case SLASH: // '/'
                if (left instanceof Double && right instanceof Double) {
                    if ((Double) right == 0) {
                        throw new RuntimeException("División por cero.");
                    }
                    return (Double) left / (Double) right;
                }
                throw new RuntimeException("Los operandos de '/' deben ser números.");
            default:
                throw new RuntimeException("Operador aritmético desconocido.");
        }
    }

    @Override
    public Object visit(ExpressionGrouping expr) {
        return evaluate(expr.getExpression());
    }

    @Override
    public Object visit(ExpressionLogical expr) {
        Object left = evaluate(expr.getLeft());

        switch (expr.getOperator().getType()) {
            case OR: // '||'
                if (isTruthy(left)) return true;
                return isTruthy(evaluate(expr.getRight()));

            case AND: // '&&'
                if (!isTruthy(left)) return false;
                return isTruthy(evaluate(expr.getRight()));

            default:
                throw new RuntimeException("Operador lógico desconocido.");
        }
    }

    @Override
    public Object visit(ExpressionLiteral expr) {
        return expr.getValue();
    }

    @Override
    public Object visit(ExpressionRelational expr) {
        Object left = evaluate(expr.getLeft());
        Object right = evaluate(expr.getRight());
        
        if (left == null || right == null) {
            throw new RuntimeException("No se pueden comparar valores nulos.");
        }

        switch (expr.getOperator().getType()) {
            case GREATER:  // >
                if (left instanceof Double && right instanceof Double) {
                    return (Double) left > (Double) right;
                }
                throw new RuntimeException("Los operandos de '>' deben ser números.");
                
            case GREATER_EQUAL:  // >=
                if (left instanceof Double && right instanceof Double) {
                    return (Double) left >= (Double) right;
                }
                throw new RuntimeException("Los operandos de '>=' deben ser números.");
                
            case LESS:  // <
                if (left instanceof Double && right instanceof Double) {
                    return (Double) left < (Double) right;
                }
                throw new RuntimeException("Los operandos de '<' deben ser números.");
                
            case LESS_EQUAL:  // <=
                if (left instanceof Double && right instanceof Double) {
                    return (Double) left <= (Double) right;
                }
                throw new RuntimeException("Los operandos de '<=' deben ser números.");
                
            case EQUAL_EQUAL:  // ==
                return isEqual(left, right);
                
            case BANG_EQUAL:  // !=
                return !isEqual(left, right);
                
            default:
                throw new RuntimeException("Operador relacional desconocido.");
        }
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }
}
