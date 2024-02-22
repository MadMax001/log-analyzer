package ru.randoc.test.service;

public interface AppLogger {
    void info(String message);

    void error(Throwable throwable);
}
