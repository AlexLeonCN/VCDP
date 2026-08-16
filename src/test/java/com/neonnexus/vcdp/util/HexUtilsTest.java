package com.neonnexus.vcdp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HexUtilsTest {

    @Test
    void stripPrefixShouldAcceptOptionalPrefixAndUppercase() {
        assertEquals("ABC", HexUtils.stripPrefix("abc"));
        assertEquals("ABC", HexUtils.stripPrefix("0xabc"));
        assertEquals("ABC", HexUtils.stripPrefix("0Xabc"));
        assertEquals("1000", HexUtils.stripPrefix(" 0x1000 "));
    }

    @Test
    void stripPrefixShouldRejectInvalidHex() {
        assertNull(HexUtils.stripPrefix(null));
        assertNull(HexUtils.stripPrefix(""));
        assertNull(HexUtils.stripPrefix("   "));
        assertNull(HexUtils.stripPrefix("0x"));
        assertNull(HexUtils.stripPrefix("0X"));
        assertNull(HexUtils.stripPrefix("xyz"));
        assertNull(HexUtils.stripPrefix("0xGG"));
        assertNull(HexUtils.stripPrefix("10 00"));
    }

    @Test
    void isValidShouldMatchLegalHexStrings() {
        assertTrue(HexUtils.isValid("1"));
        assertTrue(HexUtils.isValid("0"));
        assertTrue(HexUtils.isValid("0x1000"));
        assertFalse(HexUtils.isValid(""));
        assertFalse(HexUtils.isValid("0x"));
        assertFalse(HexUtils.isValid("hello"));
    }

    @Test
    void normalizeShouldAlwaysAdd0xPrefix() {
        assertEquals("0xABC", HexUtils.normalize("abc"));
        assertEquals("0xABC", HexUtils.normalize("0xabc"));
        assertEquals("0x1000", HexUtils.normalize("1000"));
        assertNull(HexUtils.normalize("not-hex"));
    }

    @Test
    void isPositiveShouldRejectZeroAndInvalidValues() {
        assertTrue(HexUtils.isPositive("0x1"));
        assertTrue(HexUtils.isPositive("0x1000"));
        assertFalse(HexUtils.isPositive("0x0"));
        assertFalse(HexUtils.isPositive("0x00"));
        assertFalse(HexUtils.isPositive(null));
        assertFalse(HexUtils.isPositive("1000"));
        assertFalse(HexUtils.isPositive("abc"));
    }
}
