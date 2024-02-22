package ru.randoc.test.service;

import java.util.List;
import java.util.Map;

public interface ArrayValidator {
    boolean isValid(Map<Character, String> map);
    List<String> details();

}
