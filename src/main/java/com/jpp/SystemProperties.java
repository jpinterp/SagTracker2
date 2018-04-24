package com.jpp;


import java.io.*;
import java.util.Properties;

public class SystemProperties
{
    private static final String PropertiesFileName = "SagTracker2.properties";

    private static final String DatabaseHostProp = "database.host";
    private static final String DatabasePortProp = "database.port";
    private static final String DatabaseUserProp = "database.user";
    private static final String DatabasePswdProp = "database.pswd";
    private static final String AprsHostProp = "aprs.host";
    private static final String AprsPortProp = "aprs.port";
    private static final String AprsUserProp = "aprs.user";
    private static final String AprsRadiusProp = "aprs.radius";
    private static final String MapCenterLatitudeProp = "mapcenter.latitude";
    private static final String MapCenterLongitudeProp = "mapcenter.longitude";


    public static String getDatabaseHost() { return getProperty(DatabaseHostProp); }
    // public static void setDatabaseHost(String host) { setProperty(DatabaseHostProp, host); }

    public static int getDatabasePort() { return Integer.valueOf(getProperty(DatabasePortProp)); }
    // public static void setDatabasePort(int port) {setProperty(DatabasePortProp, Integer.toString(port));}

    public static String getAprsHost () { return getProperty(AprsHostProp); }

    public static int getAprsPort() { return Integer.valueOf(getProperty(AprsPortProp));}

    public static String getAprsUser() { return getProperty(AprsUserProp); }

    public static int getAprsRadius() { return Integer.valueOf(getProperty(AprsRadiusProp)); }

    public static String getMapCenterLatitude() { return getProperty(MapCenterLatitudeProp);}
    public static String getMapCenterLongitude() { return getProperty(MapCenterLongitudeProp);}


    private static String getProperty(String name)
    {
        String value = null;

        Properties prop = new Properties();
        InputStream input = null;
        try
        {
            input = new FileInputStream(PropertiesFileName);
            prop.load(input);

            value = prop.getProperty(name);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    private static void setProperty(String name, String value)
    {
        Properties prop = new Properties();
        OutputStream output = null;

        try
        {
            output = new FileOutputStream(PropertiesFileName);

            // set the properties value
            prop.setProperty(name, value);

            // save properties to project root folder
            prop.store(output, null);
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
        finally
        {
            if (output != null)
            {
                try
                {
                    output.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
