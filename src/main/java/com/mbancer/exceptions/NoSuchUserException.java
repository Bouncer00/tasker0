package com.mbancer.exceptions;

public class NoSuchUserException extends Exception {

    public NoSuchUserException() {
        super();
    }

    public NoSuchUserException(final String cause){
        super(cause);
    }
}
