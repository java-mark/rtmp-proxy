package org.mark.rtmp.proxy.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommonTest {
    @Test
    void readIntMedium() {
        byte[] input = new byte[]{10, 11, 11};
        assertEquals(658187, Common.readIntMedium(input));
        assertEquals(723722, Common.readIntMediumLE(input));
    }
}