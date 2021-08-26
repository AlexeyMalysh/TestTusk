package com.game.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlayerNotFoundException extends RuntimeException{
    public PlayerNotFoundException() {
    }

    public PlayerNotFoundException(String message) {
        super(message);
    }
    public PlayerNotFoundException(Throwable cause) {
        super(cause);
    }

    public PlayerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
