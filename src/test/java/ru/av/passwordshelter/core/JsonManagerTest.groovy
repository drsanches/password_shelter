package ru.av.passwordshelter.core

import org.json.JSONObject
import spock.lang.Specification

class JsonManagerTest extends Specification {

    def "check that constructor sets all fields correctly"() {
        given: "filename"
        String filename = UUID.randomUUID().toString()

        when: "constructor is called"
        Manager manager = new JsonManager(filename)

        then: "filename is correct"
        filename == manager.filename

        and: "crypt and fileWorker is not empty"
        manager.crypt != null
        manager.fileWorker != null
    }

    def "check that createFile creates empty file for nonexistent filename"() {
        given: "JsonManager object with nonexistent filename and master password"
        String masterPassword = UUID.randomUUID().toString()
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)
        String encryptedFile = UUID.randomUUID().toString()

        when: "createFile is called"
        manager.createFile(masterPassword)

        then: "it creates encrypted empty json file"
        1 * manager.fileWorker.exists(filename) >> false
        1 * manager.crypt.encrypt("{}", masterPassword) >> encryptedFile
        1 * manager.fileWorker.write(encryptedFile, filename)
    }

    def "check that createFile throws IOException for existent filename"() {
        given: "JsonManager object with existent filename and master password"
        String masterPassword = UUID.randomUUID().toString()
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)

        when: "createFile is called"
        manager.createFile(masterPassword)

        then: "it does not create file"
        1 * manager.fileWorker.exists(filename) >> true
        0 * manager.fileWorker.write(_ as String, _ as String)

        and: "throws IOException"
        thrown(IOException)
    }

    def "check that changeMasterPassword changes master password correctly"() {
        given: "JsonManager object and new masterPassword"
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)
        String oldMasterPassword = UUID.randomUUID().toString()
        String newMasterPassword = UUID.randomUUID().toString()
        String encryptedContent = UUID.randomUUID().toString()
        String decryptedContent = UUID.randomUUID().toString()
        String newEncryptedContent = UUID.randomUUID().toString()

        when: "changeMasterPassword is called"
        manager.changeMasterPassword(oldMasterPassword, newMasterPassword)

        then: "it changes master password"
        1 * manager.fileWorker.read(filename) >> encryptedContent
        1 * manager.crypt.decrypt(encryptedContent, oldMasterPassword) >> decryptedContent
        1 * manager.crypt.encrypt(decryptedContent, newMasterPassword) >> newEncryptedContent
        1 * manager.fileWorker.write(newEncryptedContent, filename)
    }

    def "check that getSourceList returns correct source list"() {
        given: "JsonManager object and json file"
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)
        String masterPassword = UUID.randomUUID().toString()
        String encryptedContent = UUID.randomUUID().toString()

        when: "getSourceList is called"
        List<String> sources = manager.getSourceList(masterPassword)

        then: "it reads and decrypts file"
        1 * manager.fileWorker.read(filename) >> encryptedContent
        1 * manager.crypt.decrypt(encryptedContent, masterPassword) >> file

        and: "returns correct list"
        sources == result.asList()

        where:
        file          |  result
        createJson()  |  ["source1", "source2"]
        "{}"          |  []
    }

    def "check that getSourceList throws ManagerException for wrong file"() {
        given: "JsonManager object and wrong file"
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)
        String masterPassword = UUID.randomUUID().toString()
        String encryptedContent = UUID.randomUUID().toString()

        when: "getSourceList is called"
        manager.getSourceList(masterPassword)

        then: "it reads and decrypts file"
        1 * manager.fileWorker.read(filename) >> encryptedContent
        1 * manager.crypt.decrypt(encryptedContent, masterPassword) >> UUID.randomUUID().toString()

        and: "throws ManagerException"
        thrown(ManagerException)
    }

    def "check that getAllAccountsMap returns correct map"() {
        given: "JsonManager object and json file"
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)
        String masterPassword = UUID.randomUUID().toString()
        String encryptedContent = UUID.randomUUID().toString()

        when: "getAllAccountsMap is called"
        Map<String, List<String>> map = manager.getAllAccountsMap(masterPassword)

        then: "it reads and decrypts file"
        1 * manager.fileWorker.read(filename) >> encryptedContent
        1 * manager.crypt.decrypt(encryptedContent, masterPassword) >> file

        and: "returns correct map"
        map == result

        where:
        file          |  result
        createJson()  |  ["source1":["account1", "account2"], "source2":["account3"]]
        "{}"          |  [:]
    }

    def "check that getAllAccountsMap throws ManagerException for wrong file"() {
        given: "JsonManager object and wrong file"
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)
        String masterPassword = UUID.randomUUID().toString()
        String encryptedContent = UUID.randomUUID().toString()

        when: "getAllAccountsMap is called"
        manager.getAllAccountsMap(masterPassword)

        then: "it reads and decrypts file"
        1 * manager.fileWorker.read(filename) >> encryptedContent
        1 * manager.crypt.decrypt(encryptedContent, masterPassword) >> UUID.randomUUID().toString()

        and: "throws ManagerException"
        thrown(ManagerException)
    }

    def "check that getAccountList returns correct account list"() {
        given: "JsonManager object and json file"
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)
        String masterPassword = UUID.randomUUID().toString()
        String encryptedContent = UUID.randomUUID().toString()

        when: "getAccountList is called"
        List<String> accounts = manager.getAccountList("source1", masterPassword)

        then: "it reads and decrypts file"
        1 * manager.fileWorker.read(filename) >> encryptedContent
        1 * manager.crypt.decrypt(encryptedContent, masterPassword) >> createJson()

        and: "returns correct list"
        accounts == ["account1", "account2"].asList()
    }

    def "check that getAccountList throws ManagerException for wrong source or file"() {
        given: "JsonManager object and wrong file"
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)
        String masterPassword = UUID.randomUUID().toString()
        String encryptedContent = UUID.randomUUID().toString()

        when: "getAccountList is called"
        manager.getAccountList(source, masterPassword)

        then: "it reads and decrypts file"
        1 * manager.fileWorker.read(filename) >> encryptedContent
        1 * manager.crypt.decrypt(encryptedContent, masterPassword) >> file

        and: "throws ManagerException"
        thrown(ManagerException)

        where:
        file                          |  source
        createJson()                  |  UUID.randomUUID().toString()
        UUID.randomUUID().toString()  |  UUID.randomUUID().toString()
    }

    def "check that getPassword returns correct password"() {
        given: "JsonManager object and json file"
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)
        String masterPassword = UUID.randomUUID().toString()
        String encryptedContent = UUID.randomUUID().toString()

        when: "getAccountList is called"
        String password = manager.getPassword("source1", "account1", masterPassword)

        then: "it reads and decrypts file"
        1 * manager.fileWorker.read(filename) >> encryptedContent
        1 * manager.crypt.decrypt(encryptedContent, masterPassword) >> createJson()

        and: "returns correct list"
        password == "password1"
    }

    def "check that getPassword throws ManagerException for wrong source, account or file"() {
        given: "JsonManager object and wrong file"
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)
        String masterPassword = UUID.randomUUID().toString()
        String encryptedContent = UUID.randomUUID().toString()

        when: "getAccountList is called"
        manager.getPassword(source, account, masterPassword)

        then: "it reads and decrypts file"
        1 * manager.fileWorker.read(filename) >> encryptedContent
        1 * manager.crypt.decrypt(encryptedContent, masterPassword) >> file

        and: "throws ManagerException"
        thrown(ManagerException)

        where:
        file                          |  source                        |  account
        createJson()                  |  UUID.randomUUID().toString()  |  UUID.randomUUID().toString()
        createJson()                  |  "source1"                     |  UUID.randomUUID().toString()
        UUID.randomUUID().toString()  |  UUID.randomUUID().toString()  |  UUID.randomUUID().toString()
    }

    def "check that add adds new account to the file"() {
        given: "JsonManager object, json file and account (source, account, password)"
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)
        String masterPassword = UUID.randomUUID().toString()
        String encryptedContent = UUID.randomUUID().toString()
        String newEncryptedContent = UUID.randomUUID().toString()

        when: "add is called"
        manager.add(source, account, password, masterPassword)

        then: "it adds account to the file"
        1 * manager.fileWorker.read(filename) >> encryptedContent
        1 * manager.crypt.decrypt(encryptedContent, masterPassword) >> file
        1 * manager.crypt.encrypt(result.toString(), masterPassword) >> newEncryptedContent
        1 * manager.fileWorker.write(newEncryptedContent, filename)

        where:
        file          |  source     |  account  |  password  |  result
        createJson()  |  "s1"       |  "a1"     |  "p1"      |  createJson().put("s1", new JSONObject().put("a1", "p1"))
        createJson()  |  "source1"  |  "a1"     |  "p1"      |  createJson().put("source1", createJson().getJSONObject("source1").put("a1", "p1"))
    }

    def "check that add throws ManagerException for existent account or wrong file"() {
        given: "JsonManager object, json file and account (source, account, password)"
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)
        String masterPassword = UUID.randomUUID().toString()
        String encryptedContent = UUID.randomUUID().toString()

        when: "add is called"
        manager.add(source, account, password, masterPassword)

        then: "it reads and decrypts the file"
        1 * manager.fileWorker.read(filename) >> encryptedContent
        1 * manager.crypt.decrypt(encryptedContent, masterPassword) >> file

        and: "throws ManagerException"
        thrown(ManagerException)

        where:
        file                          |  source     |  account     |  password
        createJson()                  |  "source1"  |  "account1"  |  "p1"
        UUID.randomUUID().toString()  |  "s1"       |  "a1"        |  "p1"
    }

    def "check that changePassword changes passwordfor existent account"() {
        given: "JsonManager object, json file, account and new password"
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)
        String masterPassword = UUID.randomUUID().toString()
        String encryptedContent = UUID.randomUUID().toString()
        String newEncryptedContent = UUID.randomUUID().toString()
        String source = "source1"
        String account = "account1"
        String newPassword = "new_password1"
        String newJson = createJson().put(source, createJson().getJSONObject(source).put(account, newPassword))

        when: "changePassword is called"
        manager.changePassword(source, account, newPassword, masterPassword)

        then: "it changes account password"
        1 * manager.fileWorker.read(filename) >> encryptedContent
        1 * manager.crypt.decrypt(encryptedContent, masterPassword) >> createJson()
        1 * manager.crypt.encrypt(newJson, masterPassword) >> newEncryptedContent
        1 * manager.fileWorker.write(newEncryptedContent, filename)
    }

    def "check that changePassword throws ManagerException for nonexistent account or wrong file"() {
        given: "JsonManager object, json file, account and new password"
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)
        String masterPassword = UUID.randomUUID().toString()
        String encryptedContent = UUID.randomUUID().toString()

        when: "changePassword is called"
        manager.changePassword(source, account, newPassword, masterPassword)

        then: "it reads and decrypts the file"
        1 * manager.fileWorker.read(filename) >> encryptedContent
        1 * manager.crypt.decrypt(encryptedContent, masterPassword) >> file

        and: "throws ManagerException"
        thrown(ManagerException)

        where:
        file                          |  source     |  account  |  newPassword
        createJson()                  |  "s1"       |  "a1"     |  "p1"
        createJson()                  |  "source1"  |  "a1"     |  "p1"
        UUID.randomUUID().toString()  |  "source1"  |  "a1"     |  "p1"
    }

    def "check that delete deletes account from the file"() {
        given: "JsonManager object, json file and account"
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)
        String masterPassword = UUID.randomUUID().toString()
        String encryptedContent = UUID.randomUUID().toString()
        String newEncryptedContent = UUID.randomUUID().toString()

        when: "delete is called"
        manager.delete(source, account, masterPassword)

        then: "it adds account to the file"
        1 * manager.fileWorker.read(filename) >> encryptedContent
        1 * manager.crypt.decrypt(encryptedContent, masterPassword) >> file
        1 * manager.crypt.encrypt(result.toString(), masterPassword) >> newEncryptedContent
        1 * manager.fileWorker.write(newEncryptedContent, filename)

        where:
        file          |  source     |  account     |  result
        createJson()  |  "source1"  |  "account1"  |  new JSONObject().put("source1", new JSONObject().put("account2", "password2")).put("source2", new JSONObject().put("account3", "password3"))
        createJson()  |  "source2"  |  "account3"  |  new JSONObject().put("source1", new JSONObject().put("account1", "password1").put("account2", "password2"))
    }

    def "check that delete throws ManagerException for nonexistent account or wrong file"() {
        given: "JsonManager object, json file and account"
        String filename = UUID.randomUUID().toString()
        Manager manager = new JsonManager(filename)
        manager.crypt = Mock(Crypt)
        manager.fileWorker = Mock(FileWorker)
        String masterPassword = UUID.randomUUID().toString()
        String encryptedContent = UUID.randomUUID().toString()

        when: "delete is called"
        manager.delete(source, account, masterPassword)

        then: "it reads and decrypts the file"
        1 * manager.fileWorker.read(filename) >> encryptedContent
        1 * manager.crypt.decrypt(encryptedContent, masterPassword) >> file

        and: "throws ManagerException"
        thrown(ManagerException)

        where:
        file                          |  source          |  account
        createJson()                  |  "source1"       |  "nonexistent"
        createJson()                  |  "nonexiistent"  |  "account1"
        UUID.randomUUID().toString()  |  "source1"       |  "account1"
    }

    JSONObject createJson() {
        return new JSONObject()
                .put("source1", new JSONObject()
                        .put("account1", "password1")
                        .put("account2", "password2"))
                .put("source2", new JSONObject()
                        .put("account3", "password3"))
    }
}