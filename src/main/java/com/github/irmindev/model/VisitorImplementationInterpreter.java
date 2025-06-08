package com.github.irmindev.model;

import com.github.irmindev.model.exception.ReturnException;
import com.github.irmindev.model.expressions.Expression;
import com.github.irmindev.model.expressions.ExpressionArit;
import com.github.irmindev.model.expressions.ExpressionUnary;
import com.github.irmindev.model.expressions.ExpressionGrouping;
import com.github.irmindev.model.expressions.ExpressionLogical;
import com.github.irmindev.model.expressions.ExpressionLiteral;
import com.github.irmindev.model.expressions.ExpressionRelational;
import com.github.irmindev.model.expressions.ExpressionAssign;
import com.github.irmindev.model.expressions.ExpressionCallFunction;
import com.github.irmindev.model.expressions.ExpressionVariable;
import com.github.irmindev.model.expressions.ExpressionVisitor;
import com.github.irmindev.model.statements.Statement;
import com.github.irmindev.model.statements.StatementBlock;
import com.github.irmindev.model.statements.StatementFunction;
import com.github.irmindev.model.statements.StatementIf;
import com.github.irmindev.model.statements.StatementLoop;
import com.github.irmindev.model.statements.StatementReturn;
import com.github.irmindev.model.statements.StatementVisitor;

public class VisitorImplementationInterpreter implements ExpressionVisitor<VariableValue>, StatementVisitor<Void> {
    private final Environment global;
    private final Environment environment;

    public VisitorImplementationInterpreter(Environment global, Environment environment) {
        this.global = global;
        this.environment = environment;
    }

    private VariableValue evaluate(Expression expression) {
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
    public VariableValue visit(ExpressionGrouping expr) {
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

    @Override
    public VariableValue visit(ExpressionVariable expression) {
        VariableValue value = environment.getVariable(((Token.Indetifier)expression.getName()).getLexeme());
        if (value == null) {
            throw new RuntimeException("Variable no definida: " + ((Token.Indetifier)expression.getName()).getLexeme());
        }
        return value;
    }

    @Override
    public Object visit(ExpressionAssign expression) {
        Object value = evaluate(expression.getValue());
        VariableValue variable;
        environment.assignValue(((Token.Indetifier)expression.getName()).getLexeme(), (VariableValue) value);
        return value;
    }

    @Override
    public VariableValue visit(ExpressionCallFunction expression) {
        StatementFunction function = environment.getFunction(
            ((Token.Indetifier)
            ((ExpressionVariable)expression.getCallee())
            .getName()).getLexeme());
        if (function == null) {
            throw new RuntimeException("Undefined function: " + 
                ((Token.Indetifier)((ExpressionVariable)expression.getCallee()).getName()).getLexeme());
        }

        Environment functionEnvironment = new Environment(environment);

        if(function.getParameters().size() != expression.getArguments().size()) {
            throw new RuntimeException("Function " + ((Token.Indetifier)function.getIdentifier()).getLexeme() + 
                " expects " + function.getParameters().size() + " arguments but got " + expression.getArguments().size());
        }

        for(int i = 0; i < function.getParameters().size(); i++) {
            Token.Indetifier param = (Token.Indetifier)function.getParameters().get(i);
            VariableValue argValue = evaluate(expression.getArguments().get(i));
            functionEnvironment.defineVariable(param.getLexeme(), argValue);
        }

        try {
            function.getBlock().accept(new VisitorImplementationInterpreter(global, functionEnvironment));
            return new VariableValue(null, DataType.VAR);
        } catch (ReturnException e) {
            return e.getValue();
        }

    }
    
    @Override
    public Void visit(StatementIf statement) {
        Object condition = evaluate(statement.getCondition());
        if (isTruthy(condition)) {
            statement.getBlock().accept(this);
        } else if (statement.getElseBlock() != null) {
            statement.getElseBlock().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(StatementBlock statement) {
        Environment blockEnvironment = new Environment(environment);
        for (Statement stmt : statement.getStatements()) {
            stmt.accept(new VisitorImplementationInterpreter(global, blockEnvironment));
        }
        return null;
    }

    @Override
    public Void visit(StatementLoop statement) {
        while (isTruthy(evaluate(statement.getCondition()))) {
            statement.getBlock().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(StatementFunction statement) {
        environment.defineFunction(((Token.Indetifier)statement.getIdentifier()).getLexeme(), statement);
        return null;
    }

    @Override
    public Void visit(StatementReturn statement) {
        VariableValue returnValue = null;
        
        if (statement.getValue() != null) {
            returnValue = evaluate(statement.getValue());
        }
        
        throw new ReturnException(returnValue);
    }
}
