package ru.av.passwordshelter.core;

public class CryptException extends Exception {

    CryptException(String message) {
        super(message);
    }

    CryptException(Exception e) {
        super(e);
    }
}