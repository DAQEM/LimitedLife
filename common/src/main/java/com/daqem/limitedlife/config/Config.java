package com.daqem.limitedlife.config;


public class Config {

    public boolean enableTitles = true;
    public int secondsToLive = 86400;

    public int gainedSecondsPerKill = 1800;
    public int gainedSecondsPerKillAsBoogeyman = 3600;

    public int removedSecondsPerDeath = 3600;
    public int removedSecondsPerDeathByBoogeyman = 7200;

    public int secondsPerBoogeymanElection = 1800;

    public boolean enableBoogeyman = true;
    public boolean boogeymanFailLosesColor = true;

    public int removedSecondsPerBoogeymanFail = 7200;

    public int secondsFromYellowColor = 57600;
    public int secondsFromRedColor = 28800;
}
