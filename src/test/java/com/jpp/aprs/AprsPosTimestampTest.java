package com.jpp.aprs;

import com.jpp.aprs.AprsPosTimestamp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AprsPosTimestampTest
{

    @Test
    void parseTest()
    {
        AprsPosTimestamp pos = new AprsPosTimestamp();

        pos.parse("/090330z4224.38N/07131.63W>160/001APRS");
        Assertions.assertTrue(pos.getIsPositionPacket());
        Assertions.assertEquals(0, pos.getLatitude().compareTo("+42.2438"));
        Assertions.assertEquals(0, pos.getLongitude().compareTo("-071.3163"));
        Assertions.assertEquals(0, pos.getSymbol().compareTo("car"));

        pos.parse("@090330z4209.62N/07145.89W_.../000g000t028r000p009P007b10243h93.weewx-3.4.0-Vantage");
        Assertions.assertTrue(pos.getIsPositionPacket());
        Assertions.assertEquals(0, pos.getLatitude().compareTo("+42.0962"));
        Assertions.assertEquals(0, pos.getLongitude().compareTo("-071.4589"));
        Assertions.assertEquals(0, pos.getSymbol().compareTo("weather station"));

        pos.parse("@090330z4209.62S-07145.89E>.../000g000t028r000p009P007b10243h93.weewx-3.4.0-Vantage");
        Assertions.assertTrue(pos.getIsPositionPacket());
        Assertions.assertEquals(0, pos.getLatitude().compareTo("-42.0962"));
        Assertions.assertEquals(0, pos.getLongitude().compareTo("+071.4589"));
        Assertions.assertEquals(0, pos.getSymbol().compareTo("car"));

        // Ambiguous coordinates with spaces (decimal required per APRS spec pg 24)
        pos.parse("@090330z4209.  S-071  .  E>.../000g000t028r000p009P007b10243h93.weewx-3.4.0-Vantage");
        Assertions.assertTrue(pos.getIsPositionPacket());
        Assertions.assertEquals(0, pos.getLatitude().compareTo("-42.0900"));
        Assertions.assertEquals(0, pos.getLongitude().compareTo("+071.0000"));
        Assertions.assertEquals(0, pos.getSymbol().compareTo("car"));

        pos.parse("@ too short");
        Assertions.assertFalse(pos.getIsPositionPacket());

        // Invalid packet data id
        pos.parse("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        Assertions.assertFalse(pos.getIsPositionPacket());
    }
}