package com.jpp.controllers;

import com.jpp.DataStoreFactory;
import com.jpp.IDataStore;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventsController
{
    private IDataStore ds;

    public EventsController()
    {
        ds = DataStoreFactory.getDataStore();
    }

    @RequestMapping(method=RequestMethod.GET, produces={"application/JSON"})
    @ResponseBody
    String getEvents()
    {
        String jsonData = ds.GetEventNames();
        return jsonData;
    }

}
