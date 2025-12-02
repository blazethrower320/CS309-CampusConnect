package com.example.androidexample;

public class TestUtils {
    /** Sleep for the specified milliseconds in tests */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}