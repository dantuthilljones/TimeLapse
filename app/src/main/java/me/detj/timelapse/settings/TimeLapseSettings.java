package me.detj.timelapse.settings;

public class TimeLapseSettings {

    private final String hostname;
    private final String username;
    private final String password;
    private final String directory;
    private final int intervalMinutes;

    public TimeLapseSettings(String hostname,
                             String username,
                             String password,
                             String directory,
                             int intervalMinutes) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.directory = directory;
        this.intervalMinutes = intervalMinutes;
    }

    public static TimeLapseSettings newDefault() {
        return new TimeLapseSettings(
                "",
                "",
                "",
                "",
                0);
    }

    public String getHostname() {
        return hostname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDirectory() {
        return directory;
    }

    public int getIntervalMinutes() {
        return intervalMinutes;
    }
}

