package ru.av.passwordshelter.core

import spock.lang.Specification

class AES256Test extends Specification{

    def "check that encrypt encrypts content each time differently"() {
        given: "content and password"
        String content = UUID.randomUUID().toString()
        String password = UUID.randomUUID().toString()

        when: "encrypt is called twice"
        String encrypted1 = new AES256().encrypt(content, password)
        String encrypted2 = new AES256().encrypt(content, password)

        then: "results are different"
        encrypted1 != encrypted2
    }

    def "check that decrypt decrypts content correctly"() {
        given: "content and password"
        String content = UUID.randomUUID().toString()
        String password = UUID.randomUUID().toString()
        String encrypted = new AES256().encrypt(content, password)

        when: "decrypt is called"
        String decrypted = new AES256().decrypt(encrypted, password)

        then: "result is correct"
        content != encrypted
        content == decrypted
    }

    def "check that decrypt throws CryptException for wrong password"() {
        given: "content and wrong password"
        String content = UUID.randomUUID().toString()
        String password = UUID.randomUUID().toString()
        String wrongPassword = UUID.randomUUID().toString()
        String encrypted = new AES256().encrypt(content, password)

        when: "decrypt is called"
        new AES256().decrypt(encrypted, wrongPassword)

        then: "it throws CryptException"
        thrown(CryptException)
    }
}