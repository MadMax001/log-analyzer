package ru.randoc.test;

import ru.randoc.test.service.MainWorkflow;

public class Launcher
{
    public static void main( String[] args ) {
        new MainWorkflow(System.in, System.out)
                .start(args);

    }
}
