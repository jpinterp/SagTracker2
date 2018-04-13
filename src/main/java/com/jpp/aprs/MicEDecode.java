package com.jpp;

public class MicEDecode
{
    public char latDigit;           // Latitude digit (0-9)
    public byte msg;                // A/B/C bit of message
    public boolean isSouth;         // true if south, false if north
    public int longOffset;          // Longitude offset, either +0 or +100
    public boolean isEast;          // true if east, false if west

    public MicEDecode(char latDigit, byte msg, boolean isSouth, int longOffset, boolean isEast)
    {
        this.latDigit = latDigit;
        this.msg = msg;
        this.isSouth = isSouth;
        this.longOffset = longOffset;
        this.isEast = isEast;
    }
}
