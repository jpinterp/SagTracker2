package com.jpp.aprs;

import com.jpp.SystemProperties;
import com.jpp.model.IDataStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


// Log
public class AprsPump implements Runnable
{
    private IDataStore dataStore;

    public AprsPump(IDataStore dataStore)
    {
        this.dataStore = dataStore;
    }

    @Override
    public void run()
    {
        // Retrieve APRS server and port from database
        // Configuration cfg = dataStore.GetConfiguration();
        String aprsLogin = CreateAprsLogin();

        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try
        {
            socket = new Socket(SystemProperties.getAprsHost(), SystemProperties.getAprsPort());
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(aprsLogin);
            String rawAprs = in.readLine();
            System.out.println(rawAprs);

            while (true)
            {
                rawAprs = in.readLine();
                char pktFlag = rawAprs.charAt(0);

                if (pktFlag == '#')
                {
                    System.out.println(rawAprs);
                }
                else
                {
                    // System.out.println(rawAprs);
                    TNC tnc = new TNC(rawAprs);
                    if (tnc.isValidPacket == true)
                    {
                        // Refer to chapter 5, page 17 of the aprs spec http://www.aprs.org/doc/APRS101.PDF
                        if (tnc.payload.length() > 1)
                        {
                            char dataTypeId = tnc.payload.charAt(0);
                            System.out.printf("Source: %s  data type: %c\r\n", tnc.source, dataTypeId);

                            IAprsPosition aprsPos = null;
                            /* Position w/timestamp with or without aprs msg */
                            if ((dataTypeId == '@') || (dataTypeId == '/'))
                            {
                                aprsPos = new AprsPosTimestamp(tnc.payload);
                            }
                            /* Position w/o timestamp with or without aprs msg */
                            else if ((dataTypeId == '=') || (dataTypeId == '!'))
                            {
                                aprsPos = new AprsPosNoTimestamp(tnc.payload);
                            }
                            /* Current & Old Mic-E and TM-D700, beta too */
                            else if ((dataTypeId == '\'') || (dataTypeId == '`') ||
                                     (dataTypeId == 0x1d) || (dataTypeId == 0x1c))
                            {
                                aprsPos = new AprsPosMicECur(tnc.destination, tnc.payload);
                            }
                            /* Telemetry data */
                            else if (dataTypeId == 'T')
                            {
                            }
                            /* Object */
                            else if (dataTypeId == ';')
                            {
                            }

                            if ((aprsPos != null) && (aprsPos.getIsPositionPacket() == true))
                            {
                                dataStore.AddLocation(tnc.source, aprsPos.getLatitude(), aprsPos.getLongitude(), aprsPos.getSymbol(), aprsPos.getTime(), rawAprs);
                                System.out.printf("%s: \t%s %s %s [%s]\r\n", tnc.source, aprsPos.getLatitude(), aprsPos.getLongitude(), aprsPos.getTime().toString(), aprsPos.getSymbol());
                            }
                        }
                    }
                }
            }
        }
        catch (UnknownHostException e)
        {

        }
        catch (IOException e)
        {

        }
    }

    /**
     * Creates an APRS login string with a filter using the map center and radius from the config file.  Trial
     * and error have proven that the string must end with CR-LF so that is appended by this function.
     *
     * @return APRS login string
     */
    private String CreateAprsLogin()
    {
        // Sample login string:  "user W5UVO pass -1 vers SagTrack v0.1 filter r/42.2/-71.5/25\r\n"

        // Password of -1 means read-only login
        String login = String.format("user %s pass -1 vers SagTracker v0.1 filter r/%s/%s/%d\r\n",
                SystemProperties.getAprsUser(),
                SystemProperties.getMapCenterLatitude(),    // TODO: Get from datastore
                SystemProperties.getMapCenterLongitude(),   // TODO: Get from datastore
                SystemProperties.getAprsRadius());

        return login;
    }


}
