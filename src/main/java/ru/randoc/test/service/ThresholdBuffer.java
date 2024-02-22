package ru.randoc.test.service;

import ru.randoc.test.model.LogRecord;

import java.time.LocalTime;

public interface ThresholdBuffer {
    void clear();
    boolean isExceeded();
    double calculateSuccessFraction();
    int getTotal();
    boolean add(LogRecord logRecord);
    LocalTime getFrom();
    LocalTime getTo();

}
