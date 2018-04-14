package com.jpp.controllers;

import com.jpp.model.DataStoreFactory;
import com.jpp.model.Event;
import com.jpp.model.IDataStore;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
    String getEvent()
    {
        String jsonData = ds.GetEventNames();
        return jsonData;
    }

    @RequestMapping(method=RequestMethod.POST, produces={"application/JSON"})
    @ResponseBody
    public ResponseEntity<?> createEvent(@RequestBody Event event, UriComponentsBuilder ucBuilder)
    {
        ds.SetConfiguration(event.getName());

        // Put URL of created event in header.  Note events use name not an id as is typical
        HttpHeaders headers = new HttpHeaders();
        // headers.setLocation(ucBuilder.path("/event/{id}").buildAndExpand(event.getName()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }
}
