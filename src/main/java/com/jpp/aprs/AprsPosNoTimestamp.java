package com.jpp.aprs;


import java.time.LocalDateTime;

/**
 * APRS Position format
 * ------------------------------------------------
 * | !  |     |  Sym  |      | Sym  |             |
 * | or | Lat | Table | Long | Code | Comment     |
 * | =  |     |  ID   |      |      |             |
 * |----|-----|-------|------|------|-------------|
 * | 1  |  8  |   1   |   9  |  1   | 0-43        |
 * ------------------------------------------------
 * 0    1     8       9      18     19
 *
 *  The APRS position string is passed in a TNC payload
 */

public class AprsPosNoTimestamp extends AprsPositionBase implements IAprsPosition
{
    public AprsPosNoTimestamp()
    {
    }

    public AprsPosNoTimestamp(String position)
    {
        parse(position);
    }

    /**
     * Parses an APRS position string into individual components.
     *
     * Examples:
     *   !4903.50N/07201.75W-Test 001234      no timestamp, no APRS messaging, with comment.
     *   !4903.50N/07201.75W-Test /A=001234
     *
     * @param position APRS position string as shown in examples
     */
    // TODO: Handle compressed data formats
    // TODO: Handle ambiguous coordinates
    public void parse(String position)
    {
        isPositionPacket = false;
        if ((position != null) && (position.length() >= 20))
        {
            // APRS position messages begin with ! or = symbol
            char dataTypeId = position.charAt(0);
            if ((dataTypeId == '!') || (dataTypeId == '='))
            {
                isPositionPacket = true;
                latitude = convertLatitude(position.substring(1, 9));
                tableId = position.charAt(9);
                longitude = convertLongitude(position.substring(10, 19));
                symbolCode = position.charAt(19);
                symbol = getSymbol(tableId, symbolCode);

                // Use the local time as a fake timestamp
                time = LocalDateTime.now();
            }
        }
    }
}
