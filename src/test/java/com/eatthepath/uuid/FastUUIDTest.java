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

    @Test
    public void testToString() {
        final UUID uuid = UUID.randomUUID();
        assertEquals(uuid.toString(), FastUUID.toString(uuid));
    }
    
}

