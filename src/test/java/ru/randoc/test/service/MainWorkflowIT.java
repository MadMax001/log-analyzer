package ru.randoc.test.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


class MainWorkflowIT {
    static DecimalFormatSymbols otherSymbols;
    static DateTimeFormatter formatter;
    static DecimalFormat decimalFormat;

    static final String template = "192.168.32.181 - - [14/06/2017:{@time} +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=7ae28555 HTTP/1.1\" {@code} 2 {@duration} \"-\" \"@list-item-updater\" prio:0" + System.lineSeparator();
    Random random;

    @BeforeAll
    static void beforeAll() {
        otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        decimalFormat = new DecimalFormat("0.00000", otherSymbols);
        formatter = DateTimeFormatter.ofPattern("hh:mm:ss");
    }

    @Test
    void testWorkflow_ErrorsBeforeThreshold() throws IOException {
        StringBuilder sb = new StringBuilder();
        LocalTime start = LocalTime.of(0, 0, 0);
        random = new Random();
        for (int i = 0; i < 199; i++) {
            sb.append(createLine(start, 200, 20.0 + 20.0 * random.nextDouble()));
            start = start.plusSeconds(1);
        }

        for (int i = 0; i < 30; i++) {
            sb.append(createLine(start, 500, 20.0 + 20.0 * random.nextDouble()));
            start = start.plusSeconds(1);
        }

        final String[] args = new String[] {
                "-u", "80",
                "-t", "45"
        };

        try (InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintStream out = new PrintStream(outputStream)) {

            new MainWorkflow(in, out).start(args);

            String output = outputStream.toString().replaceAll(System.lineSeparator(), "");
            assertThat(output).isEqualTo("12:00:00   12:03:48   0,87");

        }


    }

    @Test
    void testWorkflow_ErrorsExceedThresholdOnceAtTheEnd() throws IOException {
        StringBuilder sb = new StringBuilder();
        LocalTime start = LocalTime.of(0, 0, 0);
        random = new Random();
        for (int i = 0; i < 80; i++) {
            sb.append(createLine(start, 200, 20.0 + 20.0 * random.nextDouble()));
            start = start.plusSeconds(1);
        }

        for (int i = 0; i < 30; i++) {
            sb.append(createLine(start, 500, 20.0 + 20.0 * random.nextDouble()));
            start = start.plusSeconds(1);
        }

        final String[] args = new String[] {
                "-u", "80",
                "-t", "45"
        };

        try (InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintStream out = new PrintStream(outputStream)) {

            new MainWorkflow(in, out).start(args);

            String output = outputStream.toString().replaceAll(System.lineSeparator(), "");
            assertThat(output).isEqualTo("12:00:00   12:01:49   0,73");

        }


    }

    @Test
    void testWorkflow_ErrorsExceedThresholdOnceAtTheMiddle() throws IOException {
        StringBuilder sb = new StringBuilder();
        LocalTime start = LocalTime.of(0, 0, 0);
        random = new Random();
        for (int i = 0; i < 80; i++) {
            sb.append(createLine(start, 200, 20.0 + 20.0 * random.nextDouble()));
            start = start.plusSeconds(1);
        }

        for (int i = 0; i < 20; i++) {
            sb.append(createLine(start, 500, 20.0 + 20.0 * random.nextDouble()));
            start = start.plusSeconds(1);
        }

        for (int i = 0; i < 80; i++) {
            sb.append(createLine(start, 200, 20.0 + 20.0 * random.nextDouble()));
            start = start.plusSeconds(1);
        }

        final String[] args = new String[] {
                "-u", "81",
                "-t", "45"
        };

        try (InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintStream out = new PrintStream(outputStream)) {

            new MainWorkflow(in, out).start(args);

            String output = outputStream.toString().replaceAll(System.lineSeparator(), "");
            assertThat(output).isEqualTo("12:00:00   12:01:39   0,80");

        }
    }

    @Test
    void testWorkflow_ErrorsExceedThresholdTwice() throws IOException {
        StringBuilder sb = new StringBuilder();
        LocalTime start = LocalTime.of(0, 0, 0);
        random = new Random();
        for (int i = 0; i < 80; i++) {
            sb.append(createLine(start, 200, 20.0 + 20.0 * random.nextDouble()));
            start = start.plusSeconds(1);
        }

        for (int i = 0; i < 30; i++) {
            sb.append(createLine(start, 500, 20.0 + 20.0 * random.nextDouble()));
            start = start.plusSeconds(1);
        }

        for (int i = 0; i < 80; i++) {
            sb.append(createLine(start, 201, 20.0 + 20.0 * random.nextDouble()));
            start = start.plusSeconds(1);
        }

        for (int i = 0; i < 40; i++) {
            sb.append(createLine(start, 501, 20.0 + 20.0 * random.nextDouble()));
            start = start.plusSeconds(1);
        }

        final String[] args = new String[] {
                "-u", "81",
                "-t", "45"
        };

        try (InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintStream out = new PrintStream(outputStream)) {

            new MainWorkflow(in, out).start(args);

            String[] lines = outputStream.toString().split(System.lineSeparator());
            assertThat(lines).hasSize(2);
            assertThat(lines[0]).isEqualTo("12:00:00   12:01:49   0,73");
            assertThat(lines[1]).isEqualTo("12:01:50   12:03:49   0,67");

        }
    }

    @Test
    void testWorkflow_ErrorsExceedThresholdTwiceAtTheMiddle() throws IOException {
        StringBuilder sb = new StringBuilder();
        LocalTime start = LocalTime.of(0, 0, 0);
        random = new Random();
        for (int i = 0; i < 80; i++) {
            sb.append(createLine(start, 200, 20.0 + 20.0 * random.nextDouble()));
            start = start.plusSeconds(1);
        }

        for (int i = 0; i < 30; i++) {
            sb.append(createLine(start, 500, 20.0 + 20.0 * random.nextDouble()));
            start = start.plusSeconds(1);
        }

        for (int i = 0; i < 80; i++) {
            sb.append(createLine(start, 201, 20.0 + 20.0 * random.nextDouble()));
            start = start.plusSeconds(1);
        }

        for (int i = 0; i < 40; i++) {
            sb.append(createLine(start, 501, 20.0 + 20.0 * random.nextDouble()));
            start = start.plusSeconds(1);
        }

        for (int i = 0; i < 80; i++) {
            sb.append(createLine(start, 200, 20.0 + 20.0 * random.nextDouble()));
            start = start.plusSeconds(1);
        }

        final String[] args = new String[] {
                "-u", "81",
                "-t", "45"
        };

        try (InputStream in = new ByteArrayInputStream(sb.toString().getBytes());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintStream out = new PrintStream(outputStream)) {

            new MainWorkflow(in, out).start(args);

            String[] lines = outputStream.toString().split(System.lineSeparator());
            assertThat(lines).hasSize(2);
            assertThat(lines[0]).isEqualTo("12:00:00   12:01:49   0,73");
            assertThat(lines[1]).isEqualTo("12:01:50   12:03:49   0,67");

        }
    }

    String createLine(LocalTime time, int code, double duration) {
        return template
                .replace("{@time}", time.format(formatter))
                .replace("{@code}", "" + code)
                .replace("{@duration}", decimalFormat.format(duration));
    }
}