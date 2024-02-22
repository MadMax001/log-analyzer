package ru.randoc.test.service;

import ru.randoc.test.exception.AppParseException;
import ru.randoc.test.model.LogRecord;

import java.time.DateTimeException;
import java.time.LocalTime;

public class LogParserService implements LogParser {
    private static final String SEPARATOR = " ";
    private static final String DT_SEPARATOR = ":";

    @Override
    public LogRecord parseLine(String line) throws AppParseException {
        if (line == null) {
            throw new AppParseException("Null source");
        }

        String[] tokens = line.split(SEPARATOR);
        if (checkForTokensAdequacy(tokens)) {
            return LogRecord
                    .builder()
                    .code(getOperationCode(tokens))
                    .duration(getDuration(tokens))
                    .time(getTime(tokens))
                    .build();
        } else
            throw new AppParseException(String.format("Illegal line: %s", line));
    }

    private boolean checkForTokensAdequacy(String[] tokens) {
        return tokens.length >= 11;
    }

    private int getOperationCode(String[] tokens) throws AppParseException {
        try {
            return Integer.parseInt(tokens[8]);
        } catch (NumberFormatException e) {
            throw new AppParseException(String.format("Line contains inappropriate code: %s", tokens[8]));
        }
    }

    private float getDuration(String[] tokens) throws AppParseException {
        try {
            return Float.parseFloat(tokens[10]);
        } catch (NumberFormatException e) {
            throw new AppParseException(String.format("Line contains inappropriate processing duration: %s", tokens[10]));
        }
    }

    private LocalTime getTime(String[] tokens) throws AppParseException {
        try {
            String[] dateTimeParts = tokens[3].split(DT_SEPARATOR);
            return LocalTime.of(
                    Integer.parseInt(dateTimeParts[1]),
                    Integer.parseInt(dateTimeParts[2]),
                    Integer.parseInt(dateTimeParts[3])
            );
        } catch (IndexOutOfBoundsException | NumberFormatException | DateTimeException e) {
            throw new AppParseException(String.format("Line contains inappropriate time record: %s", tokens[3]));
        }

    }


}
