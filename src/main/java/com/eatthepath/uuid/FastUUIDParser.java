package com.eatthepath.uuid;

import java.util.UUID;

public class FastUUIDParser {

    public static UUID parseUUID(final String uuidString) {
        if (uuidString.length() != 36) {
            throw new IllegalArgumentException("Could not parse UUID string: " + uuidString);
        }

        long mostSignificantBits = 0;

        for (int i = 0; i < 18; i++) {
            if (i == 8 || i == 13) {
                // Skip over hyphens
                continue;
            }

            mostSignificantBits <<= 4;
            mostSignificantBits |= getHexValueForChar(uuidString.charAt(i));
        }

        long leastSignificantBits = 0;

        for (int i = 19; i < 36; i++) {
            if (i == 23) {
                // Skip over hyphens
                continue;
            }

            leastSignificantBits <<= 4;
            leastSignificantBits |= getHexValueForChar(uuidString.charAt(i));
        }

        return new UUID(mostSignificantBits, leastSignificantBits);
    }

    static int getHexValueForChar(final char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        } else if (c >= 'a' && c <= 'f') {
            return c - 87;
        } else if (c >= 'A' && c <= 'F') {
            return c - 55;
        } else {
            throw new IllegalArgumentException("Illegal hexadecimal digit: " + c);
        }
    }
}
