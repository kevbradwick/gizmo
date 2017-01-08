package com.github.kevbradwick.gizmo.core;

class Util {

    static void assertNotNull(Object var, String errorMessage) {
        if (var == null) {
            throw new RuntimeException(errorMessage);
        }
    }
}
