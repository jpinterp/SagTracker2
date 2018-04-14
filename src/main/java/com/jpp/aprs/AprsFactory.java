package com.jpp.aprs;

public class AprsFactory
{
    private static IAprs aprs = null;

    /**
     * Obtains an single instance of a aprs object
     *
     * @return data store object
     */
    public static IAprs getAprs()
    {
        if (aprs == null)
        {
            aprs = new AprsMain();
        }

        return aprs;
    }
}
