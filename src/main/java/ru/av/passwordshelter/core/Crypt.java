package ru.av.passwordshelter.core;

public interface Crypt {

    String encrypt(String content, String password) throws CryptException;

    String decrypt(String content, String password) throws CryptException;
}