package com.github.irmindev.model.exception;

import com.github.irmindev.model.VariableValue;

public class ReturnException extends RuntimeException {
    private final VariableValue value;

    public ReturnException(VariableValue value) {
        super(null, null, false, false);
        this.value = value;
    }

    public VariableValue getValue() {
        return value;
    }
}
