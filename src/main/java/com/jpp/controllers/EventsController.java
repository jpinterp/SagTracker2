package com.jpp.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventsController
{
    @RequestMapping(method=RequestMethod.GET, produces={"application/JSON"})
    @ResponseBody
    String getEvents()
    {
        return "{ \"greeting\": \"Hello, World!\"}";
    }

}
