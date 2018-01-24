/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Jon Chambers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.eatthepath.uuid;

import java.util.Random;
import java.util.UUID;

/**
 * <p>A utility class for quickly and efficiently parsing {@link java.util.UUID} instances from strings and writing UUID
 * instances as strings. The methods contained in this class are optimized for speed and to minimize garbage collection
 * pressure. In benchmarks, {@link #parseUUID(CharSequence)} is a little more than four times faster than
 * {@link UUID#fromString(String)}, and {@link #toString(UUID)} is a little more than six times faster than
 * {@link UUID#toString()}.</p>
 *
 * @author <a href="https://github.com/jchambers/">Jon Chambers</a>
 */
public class FastUUID {

    private static final boolean USE_JDK_UUID_TO_STRING;

    static {
        int majorVersion = 0;

        try {
            majorVersion = Integer.parseInt(System.getProperty("java.specification.version"));
        } catch (final NumberFormatException ignored) {
            // This will happen for pretty much anything before Java 9, which had a version scheme like "1.8" instead of
            // just "8".
        }

        USE_JDK_UUID_TO_STRING = majorVersion >= 9;
    }

    private static final int UUID_STRING_LENGTH = 36;

    private static final char[] HEX_DIGITS =
            new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static final long[] HEX_VALUES = new long[128];

    static {
        for (int i = 0; i < HEX_VALUES.length; i++) {
            HEX_VALUES[i] = -1;
        }

        HEX_VALUES['0'] = 0x0;
        HEX_VALUES['1'] = 0x1;
        HEX_VALUES['2'] = 0x2;
        HEX_VALUES['3'] = 0x3;
        HEX_VALUES['4'] = 0x4;
        HEX_VALUES['5'] = 0x5;
        HEX_VALUES['6'] = 0x6;
        HEX_VALUES['7'] = 0x7;
        HEX_VALUES['8'] = 0x8;
        HEX_VALUES['9'] = 0x9;

        HEX_VALUES['a'] = 0xa;
        HEX_VALUES['b'] = 0xb;
        HEX_VALUES['c'] = 0xc;
        HEX_VALUES['d'] = 0xd;
        HEX_VALUES['e'] = 0xe;
        HEX_VALUES['f'] = 0xf;

        HEX_VALUES['A'] = 0xa;
        HEX_VALUES['B'] = 0xb;
        HEX_VALUES['C'] = 0xc;
        HEX_VALUES['D'] = 0xd;
        HEX_VALUES['E'] = 0xe;
        HEX_VALUES['F'] = 0xf;
    }

    private FastUUID() {
        // A private constructor prevents callers from accidentally instantiating FastUUID instances
    }

    /**
     * Parses a UUID from the given character sequence. The character sequence must represent a UUID as described in
     * {@link UUID#toString()}.
     *
     * @param uuidSequence the character sequence from which to parse a UUID
     *
     * @return the UUID represented by the given character sequence
     *
     * @throws IllegalArgumentException if the given character sequence does not conform to the string representation as
     * described in {@link UUID#toString()}
     */
    public static UUID parseUUID(final CharSequence uuidSequence) {
        if (uuidSequence.length() != UUID_STRING_LENGTH ||
                uuidSequence.charAt(8) != '-' ||
                uuidSequence.charAt(13) != '-' ||
                uuidSequence.charAt(18) != '-' ||
                uuidSequence.charAt(23) != '-') {

            throw new IllegalArgumentException("Illegal UUID string: " + uuidSequence);
        }

        long mostSignificantBits = getHexValueForChar(uuidSequence.charAt(0)) << 60;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(1)) << 56;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(2)) << 52;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(3)) << 48;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(4)) << 44;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(5)) << 40;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(6)) << 36;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(7)) << 32;

        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(9)) << 28;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(10)) << 24;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(11)) << 20;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(12)) << 16;

        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(14)) << 12;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(15)) << 8;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(16)) << 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(17));

        long leastSignificantBits = getHexValueForChar(uuidSequence.charAt(19)) << 60;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(20)) << 56;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(21)) << 52;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(22)) << 48;

        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(24)) << 44;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(25)) << 40;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(26)) << 36;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(27)) << 32;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(28)) << 28;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(29)) << 24;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(30)) << 20;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(31)) << 16;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(32)) << 12;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(33)) << 8;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(34)) << 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(35));

        return new UUID(mostSignificantBits, leastSignificantBits);
    }

    /**
     * Returns a string representation of the given UUID. The returned string is formatted as described in
     * {@link UUID#toString()}.
     *
     * @param uuid the UUID to represent as a string
     *
     * @return a string representation of the given UUID
     */
    public static String toString(final UUID uuid) {
        if (USE_JDK_UUID_TO_STRING) {
            // OpenJDK 9 and newer use a fancy native approach to converting UUIDs to strings and we're better off using
            // that if it's available.
            return uuid.toString();
        }

        final long mostSignificantBits = uuid.getMostSignificantBits();
        final long leastSignificantBits = uuid.getLeastSignificantBits();

        return toString(mostSignificantBits, leastSignificantBits);
    }

    public static UUID randomUUID(final Random random) {
        final byte[] uuidBytes = getRandomUUIDBytes(random);

        final long mostSignificantBits = (uuidBytes[0] & 0xffL) << 56 |
                (uuidBytes[1] & 0xffL) << 48 |
                (uuidBytes[2] & 0xffL) << 40 |
                (uuidBytes[3] & 0xffL) << 32 |
                (uuidBytes[4] & 0xffL) << 24 |
                (uuidBytes[5] & 0xffL) << 16 |
                (uuidBytes[6] & 0xffL) << 8 |
                (uuidBytes[7] & 0xffL);

        final long leastSignificantBits = (uuidBytes[8] & 0xffL) << 56 |
                (uuidBytes[9]  & 0xffL) << 48 |
                (uuidBytes[10] & 0xffL) << 40 |
                (uuidBytes[11] & 0xffL) << 32 |
                (uuidBytes[12] & 0xffL) << 24 |
                (uuidBytes[13] & 0xffL) << 16 |
                (uuidBytes[14] & 0xffL) << 8 |
                (uuidBytes[15] & 0xffL);

        return new UUID(mostSignificantBits, leastSignificantBits);
    }

    /**
     * Returns a string representation of a type 4 (randomly-generated) UUID.
     *
     * @param random the random number source to use to generate a UUID
     *
     * @return a string representation of a randomly-generated UUID
     */
    public static String randomUUIDString(final Random random) {
        final byte[] uuidBytes = getRandomUUIDBytes(random);

        final long mostSignificantBits = (uuidBytes[0] & 0xffL) << 56 |
                (uuidBytes[1] & 0xffL) << 48 |
                (uuidBytes[2] & 0xffL) << 40 |
                (uuidBytes[3] & 0xffL) << 32 |
                (uuidBytes[4] & 0xffL) << 24 |
                (uuidBytes[5] & 0xffL) << 16 |
                (uuidBytes[6] & 0xffL) << 8 |
                (uuidBytes[7] & 0xffL);

        final long leastSignificantBits = (uuidBytes[8] & 0xffL) << 56 |
                (uuidBytes[9]  & 0xffL) << 48 |
                (uuidBytes[10] & 0xffL) << 40 |
                (uuidBytes[11] & 0xffL) << 32 |
                (uuidBytes[12] & 0xffL) << 24 |
                (uuidBytes[13] & 0xffL) << 16 |
                (uuidBytes[14] & 0xffL) << 8 |
                (uuidBytes[15] & 0xffL);

        return toString(mostSignificantBits, leastSignificantBits);
    }

    private static byte[] getRandomUUIDBytes(final Random random) {
        final byte[] uuidBytes = new byte[16];
        random.nextBytes(uuidBytes);

        // Clear and set the version to 4
        uuidBytes[6] &= 0x0f;
        uuidBytes[6] |= 0x40;

        // Clear and set the variant to IETF RFC 4122 (Leach-Salz)
        uuidBytes[8] &= 0x3f;
        uuidBytes[8] |= 0x80;

        return uuidBytes;
    }

    private static String toString(final long mostSignificantBits, final long leastSignificantBits) {
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
        uuidChars[19] = HEX_DIGITS[(int) ((leastSignificantBits & 0xf000000000000000L) >>> 60)];
        uuidChars[20] = HEX_DIGITS[(int) ((leastSignificantBits & 0x0f00000000000000L) >>> 56)];
        uuidChars[21] = HEX_DIGITS[(int) ((leastSignificantBits & 0x00f0000000000000L) >>> 52)];
        uuidChars[22] = HEX_DIGITS[(int) ((leastSignificantBits & 0x000f000000000000L) >>> 48)];
        uuidChars[23] = '-';
        uuidChars[24] = HEX_DIGITS[(int) ((leastSignificantBits & 0x0000f00000000000L) >>> 44)];
        uuidChars[25] = HEX_DIGITS[(int) ((leastSignificantBits & 0x00000f0000000000L) >>> 40)];
        uuidChars[26] = HEX_DIGITS[(int) ((leastSignificantBits & 0x000000f000000000L) >>> 36)];
        uuidChars[27] = HEX_DIGITS[(int) ((leastSignificantBits & 0x0000000f00000000L) >>> 32)];
        uuidChars[28] = HEX_DIGITS[(int) ((leastSignificantBits & 0x00000000f0000000L) >>> 28)];
        uuidChars[29] = HEX_DIGITS[(int) ((leastSignificantBits & 0x000000000f000000L) >>> 24)];
        uuidChars[30] = HEX_DIGITS[(int) ((leastSignificantBits & 0x0000000000f00000L) >>> 20)];
        uuidChars[31] = HEX_DIGITS[(int) ((leastSignificantBits & 0x00000000000f0000L) >>> 16)];
        uuidChars[32] = HEX_DIGITS[(int) ((leastSignificantBits & 0x000000000000f000L) >>> 12)];
        uuidChars[33] = HEX_DIGITS[(int) ((leastSignificantBits & 0x0000000000000f00L) >>> 8)];
        uuidChars[34] = HEX_DIGITS[(int) ((leastSignificantBits & 0x00000000000000f0L) >>> 4)];
        uuidChars[35] = HEX_DIGITS[(int)  (leastSignificantBits & 0x000000000000000fL)];

        return new String(uuidChars);
    }

    static long getHexValueForChar(final char c) {
        try {
            if (HEX_VALUES[c] < 0) {
                throw new IllegalArgumentException("Illegal hexadecimal digit: " + c);
            }
        } catch (final ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Illegal hexadecimal digit: " + c);
        }

        return HEX_VALUES[c];
    }
}
