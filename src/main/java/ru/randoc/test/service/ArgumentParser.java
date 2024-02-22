package ru.randoc.test.service;

import java.util.Map;

public interface ArgumentParser {
    Map<Character, String> parse(String[] args) throws IllegalArgumentException;
}
