package ru.randoc.test.service;

import ru.randoc.test.exception.AppParseException;
import ru.randoc.test.model.LogRecord;

public interface LogParser {
    LogRecord parseLine(String line) throws AppParseException;
}
