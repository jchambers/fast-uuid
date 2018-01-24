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

    private static final int UUID_STRING_LENGTH = 36;

    private static final char[] HEX_DIGITS =
            new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static int[] HEX_VALUE = new int[128];
    static {
        HEX_VALUE['1'] = 1;
        HEX_VALUE['2'] = 2;
        HEX_VALUE['3'] = 3;
        HEX_VALUE['4'] = 4;
        HEX_VALUE['5'] = 5;
        HEX_VALUE['6'] = 6;
        HEX_VALUE['7'] = 7;
        HEX_VALUE['8'] = 8;
        HEX_VALUE['9'] = 9;
        HEX_VALUE['a'] = 10;
        HEX_VALUE['A'] = 10;
        HEX_VALUE['b'] = 11;
        HEX_VALUE['B'] = 11;
        HEX_VALUE['c'] = 12;
        HEX_VALUE['C'] = 12;
        HEX_VALUE['d'] = 13;
        HEX_VALUE['D'] = 13;
        HEX_VALUE['e'] = 14;
        HEX_VALUE['E'] = 14;
        HEX_VALUE['f'] = 15;
        HEX_VALUE['F'] = 15;
    }

    private FastUUID() {
        // A private constructor prevents callers from accidentally instantiating FastUUID instances
    }

    /**
     * Parses a UUID from the given character sequence. The character sequence must represent a UUID as described in
     * {@link UUID#toString()}.
     *
     * @param uuidSequence the character sequence from which to parse a UUID
     * @return the UUID represented by the given character sequence
     * @throws IllegalArgumentException if the given character sequence does not conform to the string representation as
     *                                  described in {@link UUID#toString()}
     */
    public static UUID parseUUID(final String uuidSequence) {
        if (uuidSequence.length() != UUID_STRING_LENGTH) {
            throw new IllegalArgumentException("Could not parse UUID string: " + uuidSequence);
        }

        //final char[] ch = uuidSequence.toCharArray();
        long mostSignificantBits = HEX_VALUE[uuidSequence.charAt(0)];
        mostSignificantBits <<= 4;
        mostSignificantBits |= HEX_VALUE[uuidSequence.charAt(1)];
        mostSignificantBits <<= 4;
        mostSignificantBits |= HEX_VALUE[uuidSequence.charAt(2)];
        mostSignificantBits <<= 4;
        mostSignificantBits |= HEX_VALUE[uuidSequence.charAt(3)];
        mostSignificantBits <<= 4;
        mostSignificantBits |= HEX_VALUE[uuidSequence.charAt(4)];
        mostSignificantBits <<= 4;
        mostSignificantBits |= HEX_VALUE[uuidSequence.charAt(5)];
        mostSignificantBits <<= 4;
        mostSignificantBits |= HEX_VALUE[uuidSequence.charAt(6)];
        mostSignificantBits <<= 4;
        mostSignificantBits |= HEX_VALUE[uuidSequence.charAt(7)];

        mostSignificantBits <<= 4;
        mostSignificantBits |= HEX_VALUE[uuidSequence.charAt(9)];
        mostSignificantBits <<= 4;
        mostSignificantBits |= HEX_VALUE[uuidSequence.charAt(10)];
        mostSignificantBits <<= 4;
        mostSignificantBits |= HEX_VALUE[uuidSequence.charAt(11)];
        mostSignificantBits <<= 4;
        mostSignificantBits |= HEX_VALUE[uuidSequence.charAt(12)];

        mostSignificantBits <<= 4;
        mostSignificantBits |= HEX_VALUE[uuidSequence.charAt(14)];
        mostSignificantBits <<= 4;
        mostSignificantBits |= HEX_VALUE[uuidSequence.charAt(15)];
        mostSignificantBits <<= 4;
        mostSignificantBits |= HEX_VALUE[uuidSequence.charAt(16)];
        mostSignificantBits <<= 4;
        mostSignificantBits |= HEX_VALUE[uuidSequence.charAt(17)];

        long leastSignificantBits = HEX_VALUE[uuidSequence.charAt(19)];
        leastSignificantBits <<= 4;
        leastSignificantBits |= HEX_VALUE[uuidSequence.charAt(20)];
        leastSignificantBits <<= 4;
        leastSignificantBits |= HEX_VALUE[uuidSequence.charAt(21)];
        leastSignificantBits <<= 4;
        leastSignificantBits |= HEX_VALUE[uuidSequence.charAt(22)];

        leastSignificantBits <<= 4;
        leastSignificantBits |= HEX_VALUE[uuidSequence.charAt(24)];
        leastSignificantBits <<= 4;
        leastSignificantBits |= HEX_VALUE[uuidSequence.charAt(25)];
        leastSignificantBits <<= 4;
        leastSignificantBits |= HEX_VALUE[uuidSequence.charAt(26)];
        leastSignificantBits <<= 4;
        leastSignificantBits |= HEX_VALUE[uuidSequence.charAt(27)];
        leastSignificantBits <<= 4;
        leastSignificantBits |= HEX_VALUE[uuidSequence.charAt(28)];
        leastSignificantBits <<= 4;
        leastSignificantBits |= HEX_VALUE[uuidSequence.charAt(29)];
        leastSignificantBits <<= 4;
        leastSignificantBits |= HEX_VALUE[uuidSequence.charAt(30)];
        leastSignificantBits <<= 4;
        leastSignificantBits |= HEX_VALUE[uuidSequence.charAt(31)];
        leastSignificantBits <<= 4;
        leastSignificantBits |= HEX_VALUE[uuidSequence.charAt(32)];
        leastSignificantBits <<= 4;
        leastSignificantBits |= HEX_VALUE[uuidSequence.charAt(33)];
        leastSignificantBits <<= 4;
        leastSignificantBits |= HEX_VALUE[uuidSequence.charAt(34)];
        leastSignificantBits <<= 4;
        leastSignificantBits |= HEX_VALUE[uuidSequence.charAt(35)];

        return new UUID(mostSignificantBits, leastSignificantBits);
    }

    /**
     * Returns a string representation of the given UUID. The returned string is formatted as described in
     * {@link UUID#toString()}.
     *
     * @param uuid the UUID to represent as a string
     * @return a string representation of the given UUID
     */
    public static String toString(final UUID uuid) {
        final long mostSignificantBits = uuid.getMostSignificantBits();
        final long leastSignificantBits = uuid.getLeastSignificantBits();

        final char[] uuidChars = new char[UUID_STRING_LENGTH];

        uuidChars[0] = HEX_DIGITS[(int) ((mostSignificantBits & 0xf000000000000000L) >>> 60)];
        uuidChars[1] = HEX_DIGITS[(int) ((mostSignificantBits & 0x0f00000000000000L) >>> 56)];
        uuidChars[2] = HEX_DIGITS[(int) ((mostSignificantBits & 0x00f0000000000000L) >>> 52)];
        uuidChars[3] = HEX_DIGITS[(int) ((mostSignificantBits & 0x000f000000000000L) >>> 48)];
        uuidChars[4] = HEX_DIGITS[(int) ((mostSignificantBits & 0x0000f00000000000L) >>> 44)];
        uuidChars[5] = HEX_DIGITS[(int) ((mostSignificantBits & 0x00000f0000000000L) >>> 40)];
        uuidChars[6] = HEX_DIGITS[(int) ((mostSignificantBits & 0x000000f000000000L) >>> 36)];
        uuidChars[7] = HEX_DIGITS[(int) ((mostSignificantBits & 0x0000000f00000000L) >>> 32)];
        uuidChars[8] = '-';
        uuidChars[9] = HEX_DIGITS[(int) ((mostSignificantBits & 0x00000000f0000000L) >>> 28)];
        uuidChars[10] = HEX_DIGITS[(int) ((mostSignificantBits & 0x000000000f000000L) >>> 24)];
        uuidChars[11] = HEX_DIGITS[(int) ((mostSignificantBits & 0x0000000000f00000L) >>> 20)];
        uuidChars[12] = HEX_DIGITS[(int) ((mostSignificantBits & 0x00000000000f0000L) >>> 16)];
        uuidChars[13] = '-';
        uuidChars[14] = HEX_DIGITS[(int) ((mostSignificantBits & 0x000000000000f000L) >>> 12)];
        uuidChars[15] = HEX_DIGITS[(int) ((mostSignificantBits & 0x0000000000000f00L) >>> 8)];
        uuidChars[16] = HEX_DIGITS[(int) ((mostSignificantBits & 0x00000000000000f0L) >>> 4)];
        uuidChars[17] = HEX_DIGITS[(int) (mostSignificantBits & 0x000000000000000fL)];
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
        uuidChars[35] = HEX_DIGITS[(int) (leastSignificantBits & 0x000000000000000fL)];

        return new String(uuidChars);
    }
}

