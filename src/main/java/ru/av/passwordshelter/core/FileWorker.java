package ru.av.passwordshelter.core;

import java.io.IOException;

public interface FileWorker {

    String read(String filename)throws IOException;
    void write(String content, String filename) throws IOException;
    boolean exists(String filename);
}