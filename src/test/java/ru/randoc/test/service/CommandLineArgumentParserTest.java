package ru.randoc.test.service;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CommandLineArgumentParserTest {

    @Test
    void twoArguments() {
        String[] line = new String[]{
                "-u", "99.9",
                "-t", "45.1"
        };
        ArgumentParser parser = new CommandLineArgumentParser();
        Map<Character, String> parsedParams = parser.parse(line);
        assertThat(parsedParams)
                .isNotNull()
                .hasSize(2)
                .containsEntry('u', "99.9")
                .containsEntry('t', "45.1");
    }

    @Test
    void oneArgumentWithoutValue() {
        String[] line = new String[]{
                "-u", "99.9",
                "-t", "45.1",
                "-b"
        };
        ArgumentParser parser = new CommandLineArgumentParser();
        assertThatThrownBy(() -> parser.parse(line))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Arguments must consist from pair {-key value}");
    }

    @Test
    void argumentWithoutMinus() {
        String[] line = new String[]{
                "-u", "99.9",
                "t", "45.1"
        };
        ArgumentParser parser = new CommandLineArgumentParser();
        assertThatThrownBy(() -> parser.parse(line))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Arguments must starts with '-'");
    }

}