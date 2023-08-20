package io.github.greetapp;

public class AppException extends RuntimeException {

    private int status;
    private String message;

    public AppException(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public AppException(String message) {
        this(500, message);
    }

    public int getStatus() {
        return this.status;
    }

    public String getMessage() {
        return this.message;
    }

}
