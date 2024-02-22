package ru.randoc.test.service;

import ru.randoc.test.model.LogRecord;

public interface LogRecordChecker {
    boolean isSuccessful(LogRecord logRecord);
}
