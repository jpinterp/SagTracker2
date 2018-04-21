package com.jpp.model;

import java.util.List;

public class Station
{
    private MongoId _id;
    private String callsign;
    private boolean registered;
    private List<Location> locations;

    public MongoId get_id()
    {
        return _id;
    }
    public void set_id(MongoId _id)
    {
        this._id = _id;
    }

    public String getCallsign()
    {
        return callsign;
    }

    public void setCallsign(String callsign)
    {
        this.callsign = callsign;
    }

    public boolean getRegistered()
    {
        return registered;
    }

    public void setRegistered(boolean registered)
    {
        this.registered = registered;
    }

    public List<Location> getLocations()
    {
        return locations;
    }
    public void setLocations(List<Location> locations)
    {
        this.locations = locations;
    }
}
