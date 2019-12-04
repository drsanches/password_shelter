package ru.av.passwordshelter.core;

public class ManagerException extends Exception{

    ManagerException(String message) {
        super(message);
    }

    ManagerException(Exception e) {
        super(e);
    }
}