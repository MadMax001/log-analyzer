package ru.randoc.test.service;

import org.junit.jupiter.api.Test;
import ru.randoc.test.exception.AppParseException;
import ru.randoc.test.model.LogRecord;

import static org.assertj.core.api.Assertions.*;

class LogParserServiceTest {

    @Test
    void parseValidLine_AndGetLogRecord() throws AppParseException {
        final String logLine = "192.168.32.181 - - [14/06/2017:16:47:02 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1\" 200 2 44.510983 \"-\" \"@list-item-updater\" prio:0";
        LogParser parser = new LogParserService();
        LogRecord record = parser.parseLine(logLine);
        assertThat(record.getCode()).isEqualTo(200);
        assertThat(record.getDuration()).isEqualTo(44.510983d, withPrecision(5d));
        assertThat(record.getTime().getHour()).isEqualTo(16);
        assertThat(record.getTime().getMinute()).isEqualTo(47);
        assertThat(record.getTime().getSecond()).isEqualTo(2);
    }

    @Test
    void parseNullLine_AndGetAppParseException() {
        LogParser parser = new LogParserService();
        assertThatThrownBy(() -> parser.parseLine(null))
                .isInstanceOf(AppParseException.class)
                .hasMessageStartingWith("Null source");
    }


    @Test
    void parseInValidLine_WithNotEnoughInformation_AndGetAppParseException() {
        final String logLine = "192.168.32.181 - - [14/06/2017:16:47:02 +1000]";
        LogParser parser = new LogParserService();
        assertThatThrownBy(() -> parser.parseLine(logLine))
                .isInstanceOf(AppParseException.class)
                .hasMessageStartingWith("Illegal line");
    }

    @Test
    void parseInValidLine_WithIllegalProcessDuration_AndGetAppParseException() {
        final String logLine = "192.168.32.181 - - [14/06/2017:16:47:02 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1\" 200 2 44,510983 \"-\" \"@list-item-updater\" prio:0";
        LogParser parser = new LogParserService();
        assertThatThrownBy(() -> parser.parseLine(logLine))
                .isInstanceOf(AppParseException.class)
                .hasMessageStartingWith("Line contains inappropriate processing duration");
    }

    @Test
    void parseInValidLine_WithIllegalCode_AndGetAppParseException() {
        final String logLine = "192.168.32.181 - - [14/06/2017:16:47:02 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1\" 200a 2 44.510983 \"-\" \"@list-item-updater\" prio:0";
        LogParser parser = new LogParserService();
        assertThatThrownBy(() -> parser.parseLine(logLine))
                .isInstanceOf(AppParseException.class)
                .hasMessageStartingWith("Line contains inappropriate code");
    }

    @Test
    void parseInValidLine_WithIllegalTime_AndGetAppParseException() {
        final String logLine = "192.168.32.181 - - [14/06/2017:16:77:02 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1\" 200 2 44.510983 \"-\" \"@list-item-updater\" prio:0";
        LogParser parser = new LogParserService();
        assertThatThrownBy(() -> parser.parseLine(logLine))
                .isInstanceOf(AppParseException.class)
                .hasMessageStartingWith("Line contains inappropriate time record");
    }

    @Test
    void parseInValidLine_WithIllegalDateTime_AndGetAppParseException() {
        final String logLine = "192.168.32.181 - - [14/06/2017_16:77:02 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1\" 200 2 44.510983 \"-\" \"@list-item-updater\" prio:0";
        LogParser parser = new LogParserService();
        assertThatThrownBy(() -> parser.parseLine(logLine))
                .isInstanceOf(AppParseException.class)
                .hasMessageStartingWith("Line contains inappropriate time record");
    }


    @Test
    void parseTwoLines_AndGetTwoLogRecord() throws AppParseException {
        final String logLine1 = "192.168.32.181 - - [14/06/2017:16:47:02 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=6076537c HTTP/1.1\" 200 2 44.510983 \"-\" \"@list-item-updater\" prio:0";
        final String logLine2 = "192.168.32.181 - - [14/06/2017:15:14:13 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=7ae28555 HTTP/1.1\" 201 2 23.251219 \"-\" \"@list-item-updater\" prio:0";
        LogParser parser = new LogParserService();

        LogRecord record1 = parser.parseLine(logLine1);
        assertThat(record1.getCode()).isEqualTo(200);
        assertThat(record1.getDuration()).isEqualTo(44.510983d, withPrecision(5d));
        assertThat(record1.getTime().getHour()).isEqualTo(16);
        assertThat(record1.getTime().getMinute()).isEqualTo(47);
        assertThat(record1.getTime().getSecond()).isEqualTo(2);


        LogRecord record2 = parser.parseLine(logLine2);
        assertThat(record2.getCode()).isEqualTo(201);
        assertThat(record2.getDuration()).isEqualTo(23.251219d, withPrecision(5d));
        assertThat(record2.getTime().getHour()).isEqualTo(15);
        assertThat(record2.getTime().getMinute()).isEqualTo(14);
        assertThat(record2.getTime().getSecond()).isEqualTo(13);

    }

}