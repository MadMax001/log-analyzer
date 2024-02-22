package ru.randoc.test.service;

import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;

@RequiredArgsConstructor
public class MainWorkflow {
    private static final int BUFFER_SIZE_DEFAULT = 1000;
    private final InputStream in;
    private final PrintStream out;

    public void start(String[] args) {
        ArrayValidator validator = new ArgumentsValidator();
        ArgumentParser argParser = new CommandLineArgumentParser();
        AppLogger logger = new ConsoleLogger();
        LogParser logParser = new LogParserService();

        try ( StreamReader reader = new StreamReaderService(in, logger)) {
            Map<Character, String> argsMap = argParser.parse(args);
            if (validator.isValid(argsMap)) {

                double errorsThreshold = Double.parseDouble(argsMap.get('u').replace(',','.'));
                double timeExecutionThreshold = Double.parseDouble(argsMap.get('t').replace(',','.'));
                int limit = argsMap.containsKey('s') ?
                        Integer.parseInt(argsMap.get('s').replace(',','.')) :
                        BUFFER_SIZE_DEFAULT;
                LogRecordChecker logRecordChecker = new LogRecordCheckerImpl(timeExecutionThreshold);
                ThresholdBuffer buffer = new ThresholdCircularBuffer(logRecordChecker, errorsThreshold, limit);
                Monitor monitor = new LogMonitor(out, logParser, reader, buffer, logRecordChecker);

                monitor.process();
        } else {
            throw new IllegalArgumentException(
                    String.join(", ", validator.details())
            );
        }
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }
}
