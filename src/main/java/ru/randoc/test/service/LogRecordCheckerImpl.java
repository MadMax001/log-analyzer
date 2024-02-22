package ru.randoc.test.service;

import lombok.RequiredArgsConstructor;
import ru.randoc.test.model.LogRecord;

@RequiredArgsConstructor
public class LogRecordCheckerImpl implements LogRecordChecker {
    private final double timeExecutionThreshold;

    @Override
    public boolean isSuccessful(LogRecord logRecord) {
        return isSuccessfulByCode(logRecord) && isSuccessfulByExecutionTime(logRecord);
    }

    private boolean isSuccessfulByCode(LogRecord logRecord) {
        return  (logRecord.getCode() < 500 || logRecord.getCode() >= 600);
    }

    private boolean isSuccessfulByExecutionTime(LogRecord logRecord) {
        return logRecord.getDuration() < timeExecutionThreshold;
    }

}
