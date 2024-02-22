package ru.randoc.test.service;

import org.junit.jupiter.api.Test;
import ru.randoc.test.model.LogRecord;
import ru.randoc.test.model.TestLogRecordBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class LogRecordCheckerImplTest {

    @Test
    void successfulRecord() {
        LogRecord record = TestLogRecordBuilder.aRecord()
                .withCode(200)
                .withDuration(10)
                .build();
        LogRecordChecker checker = new LogRecordCheckerImpl(20);
        assertThat(checker.isSuccessful(record)).isTrue();
    }

    @Test
    void code500() {
        LogRecord record = TestLogRecordBuilder.aRecord()
                .withCode(500)
                .withDuration(10)
                .build();
        LogRecordChecker checker = new LogRecordCheckerImpl(20);
        assertThat(checker.isSuccessful(record)).isFalse();

    }

    @Test
    void code503() {
        LogRecord record = TestLogRecordBuilder.aRecord()
                .withCode(503)
                .withDuration(10)
                .build();
        LogRecordChecker checker = new LogRecordCheckerImpl(20);
        assertThat(checker.isSuccessful(record)).isFalse();

    }

    @Test
    void code100() {
        LogRecord record = TestLogRecordBuilder.aRecord()
                .withCode(100)
                .withDuration(10)
                .build();
        LogRecordChecker checker = new LogRecordCheckerImpl(20);
        assertThat(checker.isSuccessful(record)).isTrue();

    }

    @Test
    void durationMoreThreshold() {
        LogRecord record = TestLogRecordBuilder.aRecord()
                .withCode(500)
                .withDuration(25)
                .build();
        LogRecordChecker checker = new LogRecordCheckerImpl(20);
        assertThat(checker.isSuccessful(record)).isFalse();

    }

    @Test
    void durationEqualsThreshold() {
        LogRecord record = TestLogRecordBuilder.aRecord()
                .withCode(500)
                .withDuration(10)
                .build();
        LogRecordChecker checker = new LogRecordCheckerImpl(10);
        assertThat(checker.isSuccessful(record)).isFalse();

    }



}