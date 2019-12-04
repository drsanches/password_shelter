package ru.av.passwordshelter.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface Manager {

    void createFile(String masterPassword) throws IOException, CryptException;

    void changeMasterPassword(String oldMasterPassword, String newMasterPassword) throws IOException, CryptException;

    List<String> getSourceList(String masterPassword) throws IOException, CryptException, ManagerException;

    Map<String, List<String>> getAllAccountsMap(String masterPassword) throws IOException, CryptException, ManagerException;

    List<String> getAccountList(String source, String masterPassword) throws IOException, CryptException, ManagerException;

    String getPassword(String source, String account, String masterPassword) throws IOException, CryptException, ManagerException;

    void add(String source, String account, String password, String masterPassword) throws IOException, CryptException, ManagerException;

    void changePassword(String source, String account, String newPassword, String masterPassword) throws IOException, CryptException, ManagerException;

    void delete(String source, String account, String masterPassword) throws IOException, CryptException, ManagerException;
}