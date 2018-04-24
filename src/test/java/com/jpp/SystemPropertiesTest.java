package com.jpp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SystemPropertiesTest
{
    @Test
    void getAprsHostTest()
    {
        String host = SystemProperties.getAprsHost();
        Assertions.assertEquals(0, host.compareTo("rotate.aprs2.net"));

        int port = SystemProperties.getAprsPort();
        Assertions.assertEquals(14580, port);

        String user = SystemProperties.getAprsUser();
        Assertions.assertEquals(0, user.compareTo("w5uvo"));

        int radius = SystemProperties.getAprsRadius();
        Assertions.assertEquals(25, radius);

    }
}
