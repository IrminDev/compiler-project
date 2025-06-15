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
import com.github.irmindev.model.statements.StatementBoolean;
import com.github.irmindev.model.statements.StatementChar;
import com.github.irmindev.model.statements.StatementDouble;
import com.github.irmindev.model.statements.StatementInteger;
import com.github.irmindev.model.statements.StatementString;
import com.github.irmindev.model.statements.StatementVar;
import com.github.irmindev.model.statements.StatementExpression;
import com.github.irmindev.model.statements.StatementPrint;


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
    public VariableValue visit(ExpressionUnary expr) {  
        VariableValue right = evaluate(expr.getRight());

        switch (expr.getOperator().getType()) {
            case MINUS: // '-'
                if (right.getValue() instanceof Double) {
                    return new VariableValue(DataType.DOUBLE, -((Double) right.getValue()));
                }
                if (right.getValue() instanceof Integer) {
                    return new VariableValue(DataType.INTEGER, -((Integer) right.getValue()));
                }
                throw new RuntimeException("El operando debe ser un número.");
            case BANG: // '!'
                return new VariableValue(DataType.BOOLEAN, !isTruthy(right.getValue()));
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
    public VariableValue visit(ExpressionArit expr) {
        VariableValue left = evaluate(expr.getLeft());
        VariableValue right = evaluate(expr.getRight());

        switch (expr.getOperator().getType()) {
            case PLUS: // '+'	
                if (left.getValue() instanceof Double && right.getValue() instanceof Double) {
                    return new VariableValue(DataType.DOUBLE, (Double) left.getValue() + (Double) right.getValue());
            
                }
                if (left.getValue() instanceof Integer && right.getValue() instanceof Integer) {
                    return new VariableValue(DataType.INTEGER, (Integer) left.getValue() + (Integer) right.getValue());
                }

                if (left.getValue() instanceof String || right.getValue() instanceof String) {
                    return new VariableValue(DataType.STRING, (String) left.getValue() + (String) right.getValue());
                }

                throw new RuntimeException("Los operandos de '+' deben ser números.");
            case MINUS: // '-'
                if (left.getValue() instanceof Double && right.getValue() instanceof Double) {
                    return new VariableValue(DataType.DOUBLE, (Double) left.getValue() - (Double) right.getValue());
                }
                if (left.getValue() instanceof Integer && right.getValue() instanceof Integer) {
                    return new VariableValue(DataType.INTEGER, (Integer) left.getValue() - (Integer) right.getValue());
                }
                throw new RuntimeException("Los operandos de '-' deben ser números.");
            case STAR: // '*'
                if (left.getValue() instanceof Double && right.getValue() instanceof Double) {
                    return new VariableValue(DataType.DOUBLE, (Double) left.getValue() * (Double) right.getValue());
                }
                if (left.getValue() instanceof Integer && right.getValue() instanceof Integer) {
                    return new VariableValue(DataType.INTEGER, (Integer) left.getValue() * (Integer) right.getValue());
                }
                throw new RuntimeException("Los operandos de '*' deben ser números.");
            case SLASH: // '/'
                if (left.getValue() instanceof Double && right.getValue() instanceof Double) {
                    if ((Double) right.getValue() == 0) {
                        throw new RuntimeException("División por cero.");
                    }
                    return new VariableValue(DataType.DOUBLE, (Double) left.getValue() / (Double) right.getValue());
                }
                if (left.getValue() instanceof Integer && right.getValue() instanceof Integer) {
                    if ((Integer) right.getValue() == 0) {
                        throw new RuntimeException("División por cero.");
                    }
                    return new VariableValue(DataType.INTEGER, (Integer) left.getValue() / (Integer) right.getValue());
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
    public VariableValue visit(ExpressionLogical expr) {
        VariableValue left = evaluate(expr.getLeft());

        switch (expr.getOperator().getType()) {
            case OR: // '||'
                if (isTruthy(left.getValue())) {
                    return new VariableValue(DataType.BOOLEAN, true);
                }
                VariableValue right = evaluate(expr.getRight());
                return new VariableValue(DataType.BOOLEAN, isTruthy(right.getValue()));

            case AND: // '&&'
                if (!isTruthy(left.getValue())) {
                    return new VariableValue(DataType.BOOLEAN, false);
                }
                VariableValue rightAnd = evaluate(expr.getRight());
                return new VariableValue(DataType.BOOLEAN, isTruthy(rightAnd.getValue()));

            default:
                throw new RuntimeException("Operador lógico desconocido.");
        }
    }

    @Override
    public VariableValue visit(ExpressionLiteral expr) {
        Object value = expr.getValue();
        
        // Determinar el tipo de dato basado en el valor
        if (value instanceof Double) {
            return new VariableValue(DataType.DOUBLE, value);
        } else if (value instanceof Boolean) {
            return new VariableValue(DataType.BOOLEAN, value);
        } else if (value instanceof String) {
            return new VariableValue(DataType.STRING, value);
        } else if (value instanceof Integer) {
            return new VariableValue(DataType.INTEGER, value);
        } else if (value instanceof Character) {
            return new VariableValue(DataType.CHAR, value);
        } else if (value == null) {
            return new VariableValue(DataType.VAR, null);
        } else {
            return new VariableValue(DataType.VAR, value);
        }
    }

    @Override
    public VariableValue visit(ExpressionRelational expr) {
        VariableValue left = evaluate(expr.getLeft());
        VariableValue right = evaluate(expr.getRight());
        
        if (left.getValue() == null || right.getValue() == null) {
            throw new RuntimeException("No se pueden comparar valores nulos.");
        }

        switch (expr.getOperator().getType()) {
            case GREATER:  // >
                if (left.getValue() instanceof Double && right.getValue() instanceof Double) {
                    return new VariableValue(DataType.BOOLEAN, (Double) left.getValue() > (Double) right.getValue());
                }
                if (left.getValue() instanceof Integer && right.getValue() instanceof Integer) {
                    return new VariableValue(DataType.BOOLEAN, (Integer) left.getValue() > (Integer) right.getValue());
                }
                throw new RuntimeException("Los operandos de '>' deben ser números.");
                
            case GREATER_EQUAL:  // >=
                if (left.getValue() instanceof Double && right.getValue() instanceof Double) {
                    return new VariableValue(DataType.BOOLEAN, (Double) left.getValue() >= (Double) right.getValue());
                }
                if (left.getValue() instanceof Integer && right.getValue() instanceof Integer) {
                    return new VariableValue(DataType.BOOLEAN, (Integer) left.getValue() >= (Integer) right.getValue());
                }
                throw new RuntimeException("Los operandos de '>=' deben ser números.");
                
            case LESS:  // <
                if (left.getValue() instanceof Double && right.getValue() instanceof Double) {
                    return new VariableValue(DataType.BOOLEAN, (Double) left.getValue() < (Double) right.getValue());
                }
                if (left.getValue() instanceof Integer && right.getValue() instanceof Integer) {
                    return new VariableValue(DataType.BOOLEAN, (Integer) left.getValue() < (Integer) right.getValue());
                }
                throw new RuntimeException("Los operandos de '<' deben ser números.");
                
            case LESS_EQUAL:  // <=
                if (left.getValue() instanceof Double && right.getValue() instanceof Double) {
                    return new VariableValue(DataType.BOOLEAN, (Double) left.getValue() <= (Double) right.getValue());
                }
                if (left.getValue() instanceof Integer && right.getValue() instanceof Integer) {
                    return new VariableValue(DataType.BOOLEAN, (Integer) left.getValue() <= (Integer) right.getValue());
                }
                throw new RuntimeException("Los operandos de '<=' deben ser números.");
                
            case EQUAL_EQUAL:  // ==
                return new VariableValue(DataType.BOOLEAN, isEqual(left.getValue(), right.getValue()));
                
            case BANG_EQUAL:  // !=
                return new VariableValue(DataType.BOOLEAN, !isEqual(left.getValue(), right.getValue()));
                
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
    public VariableValue visit(ExpressionAssign expression) {
        Object value = evaluate(expression.getValue());
        VariableValue variable = environment.getVariable(((Token.Indetifier)expression.getName()).getLexeme());
        if (variable == null) {
            throw new RuntimeException("Variable no definida: " + ((Token.Indetifier)expression.getName()).getLexeme());
        }
        environment.assignValue(((Token.Indetifier)expression.getName()).getLexeme(), (VariableValue) value);
        return variable;
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

    @Override
    public Void visit(StatementBoolean statement) {
        VariableValue value = null;
        
        if (statement.getInitializer() != null) {
            value = evaluate(statement.getInitializer());
            
            if (!(value.getValue() instanceof Boolean)) {
                throw new RuntimeException("El valor asignado a una variable boolean debe ser de tipo boolean.");
            }
        } else {
            value = new VariableValue(DataType.BOOLEAN, false);
        }
        
        String variableName = ((Token.Indetifier)statement.getIdentifier()).getLexeme();
        environment.defineVariable(variableName, value);
        
        return null;
    }

    @Override
    public Void visit(StatementChar statement) {
        VariableValue value = null;
        
        if (statement.getInitializer() != null) {
            value = evaluate(statement.getInitializer());
            
            if (!(value.getValue() instanceof Character)) {
                throw new RuntimeException("El valor asignado a una variable char debe ser de tipo char.");
            }
        } else {
            value = new VariableValue(DataType.CHAR, '\0');
        }
        
        String variableName = ((Token.Indetifier)statement.getIdentifier()).getLexeme();
        environment.defineVariable(variableName, value);
        
        return null;
    }

    @Override
    public Void visit(StatementDouble statement) {
        VariableValue value = null;
        
        if (statement.getInitializer() != null) {
            value = evaluate(statement.getInitializer());
            
            if (!(value.getValue() instanceof Double)) {
                throw new RuntimeException("El valor asignado a una variable double debe ser de tipo double.");
            }
        } else {
            value = new VariableValue(DataType.DOUBLE, 0.0);
        }
        
        String variableName = ((Token.Indetifier)statement.getIdentifier()).getLexeme();
        environment.defineVariable(variableName, value);
        
        return null;
    }

    @Override
    public Void visit(StatementInteger statement) {
        VariableValue value = null;
        
        if (statement.getInitializer() != null) {
            value = evaluate(statement.getInitializer());
            
            if (!(value.getValue() instanceof Integer)) {
                throw new RuntimeException("El valor asignado a una variable integer debe ser de tipo integer.");
            }
        } else {
            value = new VariableValue(DataType.INTEGER, 0);
        }
        
        String variableName = ((Token.Indetifier)statement.getIdentifier()).getLexeme();
        environment.defineVariable(variableName, value);
        
        return null;
    }

    @Override
    public Void visit(StatementString statement) {
        VariableValue value = null;
        
        if (statement.getInitializer() != null) {
            value = evaluate(statement.getInitializer());
            
            if (!(value.getValue() instanceof String)) {
                throw new RuntimeException("El valor asignado a una variable string debe ser de tipo string.");
            }
        } else {
            value = new VariableValue(DataType.STRING, "");
        }
        
        String variableName = ((Token.Indetifier)statement.getIdentifier()).getLexeme();
        environment.defineVariable(variableName, value);
        
        return null;
    }

    @Override
    public Void visit(StatementExpression statement) {
        evaluate(statement.getExpression());
        return null;
    }

    @Override
    public Void visit(StatementPrint statement) {
        VariableValue value = evaluate(statement.getExpression());
        if (value != null) {
            System.out.println(value.getValue());
        } else {
            System.out.println("null");
        }
        
        return null;
    }

    @Override
    public Void visit(StatementVar statement) {
        VariableValue value = null;

        if (statement.getInitializer() != null) {
            value = evaluate(statement.getInitializer());
            value.setType(DataType.VAR);
        } else {
            value = new VariableValue(DataType.VAR, null);
        }

        String variableName = ((Token.Indetifier)statement.getIdentifier()).getLexeme();
        environment.defineVariable(variableName, value);

        return null;
    }
}
