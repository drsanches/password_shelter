package ru.av.passwordshelter.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TextFileWorker implements FileWorker {

    @Override
    public boolean exists(String filename) {
        return Files.exists(Paths.get(filename));
    }

    @Override
    public String read(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
    }

    @Override
    public void write(String content, String filename) throws IOException {
        Files.write(Paths.get(filename), content.getBytes());
    }
}