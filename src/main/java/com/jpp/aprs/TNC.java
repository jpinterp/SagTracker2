package com.jpp.aprs;

//
// TNC2 packet, from the APRS Specification, page 82:
//
// -----------------------------------------------------------------
// |          |   |             | 0-8 Digipeaters |   |            |
// |          |   |             |-----------------|   |            |
// |  Source  |   | Destination |   | Digipeater  |   |   Payload  |
// | Callsign | > |  Callsign   | , | Callsigns   | : |            |
// | (-SSID)  |   |  (-SSID)    |   | (-SSID)(*)  |   |            |
// |----------|---|-------------|---|-------------|---|            |
// |   1-9    | 1 |     1-9     |       0-81      | 1 |            |
// -----------------------------------------------------------------

public class TNC
{
    public String header;
    public String source;
    public String destination;
    public String digipeater;
    public String payload;
    public boolean isValidPacket;

    public TNC(String packet)
    {
        parse(packet);
    }

    public void parse(String packet)
    {
        isValidPacket = false;
        if (packet.charAt(0) != '#')    // ignore comment packets
        {
            isValidPacket = true;
            String[] t = packet.split(":");
            header = t[0];
            payload = t[1];

            t = header.split(">");
            source = t[0];

            String[] t2 = t[1].split(",");
            destination = t2[0];
            digipeater = t2[1];
        }
    }
}

