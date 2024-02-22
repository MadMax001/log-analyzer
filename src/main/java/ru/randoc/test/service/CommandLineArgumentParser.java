package ru.randoc.test.service;

import java.util.*;

public class CommandLineArgumentParser implements ArgumentParser {
    @Override
    public Map<Character, String> parse(String[] args) throws IllegalArgumentException {
        if (args.length % 2 != 0)
            throw new IllegalArgumentException("Arguments must consist from pair {-key value}");
        Map<Character, String> map = new HashMap<>();
        Character key = null;
        for (int i = 0; i < args.length; i++) {
              if (i % 2 == 0)
                  key = parseKey(args[i]);
              else
                  map.put(key, args[i]);
        }
        return map;
    }

    private Character parseKey(String arg) {
        if (arg.length() != 2 && arg.charAt(0) != '-')
            throw new IllegalArgumentException("Arguments must starts with '-'");
        return arg.charAt(1);
    }
}
