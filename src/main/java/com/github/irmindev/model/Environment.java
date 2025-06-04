package com.github.irmindev.model;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Environment parent;
    private final Map<String, VariableValue> variables = new HashMap<>();

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
            variables.put(name, value);
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
