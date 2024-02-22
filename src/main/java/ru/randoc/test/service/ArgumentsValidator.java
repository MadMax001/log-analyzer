package ru.randoc.test.service;


import java.util.*;

public class ArgumentsValidator implements ArrayValidator{
    private static final List<Character> validParams = Arrays.asList('u', 't', 's');
    private final List<String> details;

    public ArgumentsValidator() {
        details = new LinkedList<>();
    }

    @Override
    public List<String> details() {
        return details;
    }

    @Override
    public boolean isValid(Map<Character, String> map) throws IllegalArgumentException {
        details.clear();
        Set<Character> remainingArguments = new HashSet<>(validParams);
        remainingArguments.remove('s');

        for (Map.Entry<Character, String> entry : map.entrySet()) {
            if (validParams.contains(entry.getKey())) {
                if (!isValidArgument(entry.getKey(), entry.getValue()))
                    details.add(String.format("Illegal value of argument '%s'", entry.getKey()));
                remainingArguments.remove(entry.getKey());
            } else {
                details.add(String.format("Unknown argument '%s'", entry.getKey()));
            }
        }
        for (Character c : remainingArguments) {
            details.add(String.format("Absent required argument '%s'", c));
        }

        return details.isEmpty();
    }

    private boolean isValidArgument(Character key, String value) {
        switch (key) {
            case 'u': return isDouble(value);
            case 't': return isDouble(value);
            case 's': return isInteger(value);
            default: return false;
        }
    }

    private boolean isDouble(String value) {
        try {
            Double.parseDouble(value.replace(",","."));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }

    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
