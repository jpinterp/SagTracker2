package com.jpp;


/**
 * APRS Position format
 * ---------------------------------------------------------
 * | /  | Time   |     |  Sym  |      | Sym  |             |
 * | or | DHS or | Lat | Table | Long | Code | Comment     |
 * | @  |  HMS   |     |  ID   |      |      |             |
 * |----|--------|-----|-------|------|------|-------------|
 * | 1  |   7    |  8  |   1   |   9  |  1   | 0-43        |
 * ---------------------------------------------------------
 * 0    1        8    16      17     26     27
 *
 *  The APRS position string is passed in a TNC payload
 */



public class AprsPosTimestamp extends AprsPositionBase implements IAprsPosition
{
    public AprsPosTimestamp()
    {
    }

    public AprsPosTimestamp(String position)
    {
        parse(position);
    }

    /**
     * Parses an APRS position string into individual components.
     *
     * Examples:
     *   @090330z4224.38N/07131.63W>160/001APRS Mobile
     *   @090330z4209.62N/07145.89W_.../000g000t028r000p009P007b10243h93.weewx-3.4.0-Vantage
     *
     * @param position APRS position string as shown in examples
     */
    // TODO: Handle compressed data formats
    // TODO: Handle ambiguous coordinates
    public void parse(String position)
    {
        isPositionPacket = false;
        if ((position != null) && (position.length() >= 26))
        {
            // APRS position messages begin with @ or / symbol
            char dataTypeId = position.charAt(0);
            if ((dataTypeId == '@') || (dataTypeId == '/'))
            {
                isPositionPacket = true;
                time = convertTime(position.substring(1, 8));
                latitude = convertLatitude(position.substring(8, 16));
                tableId = position.charAt(16);
                longitude = convertLongitude(position.substring(17, 26));
                symbolCode = position.charAt(26);
                symbol = getSymbol(tableId, symbolCode);
            }
        }
    }
}
