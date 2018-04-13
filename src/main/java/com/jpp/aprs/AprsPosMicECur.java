package com.jpp.aprs;

import java.time.LocalDateTime;
import java.util.HashMap;

/*
 * Mic-E Data - Destination Address Field Format
 * -------------------------------------------------------------------------------------------------
 * | Lat Digit 1 | Lat Digit 2 | Lat Digit 3 | Lat Digit 4 | Lat Digit 5 | Lat Digit 6 |   Aprs    |
 * |  + Message  |  + Message  |  + Message  |  + N/S Lat  | + Longitude | + W/E Long  | Digi Path |
 * |    Bit A    |    Bit B    |    Bit C    |  Indicator  |    Offset   |  Indicator  |    Code   |
 * |-------------|-------------|-------------|-------------|-------------|-------------|-----------|
 * |      1      |      1      |      1      |      1      |       1     |      1      |     1     |
 * -------------------------------------------------------------------------------------------------
 *
 * Mic-E Data - Information Field Format
 * ----------------------------------------------------------------------------------------------
 * | Data |     Longitude      |   Speed and Course    |        | Symbol | Mic-e Telemetry Data |
 * | Type |--------------------|-----------------------| Symbol | Table  |----------------------|
 * |  ID  | d+28 | m+28 | h+28 | SP+28 | dc+28 | SE+28 |  Code  |  ID    |   Mic-E Status Text  |
 * |------|------|------|------|-------|-------{-------|--------|--------|----------------------|
 * |   1  |   1  |   1  |   1  |   1   |   1   |   1   |    1        1   |          n           |
 * ----------------------------------------------------------------------------------------------
 */
public class AprsPosMicECur extends AprsPositionBase implements IAprsPosition
{
    private static HashMap<String, MicEDecode> destEncoding = encodingInit();

    private int msg = 0;

    public AprsPosMicECur()
    {
    }

    public AprsPosMicECur(String destField, String infoField)
    {
        parse(destField, infoField);
    }

    public void parse(String destField, String infoField)
    {
        char typeId = infoField.charAt(0);

        if ((typeId == '`') || (typeId == '\'') ||
            (typeId == 0x1d) || (typeId == 0x1c))
        {
            try
            {


                // Aprs message does not include a timestamp so use the current time
                time = LocalDateTime.now();

                // Latitude is encoded in the TNC (AX.25) destination field
                latitude = decodeLatitude(destField);

                // A 3-bit message is encoded in the APRS destination (latitude) field
                msg = decodeMsg(destField);

                // Longitude in encoded in the TNC (AX.25) information/payload field and
                // the offset and east/west designation are encoded in the destination
                // field.
                longitude = decodeLongitude(destField, infoField);

                // The APRS symbol is encoded in the information field
                symbol = decodeSymbol(infoField);

                // Assume valid position if both latitude and longitude are decoded
                isPositionPacket = ((latitude != null) && (longitude != null));
            }
            catch (Exception e)
            {
                System.out.printf("Error parsing MicE packet: destField=\"%s\", infoField=\"%s\"\r\n", destField, infoField);
                System.out.printf("  Exception: %s\r\n", e.getMessage());
            }
        }
    }

    /**
     * Decodes the information in the TNC (AX.25) information/payload field to obtain
     * the name of a symbol
     *
     * @param infoField TNC (AX.25) information field
     * @return name of a symbol or "unknown(xy)" if not known.  Research has shown that
     * many APRS messages do not contain valid symbol information
     */
    private String decodeSymbol(String infoField)
    {
        String ret = null;

        if (infoField.length() >= 9)
        {
            char symCode = infoField.charAt(7);
            char symTableId = infoField.charAt(8);
            ret = getSymbol(symTableId, symCode);
        }
        return ret;
    }


    /**
     * Decodes the latitude from the packet destination field.
     *
     * Note:  This function decodes the longitude offset and sign (east/west)
     *
     * @param destField Encoded latitude in TNC (AX.25) destination field
     * @return decoded latitude string in +/-xx.yyyy format
     */
    private String decodeLatitude(String destField)
    {
        String ret = null;

        if (destField.length() >= 6)
        {
            StringBuilder sb = new StringBuilder();

            String t = destField.substring(0, 1);
            MicEDecode m = destEncoding.get(t);
            sb.append(m.latDigit);

            t = destField.substring(1, 2);
            m = destEncoding.get(t);
            sb.append(m.latDigit);

            // Put the latitude into xx.yyyy format
            sb.append('.');

            t = destField.substring(2, 3);
            m = destEncoding.get(t);
            sb.append(m.latDigit);

            // 4th char includes the north/south designation
            t = destField.substring(3,4);
            m = destEncoding.get(t);
            sb.append(m.latDigit);
            char latSign = m.isSouth ? '-' : '+';
            sb.insert(0, latSign);

            // 5th char includes the longitude offset
            t = destField.substring(4,5);
            m = destEncoding.get(t);
            sb.append(m.latDigit);

            // 6th char includes the east/west designation
            t = destField.substring(5);
            m = destEncoding.get(t);
            sb.append(m.latDigit);

            ret = sb.toString();
        }

        return ret;
    }

    /**
     * Decodes the longitude offset, either 0 or 100, from the encoded TNC (AX.25) destination field
     *
     * @param destField TNC (AX.25) destination field
     * @return longitude offset, either 0 or 100
     */
    private int decodeLongOffset(String destField)
    {
        String t = destField.substring(4,5);
        MicEDecode m = destEncoding.get(t);
        int offset = m.longOffset;
        return offset;
    }

    /**
     * Decodes the longitude east/west designation from the encoded TNC (AX.25) destination field
     * @param destField TNC (AX.25) destination field
     * @return logitude direction, either + or -
     */
    private char decodeLongSign(String destField)
    {
        // 6th char includes the east/west designation
        String t = destField.substring(5);
        MicEDecode m = destEncoding.get(t);
        char sign = m.isEast ? '+' : '-';
        return sign;
    }

    /**
     * Decode the 3-bit message encoded in the APRS destination field.
     *
     * @param destField Encoded latitude in TNC (AX.25) destination field
     * @return 3 bit message
     */
    private int decodeMsg(String destField)
    {
        int msg = 7;    // msg 111b = off duty while msg 0 = emergency, default to off duty

        if (destField.length() >= 3)
        {
            // high bit
            String t = destField.substring(0, 1);
            MicEDecode m = destEncoding.get(t);
            msg = m.msg << 2;

            // mid bit
            t = destField.substring(1, 2);
            m = destEncoding.get(t);
            msg = msg | (m.msg << 1);

            // low bit
            t = destField.substring(2, 3);
            m = destEncoding.get(t);
            msg = msg | m.msg;
        }
        return msg;
    }

    /**
     * Decodes the longitude from the APRS info field.
     *
     * @param destField TNC (AX.25) destination field includes the longitude offset (0,100) and the
     *                  east/west designation
     * @param infoField Encoded longitude in TNC (AX.25) info/payload field
     * @return decoded longitude string in +/-xxx.yyyy format
     */
    private String decodeLongitude(String destField, String infoField)
    {
        String ret = null;

        int offset = decodeLongOffset(destField);
        char sign = decodeLongSign(destField);

        if (infoField.length() >= 4)
        {
            StringBuilder sb = new StringBuilder();
            sb.append(sign);
            sb.append(decodeDegrees(infoField.charAt(1), offset));  // offset 0 is data type ID
            sb.append('.');
            sb.append(decodeMinutes(infoField.charAt(2)));
            sb.append(decodeHundredths(infoField.charAt(3)));
            ret = sb.toString();
        }
        return ret;
    }

    /**
     * Decodes the degrees which are in d+28 format.
     *
     * @param code Ascii code to be decoded
     * @param longOffset Longitude offset (0 or 100) from latitude decoding
     * @return Degrees string in xx format
     */
    private String decodeDegrees(char code, int longOffset)
    {
        int d = ((int)code)-28;

        d += longOffset;        // no-op if offset is 0

        if ((d>=180) && (d<=189))
        {
            d -= 80;
        }
        if ((d>=190) && (d<=199))
        {
            d -= 190;
        }

        String s = String.format("%03d", d);
        return s;
    }

    /**
     * Decodes minutes which are in m+28 format
     *
     * @param code Ascii code to be decoded
     * @return Minutes string in xx format
     */
    private String decodeMinutes(char code)
    {
        int m = ((int)code)-28;

        if (m>=60)
        {
            m -= 60;
        }

        String s = String.format("%02d", m);
        return s;
    }

    /**
     * Decodes the hundredths of minutes in h+28 format
     *
     * @param code Ascii code to be decoded
     * @return Hundredths of minutes in xx format
     */
    private String decodeHundredths(char code)
    {
        int h = ((int)code)-28;

        String s = String.format("%02d", h);
        return s;
    }

    /**
     * Initialize the hash map used to parse the Mic-E destination address.
     * The map is static and so is this initializer so that the map is
     * initialized once and shared between class instances
     *
     * Note: Refer to chart on page 44 of APRS spec http://www.aprs.org/doc/APRS101.PDF
     *
     * @return hash map with digit of destination field as the key and an instance
     *   of a MicEDecode structure as the value
     */
    private static HashMap<String, MicEDecode> encodingInit()
    {
        HashMap<String, MicEDecode> map = new HashMap<String, MicEDecode>();

        map.put("0", new MicEDecode('0', (byte) 0, true, 0, true));
        map.put("1", new MicEDecode('1', (byte) 0, true, 0, true));
        map.put("2", new MicEDecode('2', (byte) 0, true, 0, true));
        map.put("3", new MicEDecode('3', (byte) 0, true, 0, true));
        map.put("4", new MicEDecode('4', (byte) 0, true, 0, true));
        map.put("5", new MicEDecode('5', (byte) 0, true, 0, true));
        map.put("6", new MicEDecode('6', (byte) 0, true, 0, true));
        map.put("7", new MicEDecode('7', (byte) 0, true, 0, true));
        map.put("8", new MicEDecode('8', (byte) 0, true, 0, true));
        map.put("9", new MicEDecode('9', (byte) 0, true, 0, true));

        // A-K not used in address bytes 4-6
        map.put("A", new MicEDecode('0', (byte) 1, true, 0, true));
        map.put("B", new MicEDecode('1', (byte) 1, true, 0, true));
        map.put("C", new MicEDecode('2', (byte) 1, true, 0, true));
        map.put("D", new MicEDecode('3', (byte) 1, true, 0, true));
        map.put("E", new MicEDecode('4', (byte) 1, true, 0, true));
        map.put("F", new MicEDecode('5', (byte) 1, true, 0, true));
        map.put("G", new MicEDecode('6', (byte) 1, true, 0, true));
        map.put("H", new MicEDecode('7', (byte) 1, true, 0, true));
        map.put("I", new MicEDecode('8', (byte) 1, true, 0, true));
        map.put("J", new MicEDecode('9', (byte) 1, true, 0, true));

        map.put("K", new MicEDecode(' ', (byte) 1, true, 0, true));
        map.put("L", new MicEDecode(' ', (byte) 0, true, 0, true));

        map.put("P", new MicEDecode('0', (byte) 1, false, 100, false));
        map.put("Q", new MicEDecode('1', (byte) 1, false, 100, false));
        map.put("R", new MicEDecode('2', (byte) 1, false, 100, false));
        map.put("S", new MicEDecode('3', (byte) 1, false, 100, false));
        map.put("T", new MicEDecode('4', (byte) 1, false, 100, false));
        map.put("U", new MicEDecode('5', (byte) 1, false, 100, false));
        map.put("V", new MicEDecode('6', (byte) 1, false, 100, false));
        map.put("W", new MicEDecode('7', (byte) 1, false, 100, false));
        map.put("X", new MicEDecode('8', (byte) 1, false, 100, false));
        map.put("Y", new MicEDecode('9', (byte) 1, false, 100, false));
        map.put("z", new MicEDecode(' ', (byte) 1, false, 100, false));

        return map;
    }


}


