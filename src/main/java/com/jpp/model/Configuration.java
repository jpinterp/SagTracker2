package com.jpp.model;

public class Configuration
{
    private MongoId _id;
    private Position mapcenter;
    private AprsInfo aprs;

    public Configuration(Position mapcenter, AprsInfo aprs)
    {
        this.mapcenter = mapcenter;
        this.aprs = aprs;
    }

    public MongoId get_id()
    {
        return _id;
    }
    public void set_id(MongoId _id)
    {
        this._id = _id;
    }

    public Position getMapcenter() { return mapcenter; }
    public void setMapcenter(Position mapcenter) { this.mapcenter = mapcenter; }

    public AprsInfo getAprs() { return aprs; }
    public void setAprs(AprsInfo aprs) { this.aprs = aprs; };
}
