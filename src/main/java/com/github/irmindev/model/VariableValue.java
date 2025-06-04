package com.github.irmindev.model;

public class VariableValue {
    private DataType type;
    private Object value;

    public VariableValue(DataType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
