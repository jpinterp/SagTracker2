package com.jpp.controllers;

import com.jpp.DataStoreFactory;
import com.jpp.IDataStore;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/locations")
public class LocationsController
{
    private IDataStore ds;

    public LocationsController()
    {
        ds = DataStoreFactory.getDataStore();
    }

    @RequestMapping(method=RequestMethod.GET, produces={"application/JSON"})
    @ResponseBody
    String getEvents()
    {
        String jsonData = ds.GetLocations();
        return jsonData;
    }

}
