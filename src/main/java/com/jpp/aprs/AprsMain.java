package com.jpp.aprs;

import com.jpp.model.DataStoreFactory;
import com.jpp.model.IDataStore;

public class AprsMain implements IAprs
{
    private Thread t;

    public void start()
    {
        IDataStore ds = DataStoreFactory.getDataStore();

        t = new Thread(new AprsPump(ds));

        t.start();
    }

    public void stop()
    {
    }
}
