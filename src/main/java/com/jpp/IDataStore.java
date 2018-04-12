package com.jpp;

import java.time.LocalDateTime;

public interface IDataStore
{
    boolean Connect();
    boolean Connect(String host, int port);
    boolean Disconnect();

    // Get the list of existing events.  For MongoDb this is a list of databaase
    String GetEventNames();

    // Set the name for an event.  For MongoDb this is the name of the database
    boolean SetEventName(String eventName);


    // Add a stations location
    boolean AddLocation(String callsign, String lattitude, String longitude, String symbol, LocalDateTime timestamp, String raw);

    // retrieve latest locations for all stations, data in JSON format
    String GetLocations();

    // retrieve stations for either registered or unregistered stations
    String GetLocations(boolean registered);


    // Set the registration status for a single station
    boolean SetStationRegistration(String callsign, boolean registration);

    // Retrieve the registration status for all stations, in JSON format
    String GetStationRegistrations();


    // Get the system configuration in JSON format
    String GetConfiguration();

    // Set the system configuration in JSON format
    boolean SetConfiguration(String configJson);
}
