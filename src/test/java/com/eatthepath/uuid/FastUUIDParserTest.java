package com.eatthepath.uuid;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class FastUUIDParserTest {

    @Test
    public void testParseUUID() {
        final UUID uuid = UUID.randomUUID();
        assertEquals(uuid, FastUUIDParser.parseUUID(uuid.toString()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseUUIDWrongLength() {
        FastUUIDParser.parseUUID(UUID.randomUUID().toString() + "0");
    }

    @Test
    public void testToString() {
        final UUID uuid = UUID.randomUUID();
        assertEquals(uuid.toString(), FastUUIDParser.toString(uuid));
    }

    @Test
    public void testGetHexValueForChar() {
        assertEquals(0, FastUUIDParser.getHexValueForChar('0'));
        assertEquals(1, FastUUIDParser.getHexValueForChar('1'));
        assertEquals(2, FastUUIDParser.getHexValueForChar('2'));
        assertEquals(3, FastUUIDParser.getHexValueForChar('3'));
        assertEquals(4, FastUUIDParser.getHexValueForChar('4'));
        assertEquals(5, FastUUIDParser.getHexValueForChar('5'));
        assertEquals(6, FastUUIDParser.getHexValueForChar('6'));
        assertEquals(7, FastUUIDParser.getHexValueForChar('7'));
        assertEquals(8, FastUUIDParser.getHexValueForChar('8'));
        assertEquals(9, FastUUIDParser.getHexValueForChar('9'));

        assertEquals(10, FastUUIDParser.getHexValueForChar('a'));
        assertEquals(11, FastUUIDParser.getHexValueForChar('b'));
        assertEquals(12, FastUUIDParser.getHexValueForChar('c'));
        assertEquals(13, FastUUIDParser.getHexValueForChar('d'));
        assertEquals(14, FastUUIDParser.getHexValueForChar('e'));
        assertEquals(15, FastUUIDParser.getHexValueForChar('f'));

        assertEquals(10, FastUUIDParser.getHexValueForChar('A'));
        assertEquals(11, FastUUIDParser.getHexValueForChar('B'));
        assertEquals(12, FastUUIDParser.getHexValueForChar('C'));
        assertEquals(13, FastUUIDParser.getHexValueForChar('D'));
        assertEquals(14, FastUUIDParser.getHexValueForChar('E'));
        assertEquals(15, FastUUIDParser.getHexValueForChar('F'));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetHexValueForCharIllegalChar() {
        FastUUIDParser.getHexValueForChar('x');
    }
}