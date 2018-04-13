package com.jpp.aprs;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;

public abstract class AprsPositionBase implements IAprsPosition
{
    protected boolean isPositionPacket = false;
    protected LocalDateTime time;
    protected char tableId = '\0';
    protected String latitude = null;
    protected String longitude = null;
    protected char symbolCode = '\0';
    protected String symbol = null;

    protected static HashMap<String, String> symbolMap = symbolMapInit();

    @Override
    public boolean getIsPositionPacket()
    {
        return isPositionPacket;
    }

    @Override
    public LocalDateTime getTime()
    {
        return time;
    }

    @Override
    public String getLatitude()
    {
        return latitude;
    }

    @Override
    public String getLongitude()
    {
        return longitude;
    }

    @Override
    public String getSymbol()
    {
        return symbol;
    }

    /**
     * Convert APRS latitude format into Google Maps' format:*    xxxx.xxN/S to +/-xx.xxxx
     *
     * Note: According to the APRS spec (page 24) ambiguous locations have spaces
     * instead of digits but the decimal point should always be in place.  For example:
     *  4903.5 N
     *  4903.  N
     *  490 .  N
     *  49  .  N
     *
     * @param position Latitude in N/S format
     * @return Latitude in +/- format
     */
    protected String convertLatitude(String position)
    {
        // Latitude:  North is +, South is -
        char northSouth = position.charAt(position.length()-1);

        String newPos = (northSouth == 'N') ? "+" : "-";

        for (int i = 0; i < position.length() - 1; i++)      // skip last char in lat string
        {
            char c = position.charAt(i);

            if (i == 2)     // need a decimal point before the 3rd digit
            {
                newPos = newPos.concat(".");
            }
            if (c == ' ')   // handle ambiguous cases
            {
                c = '0';
            }
            if (c != '.')   // skip decimal in original position string
            {
                newPos = newPos.concat(Character.toString(c));
            }

        }
        return newPos;
    }


    /***
     * Convert APRS longitude format into Google Maps' format:
     *     yyyyy.yyE/W to +/-yyy.yyyy
     *
     * Note: According to the APRS spec (page 24) ambiguous locations have spaces
     * instead of digits but the decimal point should always be in place.  For example:
     *  4903.5 N
     *  4903.  N
     *  490 .  N
     *  49  .  N
     *
     * @param position  Longitude in E/W format
     * @return Longitude in +/- format
     */
    protected String convertLongitude(String position)
    {
        // Longitude:  East is +, West is -
        char eastWest = position.charAt(position.length() - 1);

        String newPos = (eastWest == 'E') ? "+" : "-";

        for (int i = 0; i < position.length() - 1; i++)      // skip last char in lat string
        {
            char c = position.charAt(i);

            if (i == 3)     // need a decial point before the 4th digit
            {
                newPos = newPos.concat(".");
            }
            if (c == ' ')   // handle ambiguous cases
            {
                c = '0';
            }
            if (c != '.')   // skip decimal in original position string
            {
                newPos = newPos.concat(Character.toString(c));
            }
        }
        return newPos;
    }



    /***
     * Convert the APRS time into local time.  APRS has 4 possible time formats all determined by
     * the 7th character:
     *      z - DDhhmmz - two digit day, two digit hour, two digit minute, zulu time
     *      / - DDhhmm/ - two digit day, two digit hour, two digit minute, local time
     *      h - hhmmssh - two digit hour, two digit minute, two digit second, zulu time
     *      0-9 - MMDDhhmm - two digit month, two digit day, two digit hour, two digit minute, zulu time
     *
     * Note:  All formats are 7 characters except for last which is 8.  For further information
     * please refer to APRS specificateion page 22 (http://www.aprs.org/doc/APRS101.PDF)
     *
     * @param timeString Time in APRS format
     * @return Local time
     */
    protected LocalDateTime convertTime(String timeString)
    {
        LocalDateTime localDateTime = LocalDateTime.now();

        if (timeString.charAt(6) == 'z')
        {
            // The APRS time does not include year or month so get those from the
            // current zulu time
            ZonedDateTime zoneDateTimeNow = ZonedDateTime.now(ZoneId.of("Z"));
            int year = zoneDateTimeNow.getYear();
            int month = zoneDateTimeNow.getMonthValue();
            // Use the APRS day, hour, and minute
            int dayOfMonth = Integer.valueOf(timeString.substring(0, 2));
            int hour = Integer.valueOf(timeString.substring(2, 4));
            int minute = Integer.valueOf(timeString.substring(4, 6));
            // Create the APRS time object
            ZonedDateTime zoneDateTimeAprs = ZonedDateTime.of(year, month, dayOfMonth, hour, minute, 0, 0, ZoneId.of("Z"));

            // Convert the APRS zulu time to the local time.  Note that the ZoneDateTime.toLocalDateTime()
            // function does not consider timezone, hence the following messy statement:
            localDateTime = zoneDateTimeAprs.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        }

        return localDateTime;
    }

    /**
     * The APRS message contains an index into a symbol table (actually there are two
     * tables) for what type icon to display on a map.  This function handles the most
     * used cases in position reports
     *
     * @param tableId specify which table to use: '/' for primary, '-' for secondary
     * @param symbolCode index into the appropriate symbol table
     * @return textual description of the symbol
     */
    protected String getSymbol(char tableId, char symbolCode)
    {
        String key = String.format("%c%c", tableId, symbolCode);

        String ret;
        if (symbolMap.containsKey(key))
        {
            ret = symbolMap.get(key);
        }
        else
        {
            ret = String.format("unknown(%c%c)", tableId, symbolCode);
        }

        return ret;
    }


    /**
     * Initialize the hash map used to decode the symbols.
     * The map is static and so is this initializer so that the map is
     * initialized once and shared between class instances
     *
     * Note: Refer to chart on page 104 of APRS spec http://www.aprs.org/doc/APRS101.PDF
     *
     * @return string containing the name of the symbol
     */
    protected static HashMap<String, String> symbolMapInit()
    {
        HashMap<String, String> map = new HashMap<>();

        map.put("/!", "police");
        map.put("/#", "digi");
        map.put("/$", "phone");
        map.put("/-", "house");
        map.put("/<", "motorcycle");
        map.put("/>", "car");
        map.put("/Y", "yacht");
        map.put("/b", "bicycle");
        map.put("/j", "jeep");
        map.put("/o", "emergency ops center");
        map.put("/r", "antenna");
        map.put("/y", "yagi");
        map.put("/_", "weather station");

        map.put("-#", "digi");
        map.put("--", "house-hf");
        map.put("->", "car");

        return map;
    }
}
