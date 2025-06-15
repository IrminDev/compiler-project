package com.github.irmindev.model;

import java.util.HashMap;
import java.util.Map;

import com.github.irmindev.model.statements.StatementFunction;

public class Environment {
    private final Environment parent;
    private final Map<String, VariableValue> variables = new HashMap<>();
    private final Map<String, StatementFunction> functions = new HashMap<>();

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public Environment(){
        this.parent = null;
    }

    public Environment getParent() {
        return parent;
    }

    public VariableValue getVariable(String name) {
        VariableValue value = variables.get(name);
        if (value == null && parent != null) {
            return parent.getVariable(name);
        }
        return value;
    }

    public void assignValue(String name, VariableValue value) {
        if (variables.containsKey(name)) {
            if(variables.get(name).getType().equals(value.getType())){
                variables.put(name, value);
            } else if(variables.get(name).getType().equals(DataType.VAR)){
                value.setType(DataType.VAR);
                variables.put(name, value);
            } else {
                throw new RuntimeException("Type mismatch for variable " + name + ": expected " + variables.get(name).getType() + " but got " + value.getType());
            }
        } else if (parent != null) {
            parent.assignValue(name, value);
        } else {
            throw new RuntimeException("Variable " + name + " is not defined.");
        }
    }

    public void defineVariable(String name, VariableValue value) {
        if (variables.containsKey(name)) {
            throw new RuntimeException("Variable " + name + " is already defined.");
        }
        variables.put(name, value);
    }

    public void defineFunction(String name, StatementFunction function) {
        if (functions.containsKey(name)) {
            throw new RuntimeException("Function " + name + " is already defined.");
        }
        functions.put(name, function);
    }

    public StatementFunction getFunction(String name) {
        StatementFunction function = functions.get(name);
        if (function == null && parent != null) {
            return parent.getFunction(name);
        }
        return function;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(variables.toString());
        if (parent != null) {
            sb.append("->").append(parent.toString());
        }

        return sb.toString();
    }
}
