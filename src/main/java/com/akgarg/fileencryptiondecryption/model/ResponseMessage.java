package com.akgarg.fileencryptiondecryption.model;

public class ResponseMessage {

    private final String message;

    public ResponseMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return this.message + "<br><br><a href='/'>Go to home</a>";
    }
}
