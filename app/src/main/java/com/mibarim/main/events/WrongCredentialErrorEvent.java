package com.mibarim.main.events;

import java.io.Serializable;

public class WrongCredentialErrorEvent {
    private Serializable cause;

    public WrongCredentialErrorEvent(Serializable cause) {
        this.cause = cause;
    }

    public Serializable getCause() {
        return cause;
    }
}
