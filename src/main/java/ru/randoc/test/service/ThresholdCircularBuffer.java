package ru.randoc.test.service;

import lombok.Getter;
import ru.randoc.test.exception.AppBufferException;
import ru.randoc.test.model.LogRecord;

import java.time.LocalTime;
import java.util.Deque;
import java.util.LinkedList;

public class ThresholdCircularBuffer implements ThresholdBuffer {

    private final LogRecordChecker logRecordChecker;
    private final double successThreshold;
    private final int limit;
    private final Deque<LogRecord> records;

    private int errors;

    @Getter
    private LocalTime from;

    @Getter
    private LocalTime to;

    public ThresholdCircularBuffer(LogRecordChecker logRecordChecker,
                                   double errorsThreshold,
                                   int limit) {
        this.logRecordChecker = logRecordChecker;
        this.successThreshold = errorsThreshold;
        this.limit = limit;
        records = new LinkedList<>();
    }

    public void clear() {
        errors = 0;
        from = null;
        to = null;
        records.clear();
    }

    public boolean isExceeded() {
        return !records.isEmpty() && calculateSuccessFraction() < successThreshold / 100.0;
    }

    public double calculateSuccessFraction() {
        if (records.isEmpty())
            throw new AppBufferException("Empty buffer");
        return 1.0 * (records.size() - errors) / records.size();
    }

    public int getTotal() {
        return records.size();
    }


    public boolean add(LogRecord logRecord) {
        if (records.size() >= limit) {
            if (canShift())
                shiftRecords();
            else
                return false;
        }

        if (from == null || from.isAfter(logRecord.getTime()))
            from = logRecord.getTime();

        if (to == null || to.isBefore(logRecord.getTime()))
            to = logRecord.getTime();

        if (isError(logRecord))
            errors++;

        records.add(logRecord);
        return true;
    }

    private boolean canShift() {
        return !isError(records.peekFirst());
    }

    private boolean isError(LogRecord logRecord) {
        return !logRecordChecker.isSuccessful(logRecord);
    }


    private void shiftRecords() {
        records.pollFirst();
        from = records.peekFirst().getTime();
    }


}
