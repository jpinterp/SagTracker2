package com.jpp.controllers;

import com.jpp.aprs.AprsFactory;
import com.jpp.aprs.IAprs;
import com.jpp.model.Configuration;
import com.jpp.model.DataStoreFactory;
import com.jpp.model.IDataStore;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/configuration")
public class ConfigurationController
{
    private IDataStore ds;

    public ConfigurationController()
    {
        ds = DataStoreFactory.getDataStore();
    }

    @RequestMapping(method=RequestMethod.GET, produces={"application/JSON"})
    @ResponseBody
    public ResponseEntity<?> getConfiguration()
    {
        // Get the JSON data from the data store and pass it directly back to the
        // client.  Don't bother with creating intermediate POJOs
        String jsonData = ds.GetConfigurationJson();
        return new ResponseEntity<String>(jsonData, HttpStatus.OK);
    }

    @RequestMapping(method=RequestMethod.POST, produces={"application/JSON"})
    @ResponseBody
    public ResponseEntity<?> createConfiguration(@RequestBody Configuration configuration, UriComponentsBuilder ucBuilder)
    {
        ds.SetConfiguration(configuration);

        // Once the event name (database) is selected, the APRS messages can be saved.
        // IAprs aprs = AprsFactory.getAprs();
        // aprs.start();
        // TODO: Restart APRS pump

        // Put URL of created event in header.  Note events use name not an id as is typical
        HttpHeaders headers = new HttpHeaders();
        // headers.setLocation(ucBuilder.path("/event/{id}").buildAndExpand(event.getName()).toUri());
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
