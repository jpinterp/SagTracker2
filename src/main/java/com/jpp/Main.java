package com.jpp;


import com.jpp.aprs.AprsPump;
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
        ds.SetEventName("Tacos");

        Thread t = new Thread(new AprsPump(ds));
        t.start();

    }

}