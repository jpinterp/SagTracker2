package com.jpp.aprs;

import com.jpp.aprs.AprsPosNoTimestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AprsPosNoTimestampTest
{

    @Test
    void parse()
    {
        AprsPosNoTimestamp pos = new AprsPosNoTimestamp();

        // !4903.50N/07201.75W-Test 001234
        // !4903.50N/07201.75W-Test /A=001234

        pos.parse("!4903.50N/07201.75W-Test 001234");
        Assertions.assertTrue(pos.getIsPositionPacket());
        Assertions.assertEquals(0, pos.getLatitude().compareTo("+49.0350"));
        Assertions.assertEquals(0, pos.getLongitude().compareTo("-072.0175"));
        Assertions.assertEquals(0, pos.getSymbol().compareTo("house"));

        pos.parse("=4903.50N/07201.75W-Test /A=001234");
        Assertions.assertTrue(pos.getIsPositionPacket());
        Assertions.assertEquals(0, pos.getLatitude().compareTo("+49.0350"));
        Assertions.assertEquals(0, pos.getLongitude().compareTo("-072.0175"));
        Assertions.assertEquals(0, pos.getSymbol().compareTo("house"));

        pos.parse("! too short");
        Assertions.assertFalse(pos.getIsPositionPacket());

        // Invalid packet data id
        pos.parse("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        Assertions.assertFalse(pos.getIsPositionPacket());

    }
}