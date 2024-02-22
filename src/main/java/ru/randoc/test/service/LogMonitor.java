package ru.randoc.test.service;

import lombok.RequiredArgsConstructor;
import ru.randoc.test.exception.AppParseException;
import ru.randoc.test.model.LogRecord;

import java.io.IOException;
import java.io.PrintStream;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class LogMonitor implements Monitor {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");

    private final PrintStream print;
    private final LogParser parser;
    private final StreamReader reader;
    private final ThresholdBuffer buffer;
    private final LogRecordChecker logRecordChecker;

    @Override
    public void process() throws AppParseException, IOException {
        String line = reader.readNextLine();
        while (line != null) {
            LogRecord currentRecord = parser.parseLine(line);

            if (buffer.isExceeded()) {
                if (logRecordChecker.isSuccessful(currentRecord)) {
                    print();
                    buffer.clear();
                }
            }

            boolean success = buffer.add(currentRecord);

            if (!success) {
                print();
                buffer.clear();
                buffer.add(currentRecord);
            }


            line = reader.readNextLine();
        }

        if (buffer.getTotal() > 0 && buffer.calculateSuccessFraction() < 1.0)
            print();
    }

    private void print() {
        String line = String.format(
                "%s   %s   %,.2f",
                buffer.getFrom().format(formatter),
                buffer.getTo().format(formatter),
                buffer.calculateSuccessFraction());
        print.println(line);
    }
}
