package com.filmbase.exception;

public class UserNameAlreadyUsedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserNameAlreadyUsedException() {
        super("userName already used!");
    }
}