package ru.randoc.test.service;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ArgumentsValidatorTest {
    @Test
    void twoValidArguments() {
        Map<Character, String> map = new HashMap<>();
        map.put('u',"99.9");
        map.put('t', "45.1");

        ArgumentsValidator validator = new ArgumentsValidator();
        boolean result = validator.isValid(map);
        assertThat(result).isTrue();
        assertThat(validator.details()).isEmpty();
    }

    @Test
    void threeValidArguments() {
        Map<Character, String> map = new HashMap<>();
        map.put('u',"99.9");
        map.put('t', "45.1");

        ArgumentsValidator validator = new ArgumentsValidator();
        boolean result = validator.isValid(map);
        assertThat(result).isTrue();
        assertThat(validator.details()).isEmpty();
    }

    @Test
    void wrongArgument() {
        Map<Character, String> map = new HashMap<>();
        map.put('u',"99.9");
        map.put('t', "45.1");
        map.put('a', "100");

        ArgumentsValidator validator = new ArgumentsValidator();
        boolean result = validator.isValid(map);
        assertThat(result).isFalse();
        assertThat(validator.details()).hasSize(1);
        assertThat(validator.details().get(0)).isEqualTo("Unknown argument 'a'");
    }

    @Test
    void absentOneRequiredArgument() {
        Map<Character, String> map = new HashMap<>();
        map.put('u',"99.9");
        map.put('s', "100");

        ArgumentsValidator validator = new ArgumentsValidator();
        boolean result = validator.isValid(map);
        assertThat(result).isFalse();
        assertThat(validator.details()).hasSize(1);
        assertThat(validator.details().get(0)).isEqualTo("Absent required argument 't'");
    }

    @Test
    void absentTwoRequiredArgument() {
        Map<Character, String> map = new HashMap<>();
        map.put('s', "100");

        ArgumentsValidator validator = new ArgumentsValidator();
        boolean result = validator.isValid(map);
        assertThat(result).isFalse();
        assertThat(validator.details()).hasSize(2);
        List<String> possibleDetails = Arrays.asList(
                "Absent required argument 'u'",
                "Absent required argument 't'"
        );
        assertThat(possibleDetails).containsAll(validator.details());
    }

    @Test
    void noArguments() {
        ArgumentsValidator validator = new ArgumentsValidator();
        boolean result = validator.isValid(new HashMap<>());
        assertThat(result).isFalse();
        assertThat(validator.details()).hasSize(2);
        List<String> possibleDetails = Arrays.asList(
                "Absent required argument 'u'",
                "Absent required argument 't'"
        );
        assertThat(possibleDetails).containsAll(validator.details());
    }

    @Test
    void illegalThresholdArgument() {
        Map<Character, String> map = new HashMap<>();
        map.put('u',"99a");
        map.put('t', "45.1");
        map.put('s', "100");

        ArgumentsValidator validator = new ArgumentsValidator();
        boolean result = validator.isValid(map);
        assertThat(result).isFalse();
        assertThat(validator.details()).hasSize(1);
        assertThat(validator.details().get(0)).isEqualTo("Illegal value of argument 'u'");

    }

    @Test
    void illegalDurationArgument() {
        Map<Character, String> map = new HashMap<>();
        map.put('u',"99.9");
        map.put('t', "45a");
        map.put('s', "100");

        ArgumentsValidator validator = new ArgumentsValidator();
        boolean result = validator.isValid(map);
        assertThat(result).isFalse();
        assertThat(validator.details()).hasSize(1);
        assertThat(validator.details().get(0)).isEqualTo("Illegal value of argument 't'");

    }

    @Test
    void illegalSizeArgument() {
        Map<Character, String> map = new HashMap<>();
        map.put('u',"99");
        map.put('t', "45.1");
        map.put('s', "100.4");

        ArgumentsValidator validator = new ArgumentsValidator();
        boolean result = validator.isValid(map);
        assertThat(result).isFalse();
        assertThat(validator.details()).hasSize(1);
        assertThat(validator.details().get(0)).isEqualTo("Illegal value of argument 's'");

    }

}