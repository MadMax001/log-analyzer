package ru.randoc.test.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StreamReaderServiceTest {
    final String CR = "\r\n";
    static AppLogger appLogger;

    @BeforeAll
    static void beforeAll() {
        appLogger = new ConsoleLogger();
    }

    @Test
    void readOneLineWithoutCR_FromStream_andCheckLineContent() {
        final String line = "First line";
        InputStream is = new ByteArrayInputStream(line.getBytes());

        try (StreamReaderService service = new StreamReaderService(is, appLogger)) {
            String receivedLine = service.readNextLine();
            assertThat(receivedLine).isEqualTo(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void readOneLineWithCR_FromStream_andCheckLineContent() {
        final String line = "First line";
        StringBuilder sb = new StringBuilder();
        sb.append(line);
        sb.append(CR);
        InputStream is = new ByteArrayInputStream(sb.toString().getBytes());

        try (StreamReaderService service = new StreamReaderService(is, appLogger)) {
            String receivedLine = service.readNextLine();
            assertThat(receivedLine).isEqualTo(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void readEmptyStream_andGetNull() {
        InputStream is = new ByteArrayInputStream("".getBytes());

        try (StreamReaderService service = new StreamReaderService(is, appLogger)) {
            String receivedLine = service.readNextLine();
            assertThat(receivedLine).isNull();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void readTwoLineWithCR_FromStream_andCheckLinesContent() {
        final String line1 = "First line";
        final String line2 = "Second line";
        StringBuilder sb = new StringBuilder();
        sb.append(line1);
        sb.append(CR);
        sb.append(line2);
        InputStream is = new ByteArrayInputStream(sb.toString().getBytes());

        try (StreamReaderService service = new StreamReaderService(is, appLogger)) {
            String receivedLine1 = service.readNextLine();
            assertThat(receivedLine1).isEqualTo(line1);
            String receivedLine2 = service.readNextLine();
            assertThat(receivedLine2).isEqualTo(line2);
            String receivedLine3 = service.readNextLine();
            assertThat(receivedLine3).isNull();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void readFromNullStream_andGetNPE() {
        assertThatThrownBy(() -> new StreamReaderService(null, appLogger)).isInstanceOf(NullPointerException.class);
    }
}
