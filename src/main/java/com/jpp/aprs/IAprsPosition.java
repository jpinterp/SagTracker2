package com.jpp.aprs;

import java.time.LocalDateTime;

public interface IAprsPosition
{
    boolean getIsPositionPacket();
    LocalDateTime getTime();
    String getLatitude();
    String getLongitude();
    String getSymbol();
}
