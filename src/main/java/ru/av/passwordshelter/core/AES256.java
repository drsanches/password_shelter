package ru.av.passwordshelter.core;

import org.springframework.security.crypto.encrypt.Encryptors;

public class AES256 implements Crypt {

    private String salt = "b89f5ea2987e8e54";

    @Override
    public String encrypt(String content, String password) throws CryptException {
        try {
            return Encryptors.text(password, salt).encrypt(content);
        } catch (Exception e) {
            throw new CryptException(e);
        }
    }

    @Override
    public String decrypt(String content, String password) throws CryptException {
        try {
            return Encryptors.text(password, salt).decrypt(content);
        } catch (Exception e) {
            throw new CryptException(e);
        }
    }
}