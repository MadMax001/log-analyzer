package ru.randoc.test.service;

public class ConsoleLogger implements AppLogger {
    @Override
    public void info(String message) {
        System.out.println(message);
    }

    @Override
    public void error(Throwable throwable) {
        throwable.printStackTrace();
    }
}
