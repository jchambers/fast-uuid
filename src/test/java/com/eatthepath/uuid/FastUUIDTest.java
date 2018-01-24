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

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class FastUUIDTest {

    @Test
    public void testParseUUID() {
        final UUID uuid = UUID.randomUUID();
        assertEquals(uuid, FastUUID.parseUUID(uuid.toString()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseUUIDWrongLength() {
        FastUUID.parseUUID(UUID.randomUUID().toString() + "0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseUUIDNoDashes() {
        FastUUID.parseUUID("123456789012345678901234567890123456");
    }

    @Test
    public void testToString() {
        final UUID uuid = UUID.randomUUID();
        assertEquals(uuid.toString(), FastUUID.toString(uuid));
    }

    @Test
    public void testGetHexValueForChar() {
        assertEquals(0, FastUUID.getHexValueForChar('0'));
        assertEquals(1, FastUUID.getHexValueForChar('1'));
        assertEquals(2, FastUUID.getHexValueForChar('2'));
        assertEquals(3, FastUUID.getHexValueForChar('3'));
        assertEquals(4, FastUUID.getHexValueForChar('4'));
        assertEquals(5, FastUUID.getHexValueForChar('5'));
        assertEquals(6, FastUUID.getHexValueForChar('6'));
        assertEquals(7, FastUUID.getHexValueForChar('7'));
        assertEquals(8, FastUUID.getHexValueForChar('8'));
        assertEquals(9, FastUUID.getHexValueForChar('9'));

        assertEquals(10, FastUUID.getHexValueForChar('a'));
        assertEquals(11, FastUUID.getHexValueForChar('b'));
        assertEquals(12, FastUUID.getHexValueForChar('c'));
        assertEquals(13, FastUUID.getHexValueForChar('d'));
        assertEquals(14, FastUUID.getHexValueForChar('e'));
        assertEquals(15, FastUUID.getHexValueForChar('f'));

        assertEquals(10, FastUUID.getHexValueForChar('A'));
        assertEquals(11, FastUUID.getHexValueForChar('B'));
        assertEquals(12, FastUUID.getHexValueForChar('C'));
        assertEquals(13, FastUUID.getHexValueForChar('D'));
        assertEquals(14, FastUUID.getHexValueForChar('E'));
        assertEquals(15, FastUUID.getHexValueForChar('F'));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetHexValueForCharIllegalChar() {
        FastUUID.getHexValueForChar('x');
    }
}
