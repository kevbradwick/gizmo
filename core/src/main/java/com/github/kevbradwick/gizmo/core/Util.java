package com.github.kevbradwick.gizmo.core;

public class Util {

    public static void assertNotNull(Object var, String errorMessage) {
        if (var == null) {
            throw new RuntimeException(errorMessage);
        }
    }
}
