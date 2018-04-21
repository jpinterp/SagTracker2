package com.jpp.model;

import java.sql.Timestamp;

public class Location
{
    private Timestamp timestamp;
    private String lattitude;
    private String longitude;
    private String symbol;
    private String raw;


    public Timestamp getTimestamp()
    {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getLattitude()
    {
        return lattitude;
    }
    public void setLattitude(String lattitude)
    {
        this.lattitude = lattitude;
    }

    public String getLongitude()
    {
        return longitude;
    }
    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public String getSymbol()
    {
        return symbol;
    }
    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public String getRaw()
    {
        return raw;
    }
    public void setRaw(String raw)
    {
        this.raw = raw;
    }
}
