package ru.randoc.test.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor(staticName = "aRecord")
@With
public class TestLogRecordBuilder implements TestBuilder<LogRecord>{
    private int code = 100;
    private double duration = 15.2d;
    private LocalTime time = LocalTime.now();

    @Override
    public LogRecord build() {
        return LogRecord
                .builder()
                .code(code)
                .duration(duration)
                .time(time)
                .build();
    }
}
