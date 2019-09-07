package com.akolar.maxaltitude;

public class Constants {
    public static final String VHost = "/";
    public static final String Host = "rabbit";
    public static final int Port = 5672;
    public static final String Username = System.getenv("rabbit_username");
    public static final String Password = System.getenv("rabbit_password");

    public static final String Queue = "beacons-alt";
    public static final String Exchange = "beacons";
}
