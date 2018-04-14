package com.jpp;


import com.jpp.model.DataStoreFactory;
import com.jpp.model.IDataStore;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main
{
    public static void main(String[] args)
    {
        SpringApplication.run(Main.class, args);

        IDataStore ds = DataStoreFactory.getDataStore();
        ds.Connect();

        // Don't start saving APRS messages until the event name (database) is selected.
        // This is now done in the EventsController.createEvent() method.
    }

}