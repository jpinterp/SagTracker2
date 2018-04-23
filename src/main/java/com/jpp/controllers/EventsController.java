package com.jpp.controllers;

import com.jpp.aprs.AprsFactory;
import com.jpp.aprs.IAprs;
import com.jpp.model.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Iterator;
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
        String jsonData = ds.GetEventNamesJson();
        return new ResponseEntity<String>(jsonData, HttpStatus.OK);
    }

    @RequestMapping(method=RequestMethod.POST, produces={"application/JSON"})
    @ResponseBody
    public ResponseEntity<?> createEvent(@RequestBody Event event, UriComponentsBuilder ucBuilder)
    {
        // Determine if this event is new or an existing event
        EventList events = ds.GetEventNames();
        List<Event> le = events.getEvents();
        Iterator<Event> it = le.iterator();
        boolean existingEvent = false;
        while (it.hasNext())
        {
            Event e = it.next();
            if (e.getName().compareTo(event.getName()) == 0)
            {
                existingEvent = true;
                break;
            }
        }

        // Set the name of the event
        ds.SetEventName(event.getName());

        // If new event, then create default system configuration
        if (!existingEvent)
        {
            Configuration cfg = createDefaultConfiguration();
            ds.SetConfiguration(cfg);
        }

        // Once the event name (database) is selected, the APRS messages can be saved.
        IAprs aprs = AprsFactory.getAprs();
        aprs.start();

        // Put URL of created event in header.  Note events use name not an id as is typical
        HttpHeaders headers = new HttpHeaders();
        // headers.setLocation(ucBuilder.path("/event/{id}").buildAndExpand(event.getName()).toUri());
        return new ResponseEntity(HttpStatus.CREATED);
    }

    private Configuration createDefaultConfiguration()
    {
        AprsInfo aprs = new AprsInfo(DefaultValues.host, DefaultValues.port, DefaultValues.radius);
        Position mapcenter = new Position(DefaultValues.latitude, DefaultValues.longitude);
        Configuration configuration = new Configuration(mapcenter, aprs);
        return configuration;
    }
}
