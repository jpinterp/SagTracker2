package com.jpp.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonHelpers
{
    public static String GetAprsHost(IDataStore ds)
    {
        String jsonCfg = ds.GetConfiguration();

        JsonObject jsonObject = new JsonParser().parse(jsonCfg).getAsJsonObject();
        String server = jsonObject.get("aprs-host").getAsString();

        return server;
    }

    public static int GetAprsPort(IDataStore ds)
    {
        String jsonCfg = ds.GetConfiguration();

        JsonObject jsonObject = new JsonParser().parse(jsonCfg).getAsJsonObject();

        int port = jsonObject.get("aprs-port").getAsInt();
        return port;
    }

    public static int GetAprsRadius(IDataStore ds)
    {
        String jsonCfg = ds.GetConfiguration();

        JsonObject jsonObject = new JsonParser().parse(jsonCfg).getAsJsonObject();

        int port = jsonObject.get("aprs-radius").getAsInt();
        return port;
    }

    /**
     * Retrieves the lattitude and longitude of the map in the format for use by the
     * APRS login string (i.e  "42.2000/-71.4200"
     *
     * @param ds DataStroe interface from which to retrieve the coordinates.  The data store is expected
     *           to have the following format:
     *              map-center:  {lat: xx.xxxx, lng: yy.yyyy}
     * @return coordinates of the map center formatted for the APRS login string
     */
    public static String GetMapCenter(IDataStore ds)
    {
        String jsonCfg = ds.GetConfiguration();

        JsonObject jsonObject = new JsonParser().parse(jsonCfg).getAsJsonObject();
        JsonObject mapObject = jsonObject.getAsJsonObject("map-center");
        String lat = mapObject.get("lat").getAsString();
        String lng = mapObject.get("lng").getAsString();

        return String.format("%s/%s", lat, lng);
    }
}