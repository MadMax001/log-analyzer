package ru.randoc.test.service;

import ru.randoc.test.exception.AppParseException;

import java.io.IOException;


public interface Monitor {
    void process() throws AppParseException, IOException;
}
