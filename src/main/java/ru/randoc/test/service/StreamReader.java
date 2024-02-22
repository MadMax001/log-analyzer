package ru.randoc.test.service;

import java.io.IOException;

public interface StreamReader extends AutoCloseable {
    String readNextLine() throws IOException;
}
