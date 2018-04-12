package com.jpp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AprsPosMicECurTest
{

    @Test
    void parse()
    {
        AprsPosMicECur pos = new AprsPosMicECur();

        pos.parse("U8RSRT", "'>Bp___r/");     // Arizona, antenna
        Assertions.assertEquals(0, pos.getLatitude().compareTo("+58.2324"));
        Assertions.assertEquals(0, pos.getLongitude().compareTo("-134.3884"));
        Assertions.assertEquals(0, pos.getSymbol().compareTo("antenna"));

        pos.parse("S3SR0V", "`kW2___>-");     // South Carolina, car
        Assertions.assertEquals(0, pos.getLatitude().compareTo("+33.3206"));
        Assertions.assertEquals(0, pos.getLongitude().compareTo("-079.5922"));
        Assertions.assertEquals(0, pos.getSymbol().compareTo("car"));

        pos.parse("TRQV4P", "'cx&___j/");     // South Carolina, jeep
        Assertions.assertEquals(0, pos.getLatitude().compareTo("+42.1640"));
        Assertions.assertEquals(0, pos.getLongitude().compareTo("-071.3210"));
        Assertions.assertEquals(0, pos.getSymbol().compareTo("jeep"));

        pos.parse("TR3Y9R", "`c'dmm5>/\"4H");   // escaped ", the \ is for string formatting, not data
        Assertions.assertEquals(0, pos.getLatitude().compareTo("+42.3992"));    // aprs.fi decodes as +42.6653
        Assertions.assertEquals(0, pos.getLongitude().compareTo("-071.1172"));  //  and -071.1953
        Assertions.assertEquals(0, pos.getSymbol().compareTo("car"));

        pos.parse("TR3S0R-2", "`c`2rx_>/\"4:");   // escaped ", the \ is for string formatting, not data
        Assertions.assertEquals(0, pos.getLatitude().compareTo("+42.5503"));
        Assertions.assertEquals(0, pos.getLongitude().compareTo("-071.1370"));
        Assertions.assertEquals(0, pos.getSymbol().compareTo("car"));


    }
}