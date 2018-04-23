package com.jpp.model;

public class AprsInfo
{
    private String host;
    private int port;
    private int radius;

    public AprsInfo(String host, int port, int radius)
    {
        this.host = host;
        this.port = port;
        this.radius = radius;
    }

    public String getHost() { return host; }
    public void setHost() { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public int getRadius() { return radius; }
    public void setRadius(int radius) { this.radius = radius; }

}
