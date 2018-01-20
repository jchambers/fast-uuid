package com.eatthepath.uuid;

import java.util.UUID;

public class FastUUIDParser {

    public static UUID parseUUID(final String uuidString) {
        if (uuidString.length() != 36) {
            throw new IllegalArgumentException("Could not parse UUID string: " + uuidString);
        }

        long mostSignificantBits = getHexValueForChar(uuidString.charAt(0));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidString.charAt(1));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidString.charAt(2));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidString.charAt(3));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidString.charAt(4));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidString.charAt(5));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidString.charAt(6));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidString.charAt(7));

        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidString.charAt(9));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidString.charAt(10));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidString.charAt(11));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidString.charAt(12));

        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidString.charAt(14));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidString.charAt(15));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidString.charAt(16));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidString.charAt(17));

        long leastSignificantBits = getHexValueForChar(uuidString.charAt(19));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidString.charAt(20));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidString.charAt(21));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidString.charAt(22));

        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidString.charAt(24));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidString.charAt(25));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidString.charAt(26));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidString.charAt(27));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidString.charAt(28));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidString.charAt(29));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidString.charAt(30));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidString.charAt(31));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidString.charAt(32));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidString.charAt(33));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidString.charAt(34));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidString.charAt(35));
        
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
