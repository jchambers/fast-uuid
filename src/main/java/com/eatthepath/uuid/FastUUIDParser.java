package com.eatthepath.uuid;

import java.util.UUID;

public class FastUUIDParser {

    private static final int UUID_STRING_LENGTH = 36;

    private static final char[] HEX_DIGITS =
            new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static UUID parseUUID(final CharSequence uuidSequence) {
        if (uuidSequence.length() != UUID_STRING_LENGTH) {
            throw new IllegalArgumentException("Could not parse UUID string: " + uuidSequence);
        }

        long mostSignificantBits = getHexValueForChar(uuidSequence.charAt(0));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(1));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(2));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(3));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(4));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(5));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(6));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(7));

        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(9));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(10));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(11));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(12));

        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(14));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(15));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(16));
        mostSignificantBits <<= 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(17));

        long leastSignificantBits = getHexValueForChar(uuidSequence.charAt(19));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(20));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(21));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(22));

        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(24));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(25));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(26));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(27));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(28));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(29));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(30));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(31));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(32));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(33));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(34));
        leastSignificantBits <<= 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(35));
        
        return new UUID(mostSignificantBits, leastSignificantBits);
    }

    public static String toString(final UUID uuid) {
        final long mostSignificantBits = uuid.getMostSignificantBits();
        final long leastSignificantBits = uuid.getLeastSignificantBits();

        final char[] uuidChars = new char[UUID_STRING_LENGTH];

        uuidChars[0]  = HEX_DIGITS[(int) ((mostSignificantBits & 0xf000000000000000L) >>> 60)];
        uuidChars[1]  = HEX_DIGITS[(int) ((mostSignificantBits & 0x0f00000000000000L) >>> 56)];
        uuidChars[2]  = HEX_DIGITS[(int) ((mostSignificantBits & 0x00f0000000000000L) >>> 52)];
        uuidChars[3]  = HEX_DIGITS[(int) ((mostSignificantBits & 0x000f000000000000L) >>> 48)];
        uuidChars[4]  = HEX_DIGITS[(int) ((mostSignificantBits & 0x0000f00000000000L) >>> 44)];
        uuidChars[5]  = HEX_DIGITS[(int) ((mostSignificantBits & 0x00000f0000000000L) >>> 40)];
        uuidChars[6]  = HEX_DIGITS[(int) ((mostSignificantBits & 0x000000f000000000L) >>> 36)];
        uuidChars[7]  = HEX_DIGITS[(int) ((mostSignificantBits & 0x0000000f00000000L) >>> 32)];
        uuidChars[8]  = '-';
        uuidChars[9]  = HEX_DIGITS[(int) ((mostSignificantBits & 0x00000000f0000000L) >>> 28)];
        uuidChars[10] = HEX_DIGITS[(int) ((mostSignificantBits & 0x000000000f000000L) >>> 24)];
        uuidChars[11] = HEX_DIGITS[(int) ((mostSignificantBits & 0x0000000000f00000L) >>> 20)];
        uuidChars[12] = HEX_DIGITS[(int) ((mostSignificantBits & 0x00000000000f0000L) >>> 16)];
        uuidChars[13] = '-';
        uuidChars[14] = HEX_DIGITS[(int) ((mostSignificantBits & 0x000000000000f000L) >>> 12)];
        uuidChars[15] = HEX_DIGITS[(int) ((mostSignificantBits & 0x0000000000000f00L) >>> 8)];
        uuidChars[16] = HEX_DIGITS[(int) ((mostSignificantBits & 0x00000000000000f0L) >>> 4)];
        uuidChars[17] = HEX_DIGITS[(int)  (mostSignificantBits & 0x000000000000000fL)];
        uuidChars[18] = '-';
        uuidChars[19]  = HEX_DIGITS[(int) ((leastSignificantBits & 0xf000000000000000L) >>> 60)];
        uuidChars[20]  = HEX_DIGITS[(int) ((leastSignificantBits & 0x0f00000000000000L) >>> 56)];
        uuidChars[21]  = HEX_DIGITS[(int) ((leastSignificantBits & 0x00f0000000000000L) >>> 52)];
        uuidChars[22]  = HEX_DIGITS[(int) ((leastSignificantBits & 0x000f000000000000L) >>> 48)];
        uuidChars[23] = '-';
        uuidChars[24]  = HEX_DIGITS[(int) ((leastSignificantBits & 0x0000f00000000000L) >>> 44)];
        uuidChars[25]  = HEX_DIGITS[(int) ((leastSignificantBits & 0x00000f0000000000L) >>> 40)];
        uuidChars[26]  = HEX_DIGITS[(int) ((leastSignificantBits & 0x000000f000000000L) >>> 36)];
        uuidChars[27]  = HEX_DIGITS[(int) ((leastSignificantBits & 0x0000000f00000000L) >>> 32)];
        uuidChars[28]  = HEX_DIGITS[(int) ((leastSignificantBits & 0x00000000f0000000L) >>> 28)];
        uuidChars[29] = HEX_DIGITS[(int) ((leastSignificantBits & 0x000000000f000000L) >>> 24)];
        uuidChars[30] = HEX_DIGITS[(int) ((leastSignificantBits & 0x0000000000f00000L) >>> 20)];
        uuidChars[31] = HEX_DIGITS[(int) ((leastSignificantBits & 0x00000000000f0000L) >>> 16)];
        uuidChars[32] = HEX_DIGITS[(int) ((leastSignificantBits & 0x000000000000f000L) >>> 12)];
        uuidChars[33] = HEX_DIGITS[(int) ((leastSignificantBits & 0x0000000000000f00L) >>> 8)];
        uuidChars[34] = HEX_DIGITS[(int) ((leastSignificantBits & 0x00000000000000f0L) >>> 4)];
        uuidChars[35] = HEX_DIGITS[(int)  (leastSignificantBits & 0x000000000000000fL)];

        return new String(uuidChars);
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
