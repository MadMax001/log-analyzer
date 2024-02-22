package ru.randoc.test.service;

import org.junit.jupiter.api.Test;
import ru.randoc.test.exception.AppBufferException;
import ru.randoc.test.model.LogRecord;
import ru.randoc.test.model.TestLogRecordBuilder;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ThresholdCircularBufferTest {

    @Test
    void addRecord_andCheckForTimeRange_AndCheckForTotal() {
        LogRecordChecker validator = new LogRecordCheckerImpl(50);
        LocalTime time = LocalTime.parse("10:00:01");
        LogRecord record = TestLogRecordBuilder.aRecord().withTime(time).build();
        ThresholdCircularBuffer buffer = new ThresholdCircularBuffer(validator, 100, 10);
        buffer.add(record);
        assertThat(buffer.getTotal()).isEqualTo(1);
        assertThat(buffer.getFrom()).isSameAs(time);
        assertThat(buffer.getTo()).isSameAs(time);
    }

    @Test
    void tryToCalculateSuccessFractionOnNewBuffer_AndThrowsAppBufferException() {
        LogRecordChecker validator = new LogRecordCheckerImpl(50);
        ThresholdCircularBuffer buffer = new ThresholdCircularBuffer(validator, 100, 10);
        assertThatThrownBy(buffer::calculateSuccessFraction)
                .isInstanceOf(AppBufferException.class)
                .hasMessageStartingWith("Empty buffer");
    }

    @Test
    void tryToCalculateSuccessFractionOnCleanedBuffer_AndThrowsAppBufferException() {
        LogRecordChecker validator = new LogRecordCheckerImpl(50);
        ThresholdCircularBuffer buffer = new ThresholdCircularBuffer(validator, 100, 10);
        LocalTime time = LocalTime.parse("10:00:01");
        LogRecord record = TestLogRecordBuilder.aRecord().withTime(time).build();
        buffer.add(record);
        assertThatCode(buffer::calculateSuccessFraction).doesNotThrowAnyException();

        buffer.clear();
        assertThatThrownBy(buffer::calculateSuccessFraction)
                .isInstanceOf(AppBufferException.class)
                .hasMessageStartingWith("Empty buffer");
    }

    @Test
    void addRecord_andCheckForOperationResult() {
        LogRecordChecker validator = new LogRecordCheckerImpl(50);
        LocalTime time = LocalTime.parse("10:00:01");
        LogRecord record = TestLogRecordBuilder.aRecord().withTime(time).build();
        ThresholdCircularBuffer buffer = new ThresholdCircularBuffer(validator, 100, 10);
        buffer.add(record);
        assertThat(buffer.add(record)).isTrue();
    }


    @Test
    void addThreeRecords_WithDifferentTimes_andCheckTimeRange() {
        LogRecordChecker validator = new LogRecordCheckerImpl(50);
        LocalTime time1 = LocalTime.parse("10:00:01");
        LogRecord record1 = TestLogRecordBuilder.aRecord().withTime(time1).build();

        LocalTime time2 = LocalTime.parse("10:01:00");
        LogRecord record2 = TestLogRecordBuilder.aRecord().withTime(time2).build();

        LocalTime time3 = LocalTime.parse("10:02:00");
        LogRecord record3 = TestLogRecordBuilder.aRecord().withTime(time3).build();

        ThresholdCircularBuffer buffer = new ThresholdCircularBuffer(validator, 100, 10);
        buffer.add(record1);
        buffer.add(record2);
        buffer.add(record3);

        assertThat(buffer.getFrom()).isSameAs(time1);
        assertThat(buffer.getTo()).isSameAs(time3);
        assertThat(buffer.getTotal()).isEqualTo(3);
    }

    @Test
    void addRecords_OneOfThemExceedByProcessingDurationThreshold_AndCheckForErrorThresholdExceeds() {
        LogRecordChecker validator = new LogRecordCheckerImpl(45);
        List<LogRecord> records = Arrays.asList(
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:00")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:01")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:02")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:03")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:04")).withDuration(50).build()
        );
        ThresholdCircularBuffer buffer = new ThresholdCircularBuffer(validator, 90, 10);
        for (int i = 0; i < records.size();  i++) {
            buffer.add(records.get(i));
            if (i < records.size() - 1)
                assertThat(buffer.isExceeded()).as("Record: %d", i).isFalse();
            else
                assertThat(buffer.isExceeded()).as("Record: %d", i).isTrue();
        }
    }

    @Test
    void addRecords_SomeOfThemWith5xxCode_AndCheckForErrorThresholdExceeds() {
        LogRecordChecker validator = new LogRecordCheckerImpl(45);
        List<LogRecord> records = Arrays.asList(
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:00")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:01")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:02")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:03")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:04")).withCode(200).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:05")).withCode(501).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:06")).withCode(502).build()
        );
        ThresholdCircularBuffer buffer = new ThresholdCircularBuffer(validator, 75, 10);
        for (int i = 0; i < records.size();  i++) {
            buffer.add(records.get(i));
            if (i < records.size() - 1)
                assertThat(buffer.isExceeded()).as("Record: %d", i).isFalse();
            else
                assertThat(buffer.isExceeded()).as("Record: %d", i).isTrue();
        }
    }

    @Test
    void addRecordsMoreThanLimit_AndCheckFields() {
        LogRecordChecker validator = new LogRecordCheckerImpl(45);
        List<LogRecord> records = Arrays.asList(
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:00")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:01")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:02")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:03")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:04")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:05")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:06")).build()
        );
        ThresholdCircularBuffer buffer = new ThresholdCircularBuffer(validator, 75,5);

        for (LogRecord record : records) {
            buffer.add(record);
        }
        assertThat(buffer.getTotal()).isEqualTo(5);
        assertThat(buffer.getFrom()).isEqualTo(LocalTime.parse("10:00:02"));
        assertThat(buffer.getTo()).isEqualTo(LocalTime.parse("10:00:06"));


    }

    @Test
    void addRecordsMoreThanLimit_AndThresholdExceedsOnlyAfterAllRecordsAdding_AndCheckIt() {
        LogRecordChecker validator = new LogRecordCheckerImpl(45);
        List<LogRecord> records = Arrays.asList(
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:00")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:01")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:02")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:03")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:04")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:05")).withCode(500).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:06")).withDuration(50).build()
        );
        ThresholdCircularBuffer buffer = new ThresholdCircularBuffer(validator, 60.1,5);

        for (int i = 0; i < records.size();  i++) {
            buffer.add(records.get(i));
            if (i < records.size() - 1)
                assertThat(buffer.isExceeded()).as("Record: %d", i).isFalse();
            else
                assertThat(buffer.isExceeded()).as("Record: %d", i).isTrue();
        }
    }

    @Test
    void cantAddRecord_WhereFirstRecordInBufferWithError() {
        LogRecordChecker validator = new LogRecordCheckerImpl(45);
        List<LogRecord> records = Arrays.asList(
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:00")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:01")).withCode(500).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:02")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:03")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:04")).build()
        );
        ThresholdCircularBuffer buffer = new ThresholdCircularBuffer(validator, 100,3);

        for (int i = 0; i < records.size();  i++) {
            if (i < records.size() - 1)
                assertThat(buffer.add(records.get(i))).as("Record: %d", i).isTrue();
            else
                assertThat(buffer.add(records.get(i))).as("Record: %d", i).isFalse();
        }

        assertThat(buffer.getFrom()).isEqualTo(LocalTime.parse("10:00:01"));
        assertThat(buffer.getTo()).isEqualTo(LocalTime.parse("10:00:03"));

    }

    @Test
    void useBufferTwice() {
        LogRecordChecker validator = new LogRecordCheckerImpl(45);
        List<LogRecord> records1 = Arrays.asList(
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:00")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:01")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:02")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:03")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:04")).build()
        );

        List<LogRecord> records2 = Arrays.asList(
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("11:00:00")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("11:00:01")).build()
        );

        ThresholdCircularBuffer buffer = new ThresholdCircularBuffer(validator, 100,5);

        for (LogRecord record : records1) {
            buffer.add(record);
        }
        buffer.clear();
        for (LogRecord record : records2) {
            buffer.add(record);
        }
        assertThat(buffer.getFrom()).isEqualTo(LocalTime.parse("11:00:00"));
        assertThat(buffer.getTo()).isEqualTo(LocalTime.parse("11:00:01"));
        assertThat(buffer.getTotal()).isEqualTo(2);
    }

    //добавить один с ошибкой, другой правильный и проверить возможность вставки

    @Test
    void addRecords_AndClear_AndCheckFields() {
        LogRecordChecker validator = new LogRecordCheckerImpl(45);
        List<LogRecord> records = Arrays.asList(
            TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:00")).build(),
            TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:01")).build(),
            TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:02")).build(),
            TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:03")).build()
        );
        ThresholdCircularBuffer buffer = new ThresholdCircularBuffer(validator, 75,10);
        for (LogRecord record : records) {
            buffer.add(record);
        }

        buffer.clear();
        assertThat(buffer.getTotal()).isZero();
        assertThat(buffer.getFrom()).isNull();
        assertThat(buffer.getTo()).isNull();

    }

    @Test
    void addRecords_BufferExceedsOnce_ThenClear_BufferExceedsTwice() {
        LogRecordChecker validator = new LogRecordCheckerImpl(45);
        List<LogRecord> records = Arrays.asList(
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:03")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:04")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:05")).withCode(500).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:06")).withDuration(50).build()
        );
        ThresholdCircularBuffer buffer = new ThresholdCircularBuffer(validator, 55,4);
        for (LogRecord record : records) {
            buffer.add(record);
        }

        assertThat(buffer.isExceeded()).isTrue();

        buffer.clear();

        List<LogRecord> records2 = Arrays.asList(
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:03")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:04")).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:05")).withCode(500).build(),
                TestLogRecordBuilder.aRecord().withTime(LocalTime.parse("10:00:06")).withDuration(50).build()
        );
        for (int i = 0; i < records2.size();  i++) {
            buffer.add(records2.get(i));
            if (i < records2.size() - 1)
                assertThat(buffer.isExceeded()).as("Record: %d", i).isFalse();
            else
                assertThat(buffer.isExceeded()).as("Record: %d", i).isTrue();
        }
    }

}