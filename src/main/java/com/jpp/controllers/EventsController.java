package com.jpp.controllers;

import com.jpp.aprs.AprsFactory;
import com.jpp.aprs.IAprs;
import com.jpp.model.DataStoreFactory;
import com.jpp.model.Event;
import com.jpp.model.IDataStore;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/event")
public class EventsController
{
    private IDataStore ds;

    public EventsController()
    {
        ds = DataStoreFactory.getDataStore();
    }

    @RequestMapping(method=RequestMethod.GET, produces={"application/JSON"})
    @ResponseBody
    public ResponseEntity<?> getEvent()
    {
        // Get the JSON data from the data store and pass it directly back to the
        // client.  Don't bother with creating intermediate POJOs
        String jsonData = ds.GetEventNames();
        return new ResponseEntity<String>(jsonData, HttpStatus.OK);
    }

    @RequestMapping(method=RequestMethod.POST, produces={"application/JSON"})
    @ResponseBody
    public ResponseEntity<?> createEvent(@RequestBody Event event, UriComponentsBuilder ucBuilder)
    {
        ds.SetEventName(event.getName());

        // Once the event name (database) is selected, the APRS messages can be saved.
        IAprs aprs = AprsFactory.getAprs();
        aprs.start();

        // Put URL of created event in header.  Note events use name not an id as is typical
        HttpHeaders headers = new HttpHeaders();
        // headers.setLocation(ucBuilder.path("/event/{id}").buildAndExpand(event.getName()).toUri());
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
