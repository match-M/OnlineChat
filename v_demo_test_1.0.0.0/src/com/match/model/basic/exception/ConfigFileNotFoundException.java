package com.match.model.basic.exception;

public class ConfigFileNotFoundException extends Exception{
    public ConfigFileNotFoundException(String errorInfo){
        super(errorInfo);
    }
}
