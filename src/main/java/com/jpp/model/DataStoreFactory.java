package com.jpp.model;

/**
 * Factory pattern that creates a data store singleton
 */
public class DataStoreFactory
{
    private static IDataStore ds = null;

    /**
     * Obtains an single instance of a datastore object
     *
     * @return data store object
     */
    public static IDataStore getDataStore()
    {
        if (ds == null)
        {
            ds = new MongoDataStore();
        }

        return ds;
    }
}
