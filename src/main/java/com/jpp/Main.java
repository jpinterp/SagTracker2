package com.jpp;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main
{
    public static void main(String[] args)
    {
        SpringApplication.run(Main.class, args);

        IDataStore ds = new MongoDataStore();
        ds.Connect();
        ds.SetEventName("Tacos");

        Thread t = new Thread(new AprsPump(ds));
        t.start();

    }

}