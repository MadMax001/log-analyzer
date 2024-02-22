package ru.randoc.test.service;

public class IncrementerInteger {
    int count;

    public IncrementerInteger(int count) {
        this.count = count;
    }

    public void increment() {
        count++;
    }

    public int get() {
        return count;
    }
}
