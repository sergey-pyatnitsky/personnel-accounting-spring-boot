package com.personnel_accounting.exception;

public class ExistingDataException extends RuntimeException{

    public ExistingDataException(String message) {
        super(message);
    }
}
