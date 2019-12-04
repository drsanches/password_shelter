package ru.av.passwordshelter.core;

import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class JsonManager implements Manager {

    private String filename = null;
    private Crypt crypt = new AES256();
    private FileWorker fileWorker = new TextFileWorker();

    public JsonManager(String filename) {
        this.filename = filename;
    }

    @Override
    public void createFile(String masterPassword) throws IOException, CryptException {
        if (fileWorker.exists(filename)) {
            throw new IOException("File \"" + filename + "\" already exists!");
        }
        fileWorker.write(crypt.encrypt("{}", masterPassword), filename);
    }

    @Override
    public void changeMasterPassword(String oldMasterPassword, String newMasterPassword) throws IOException, CryptException{
        fileWorker.write(crypt.encrypt(crypt.decrypt(fileWorker.read(filename), oldMasterPassword), newMasterPassword), filename);
    }

    @Override
    public List<String> getSourceList(String masterPassword) throws IOException, CryptException, ManagerException {
        String content = crypt.decrypt(fileWorker.read(filename), masterPassword);
        List<String> sources = new ArrayList<>();
        try {
            Iterator keys = new JSONObject(content).sortedKeys();
            while (keys.hasNext()) {
                sources.add(keys.next().toString());
            }
            return sources;
        } catch (Exception e) {
            throw new ManagerException(e);
        }
    }

    @Override
    public Map<String, List<String>> getAllAccountsMap(String masterPassword) throws IOException, CryptException, ManagerException {
        String content = crypt.decrypt(fileWorker.read(filename), masterPassword);
        Map<String, List<String>> map = new TreeMap<>();
        try {
            Iterator sourceIterator = new JSONObject(content).sortedKeys();
            while (sourceIterator.hasNext()) {
                String source = sourceIterator.next().toString();
                ArrayList<String> accounts = new ArrayList<>();
                Iterator accountIterator = new JSONObject(content).getJSONObject(source).sortedKeys();
                while (accountIterator.hasNext()) {
                    accounts.add(accountIterator.next().toString());
                }
                map.put(source, accounts);
            }
            return map;
        } catch (Exception e) {
            throw new ManagerException(e);
        }
    }

    @Override
    public List<String> getAccountList(String source, String masterPassword) throws IOException, CryptException, ManagerException {
        String content = crypt.decrypt(fileWorker.read(filename), masterPassword);
        List<String> accounts = new ArrayList<>();
        try {
            Iterator keys = new JSONObject(content).getJSONObject(source).sortedKeys();
            while (keys.hasNext()) {
                accounts.add(keys.next().toString());
            }
            return accounts;
        } catch (Exception e) {
            throw new ManagerException(e);
        }
    }

    @Override
    public String getPassword(String source, String account, String masterPassword) throws IOException, CryptException, ManagerException {
        String content = crypt.decrypt(fileWorker.read(filename), masterPassword);
        try {
            return new JSONObject(content).getJSONObject(source).getString(account);
        } catch (Exception e) {
            throw new ManagerException(e);
        }
    }

    @Override
    public void add(String source, String account, String password, String masterPassword) throws IOException, CryptException, ManagerException {
        String content = crypt.decrypt(fileWorker.read(filename), masterPassword);
        try {
            JSONObject json = new JSONObject(content);
            if (json.has(source)) {
                if (json.getJSONObject(source).has(account)) {
                    throw new ManagerException("Account already exists");
                } else {
                    json.getJSONObject(source).put(account, password);
                }
            } else {
                json.put(source, new JSONObject().put(account, password));
            }
            content = json.toString();
        } catch (Exception e) {
            throw new ManagerException(e);
        }
        fileWorker.write(crypt.encrypt(content, masterPassword), filename);
    }

    @Override
    public void changePassword(String source, String account, String newPassword, String masterPassword) throws IOException, CryptException, ManagerException {
        String content = crypt.decrypt(fileWorker.read(filename), masterPassword);
        try {
            JSONObject json = new JSONObject(content);
            if (!json.has(source)) {
                throw new ManagerException("Unknown source: " + source);
            } else {
                if (!json.getJSONObject(source).has(account)) {
                    throw new ManagerException("Unknown account: " + account);
                }
                json.getJSONObject(source).put(account, newPassword);
            }
            content = json.toString();
        } catch (Exception e) {
            throw new ManagerException(e);
        }
        fileWorker.write(crypt.encrypt(content, masterPassword), filename);
    }

    @Override
    public void delete(String source, String account, String masterPassword) throws IOException, CryptException, ManagerException {
        String content = crypt.decrypt(fileWorker.read(filename), masterPassword);
        try {
            JSONObject json = new JSONObject(content);
            if (json.has(source)) {
                if (json.getJSONObject(source).has(account)) {
                    json.getJSONObject(source).remove(account);
                    if (!json.getJSONObject(source).keys().hasNext()) {
                        json.remove(source);
                    }
                } else {
                    throw new ManagerException("No such source!");
                }
            } else {
                throw new ManagerException("No such source!");
            }
            content = json.toString();
        } catch (Exception e) {
            throw new ManagerException(e);
        }
        fileWorker.write(crypt.encrypt(content, masterPassword), filename);
    }
}