package com.jpp;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class MongoDataStore implements IDataStore
{
    // For a MongoDb store, each event's data is stored in their own database
    // There are 3 collections in each database:
    //      StationLocations - stations and their locations
    //      StationRegistration - transaction history of when a station was registered or unregistered
    //      SystemConfiguration - contains single document for the system configuration data

    private MongoClient client = null;
    private MongoDatabase db = null;

    private Document latestLocationAggregate = null;

    private static final String DatabaseName = "Tacos";
    private static final String LocationCollectionName = "StationLocations";
    private static final String RegistrationCollectionName = "StationRegistrations";
    private static final String ConfigurationCollectionName = "SystemConfiguration";

    private static final String CallsignField = "callsign";
    private static final String RegisteredField = "registered";
    private static final String LocationsField = "locations";
    private static final String TimestampField = "timestamp";
    private static final String LatitudeField = "lattitude";
    private static final String LongitudeField = "longitude";
    private static final String SymbolField = "symbol";
    private static final String EventField = "event";       // not stored in database but returned in JSON string
    private static final String RawField = "raw";


    /**
     * Connect to the local machine running MongoDB on the default port (27017)
     *
     * @return true if successful, false otherwie
     */
    @Override
    public boolean Connect()
    {
        return Connect("localhost", 27017);
    }

    /**
     * Connect to the machine running MongoDB
     *
     * @param host Name or IP address of machine running MongoDB
     * @param port Port number of the MongoDB service, typically 27017
     * @return true if successful, false otherwise
     */
    @Override
    public boolean Connect(String host, int port)
    {
        client = new MongoClient(host, port);

        latestLocationAggregate = CreateLocationAggregate();
        return true;
    }

    @Override
    public boolean Disconnect()
    {
        client.close();
        return true;
    }

    /**
     * Creates an aggregate for retrieving the latest location of a station.  The aggregate
     * is fairly complex and does not change so it is created one and used repeatedly.
     *
     * @return document containing the aggregate
     */
    private Document CreateLocationAggregate()
    {
        /* The follow is an aggregate that searches each documents locations subdocuments
           for the newest timestamps.  The aggregate works by creating an internal variable
           (maxtime) that is the maximum timestamp for the array of subdocuments.  The array
           is then filtered returning only the document with the newest timestamp.  The
           subdocument is added using the same name as the subdocument array in effect masking
           the original subdocuments.

           { $addFields:
                {locations:                     // mask/override element in doc
                    {$let:
                        {vars: {maxtime: {$max: "$locations.timestamp"}},
                         in:
                            {$filter:
                                {input:"$locations",
                                 cond: {$eq: ["$$this.timestamp", "$$maxtime"]}
                                }
                            }
                        }
                    }
                }
            }
        */
        Document filterDoc = new Document("$filter",
                new Document("input", "$locations")
                        .append("cond", new Document("$eq", Arrays.asList("$$this.timestamp", "$$maxtime"))));

        Document letDoc = new Document("$let",
                new Document("vars",
                        new Document("maxtime",
                                new Document("$max", "$locations.timestamp")))
                        .append("in", filterDoc));

        Document addFieldsDoc = new Document("$addFields",
                new Document("locations", letDoc));

        return addFieldsDoc;
    }

    /**
     * Retrieve the names of existing events.  For MongoDb, the event name is actually
     * the name of the database.  This function retrieves the list of all databases on
     * the system without knowing whether they are used for APRS or not.  Use caution!
     *
     * @return List of event (database) names in JSON format
     */
    @Override
    public String GetEventNames()
    {
        StringBuilder sb = new StringBuilder();
        MongoCursor<String> it = client.listDatabaseNames().iterator();

        while (it.hasNext())
        {
            String s = it.next();
            sb.append(new Document(EventField, s).toJson());
            sb.append(",");
        }
        return sb.toString();
    }

    /**
     * Specify the event name and setup any database constructs.  For MongoDb, the
     * event name is actually the name of a database.  If the database does not exist,
     * it will be created.  Three collections are created:
     *   StationLocations, StationRegistration, SystemConfiguration
     *
     * @param eventName Name of the volunteer event, also the MongoDb database name
     * @return true if successful, false otherwise
     */
    @Override
    public boolean SetEventName(String eventName)
    {
        // MongoDb is quite flexible becuase if you get a database that does not exist, it will
        // be created when a collection is created
        db = client.getDatabase(eventName);

        // Collections are auto magically created at first use

        // TODO:  Configure default system configuration document

        return true;
    }

    /**
     * Add an APRS reported position to the database in the StationLocations collection.  Coordinates
     * are in the format Google Maps expects:  +/-xx.yyyy
     *
     * @param callsign Station reporting the location
     * @param latitude Coordinate lattitude +/- xx.yyyyy format
     * @param longitude Coordinate longitude +/- xx.yyyyy format
     * @param dateTime Local time when the location was reported (APRS reports in zulu)
     * @param raw Raw APRS data string
     * @return true if successful, false otherwise
     */
    @Override
    public boolean AddLocation(String callsign, String latitude, String longitude, String symbol, LocalDateTime dateTime, String raw)
    {
        boolean bRet = false;

        MongoCollection coll = db.getCollection(LocationCollectionName);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        // Determine if record exists
        Document queryDoc = new Document(CallsignField, callsign);
        long docCount = coll.count(queryDoc);
        if (docCount == 0)
        {
            // Insert new record
            Document insertDoc = new Document(CallsignField, callsign)
                    .append(RegisteredField, true)
                    .append(LocationsField,
                            Arrays.asList(
                                new Document(TimestampField, dateTime.format(dateFormatter))
                                        .append(LatitudeField, latitude)
                                        .append(LongitudeField, longitude)
                                        .append(SymbolField, symbol)
                                        .append(RawField, raw)));
            coll.insertOne(insertDoc);
            bRet = true;
        }
        else if (docCount == 1)
        {
            // Update existing record, add new location sub-document
            Document updateDoc = new Document("$push",
                    new Document(LocationsField,
                        new Document(TimestampField, dateTime.format(dateFormatter))
                                .append(LatitudeField, latitude)
                                .append(LongitudeField, longitude)
                                .append(SymbolField, symbol)
                                .append(RawField, raw)));
            coll.updateOne(queryDoc, updateDoc);
            bRet = true;
        }
        else
        {
            // problem - too many documents found
            bRet = false;
        }

        return true;
    }

    /**
     * Retrieves the last location of all stations, both registered and unregistered
     *
     * @return List of stations in JSON format
     */
    @Override
    public String GetLocations()
    {
        return GetLocationsWorker(Arrays.asList(latestLocationAggregate));
    }

    /**
     * Retrieves the last location of stations that are either registered or not registered
     *
     * @param registered true for registered stations, false for unregistered stations
     * @return List of stations in JSON format
     */
    @Override
    public String GetLocations(boolean registered)
    {
        // The worker function does not like the Aggregate helpers, so Documents
        // are created instead
        Document matchDoc = new Document ("$match", new Document(RegisteredField, registered));

        return GetLocationsWorker(Arrays.asList(matchDoc, latestLocationAggregate));
    }

    /**
     * Worker function for the get location queries.
     *
     * @param aggregateList List of aggregate documents such as {$match: {"field", "value"}}
     * @return query results in a JSON formatted string
     */
    private String GetLocationsWorker(List<Document> aggregateList)
    {
        MongoCollection collection = db.getCollection(LocationCollectionName);

        // Perform the search
        AggregateIterable<Document> results = collection.aggregate(aggregateList);

        // Convert results into a JSON formatted string
        StringBuilder sb = new StringBuilder();
        results.forEach((Block<Document>) d ->
            { sb.append(d.toJson());
              sb.append(",");            // is the trailing comma a problem?
            }
        );

        return sb.toString();
    }

    /**
     * Marks or unmarks a station as registered.
     *
     * @param callsign Station identification
     * @param registration registration state
     * @return true if successful, false otherwise
     */
    @Override
    public boolean SetStationRegistration(String callsign, boolean registration)
    {
        // There are two parts to a station being registered:
        //  The location document's registration field is set
        //  There is a 'transaction' history of when a station's registration was changed

        // Update the stations location document
        Document queryDoc = new Document(CallsignField, callsign);
        Document updateDoc = new Document(RegisteredField, registration);
        MongoCollection coll = db.getCollection(LocationCollectionName);
        UpdateResult results = coll.updateOne(queryDoc, updateDoc);

        // Add a document in the transaction log collection
        Document addDoc = new Document(CallsignField, callsign)
                .append(RegisteredField, registration)
                .append(TimestampField, Timestamp.valueOf(LocalDateTime.now()));
        MongoCollection collTransaction = db.getCollection(RegistrationCollectionName);
        collTransaction.insertOne(addDoc);

        return results.wasAcknowledged();
    }

    @Override
    public String GetStationRegistrations()
    {
        return null;
    }

    /**
     * aprs: [server:string, port:integer]
     * @return
     */
    @Override
    public String GetConfiguration()
    {
        MongoCollection coll = db.getCollection(ConfigurationCollectionName);
        FindIterable<Document> results = coll.find().limit(1);  // should be only 1 doc in collection

        StringBuilder sb = new StringBuilder();
        results.forEach((Block<Document>) d -> sb.append(d.toJson()));

        return sb.toString();
    }

    @Override
    public boolean SetConfiguration(String configJson)
    {
        return false;
    }
}
