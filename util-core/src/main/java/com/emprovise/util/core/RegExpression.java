package com.emprovise.util.core;

public enum RegExpression {

    WHOLE_NUMBER("^-\\d+(\\.\\d*)?$"),
    DECIMAL_NUMBER("^-\\d+(\\.\\d*)?$"),
    NEGATIVE_NUMBER("^-\\d+(\\.\\d*)?$"),
    EMAIL_PATTERN("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

    private String expression;

    private RegExpression(String expression) {
        this.expression = expression;
    }
}
