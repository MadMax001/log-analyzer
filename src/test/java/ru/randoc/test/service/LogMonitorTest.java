package ru.randoc.test.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.randoc.test.exception.AppParseException;
import ru.randoc.test.model.LogRecord;
import ru.randoc.test.model.TestLogRecordBuilder;

import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogMonitorTest {
    @Mock
    PrintStream print;

    @Mock
    LogParser parser;

    @Mock
    StreamReader reader;

    @Mock
    ThresholdBuffer buffer;

    @Mock
    LogRecordChecker logRecordChecker;

    @Captor
    ArgumentCaptor<String> stringCaptor;

    @Test
    void readOneLine_AddToBufferSuccessfully_andCheckServiceInvokes() throws IOException, AppParseException {
        Monitor monitor = new LogMonitor(print, parser, reader, buffer, logRecordChecker);
        String inputString = "line";
        LogRecord record = TestLogRecordBuilder.aRecord().build();

        IncrementerInteger counter = new IncrementerInteger(0);
        when(reader.readNextLine()).thenAnswer((inv) -> {
            if (counter.get() == 0) {
                counter.increment();
                return inputString;
            } else
                return null;
        });
        when(parser.parseLine(inputString)).thenReturn(record);
        when(buffer.add(record)).thenReturn(true);
        when(buffer.isExceeded()).thenReturn(false);
        when(buffer.getTotal()).thenReturn(1);
        when(buffer.getFrom()).thenReturn(record.getTime());
        when(buffer.getTo()).thenReturn(record.getTime());
        when(buffer.calculateSuccessFraction()).thenReturn(0d);
        monitor.process();

        verify(reader, times(2)).readNextLine();
        verify(parser).parseLine(anyString());
        verify(buffer).add(any());
        verify(buffer).isExceeded();
        verify(buffer, never()).clear();
        verify(print).println(anyString());
    }

    @Test
    void addOneRecordToBufferSuccessfully_AndAnotherOneUnSuccessfully_andCheckServiceInvokes() throws IOException, AppParseException {
        Monitor monitor = new LogMonitor(print, parser, reader, buffer, logRecordChecker);
        String inputString = "line";
        LogRecord record = TestLogRecordBuilder.aRecord().build();

        IncrementerInteger counterLines = new IncrementerInteger(0);
        when(reader.readNextLine()).thenAnswer((inv) -> {
            if (counterLines.get() < 2) {
                counterLines.increment();
                return inputString;
            } else
                return null;
        });

        IncrementerInteger counterAdds = new IncrementerInteger(0);
        when(buffer.add(record)).thenAnswer(inv -> {
            if (counterAdds.get() == 0) {
                counterAdds.increment();
                return true;
            } else
                return false;
        });

        when(parser.parseLine(inputString)).thenReturn(record);

        when(buffer.isExceeded()).thenReturn(false);
        doNothing().when(buffer).clear();
        when(buffer.getFrom()).thenReturn(LocalTime.now());
        when(buffer.getTo()).thenReturn(LocalTime.now());
        when(buffer.calculateSuccessFraction()).thenReturn(0d);
        doNothing().when(print).println(anyString());

        monitor.process();

        verify(reader, times(3)).readNextLine();
        verify(parser, times(2)).parseLine(anyString());
        verify(buffer, times(3)).add(any());
        verify(buffer, times(2)).isExceeded();
        verify(buffer).clear();
        verify(print).println(anyString());

    }

    @Test
    void bufferExceed_AddOneErrorRecord_andCheckServiceInvokes() throws IOException, AppParseException {
        Monitor monitor = new LogMonitor(print, parser, reader, buffer, logRecordChecker);
        String inputString = "line";
        LogRecord record = TestLogRecordBuilder.aRecord().build();

        IncrementerInteger counterLines = new IncrementerInteger(0);
        when(reader.readNextLine()).thenAnswer((inv) -> {
            if (counterLines.get() == 0) {
                counterLines.increment();
                return inputString;
            } else
                return null;
        });

        when(buffer.add(record)).thenReturn(true);
        when(parser.parseLine(inputString)).thenReturn(record);
        when(buffer.isExceeded()).thenReturn(true);
        when(logRecordChecker.isSuccessful(any())).thenReturn(false);

        monitor.process();

        verify(reader, times(2)).readNextLine();
        verify(parser, times(1)).parseLine(anyString());
        verify(buffer, times(1)).add(any());
        verify(buffer).isExceeded();
        verify(logRecordChecker).isSuccessful(record);
        verify(buffer, never()).clear();
        verify(print, never()).println(anyString());
    }

    @Test
    void bufferExceed_AddOneSuccessfulRecord_andCheckServiceInvokes() throws IOException, AppParseException {
        Monitor monitor = new LogMonitor(print, parser, reader, buffer, logRecordChecker);
        String inputString = "line";
        LogRecord record = TestLogRecordBuilder.aRecord().build();

        IncrementerInteger counterLines = new IncrementerInteger(0);
        when(reader.readNextLine()).thenAnswer((inv) -> {
            if (counterLines.get() == 0) {
                counterLines.increment();
                return inputString;
            } else
                return null;
        });

        when(buffer.add(record)).thenReturn(true);
        when(parser.parseLine(inputString)).thenReturn(record);
        when(buffer.isExceeded()).thenReturn(true);
        doNothing().when(buffer).clear();
        when(buffer.getFrom()).thenReturn(LocalTime.now());
        when(buffer.getTo()).thenReturn(LocalTime.now());
        when(buffer.calculateSuccessFraction()).thenReturn(0d);
        when(logRecordChecker.isSuccessful(any())).thenReturn(true);

        monitor.process();

        verify(reader, times(2)).readNextLine();
        verify(parser, times(1)).parseLine(anyString());
        verify(buffer, times(1)).add(any());
        verify(buffer).isExceeded();
        verify(logRecordChecker).isSuccessful(record);
        verify(buffer).clear();
        verify(print).println(anyString());
    }

    @Test
    void checkPrintFormat() throws IOException, AppParseException {
        Monitor monitor = new LogMonitor(print, parser, reader, buffer, logRecordChecker);
        String inputString = "line";
        LogRecord record = TestLogRecordBuilder.aRecord().build();

        IncrementerInteger counterLines = new IncrementerInteger(0);
        when(reader.readNextLine()).thenAnswer((inv) -> {
            if (counterLines.get() == 0) {
                counterLines.increment();
                return inputString;
            } else
                return null;
        });

        when(buffer.add(record)).thenReturn(true);
        when(parser.parseLine(inputString)).thenReturn(record);
        when(buffer.isExceeded()).thenReturn(false);

        LocalTime from = LocalTime.of(10,0,0);
        LocalTime to = LocalTime.of(11,0,0);
        double val = 0.9512;
        when(buffer.getFrom()).thenReturn(from);
        when(buffer.getTo()).thenReturn(to);
        when(buffer.calculateSuccessFraction()).thenReturn(val);
        doNothing().when(print).println(anyString());

        when(buffer.getTotal()).thenReturn(1);

        monitor.process();

        verify(print).println(stringCaptor.capture());

        String printLine = stringCaptor.getValue();
        String expectedLine = "10:00:00   11:00:00   0,95";
        assertThat(printLine).isEqualTo(expectedLine);

    }
}