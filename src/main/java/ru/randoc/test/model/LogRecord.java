package ru.randoc.test.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Builder
@Getter

public class LogRecord {
    private final int code;
    private final double duration;
    private final LocalTime time;
}
