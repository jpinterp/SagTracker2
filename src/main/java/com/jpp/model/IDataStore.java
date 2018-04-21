package com.jpp.model;

import java.time.LocalDateTime;

public interface IDataStore
{
    boolean Connect();
    boolean Connect(String host, int port);
    boolean Disconnect();

    // Get the list of existing events.  For MongoDb this is a list of database names
    EventList GetEventNames();
    String GetEventNamesJson();

    // Set the name for an event.  For MongoDb this is the name of the database
    boolean SetEventName(String eventName);


    // Add a stations location
    boolean AddLocation(String callsign, String lattitude, String longitude, String symbol, LocalDateTime timestamp, String raw);

    // retrieve latest locations for all stations
    StationList GetLocations();
    String GetLocationsJson();
    String GetLocationsJson(boolean registered);    // select registered or unregistered stations


    // Set the registration status for a single station
    boolean SetStationRegistration(String callsign, boolean registration);

    // Retrieve the registration status for all stations, in JSON format
    String GetStationRegistrations();


    // Get the system configuration
    Configuration GetConfiguration();
    String GetConfigurationJson();

    // Set the system configuration in JSON format
    boolean SetConfiguration(Configuration configuration);
}
