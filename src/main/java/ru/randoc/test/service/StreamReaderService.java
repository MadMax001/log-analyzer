package ru.randoc.test.service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class StreamReaderService implements StreamReader{
    private final AppLogger appLogger;
    private final InputStreamReader isr;
    private final BufferedReader reader;

    public StreamReaderService(InputStream is, AppLogger appLogger) {
        this.appLogger = appLogger;
        isr = new InputStreamReader(is);
        reader = new BufferedReader(isr);

    }

    @Override
    public String readNextLine() throws IOException {
        return reader.readLine();
    }

    @Override
    public void close() {
        try {
            if (reader != null)
                reader.close();

        } catch (IOException e) {
            appLogger.error(e);
        }

        try {
            if (isr != null)
                isr.close();
        } catch (IOException e) {
            appLogger.error(e);
        }
    }
}
