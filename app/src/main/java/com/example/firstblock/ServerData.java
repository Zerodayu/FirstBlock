package com.example.firstblock;

public class ServerData {
    private String name;
    private String edition;
    private String version;
    private String loader;

    public ServerData(String name, String edition, String version, String loader) {
        this.name = name;
        this.edition = edition;
        this.version = version;
        this.loader = loader;
    }

    public String getName() { return name; }
    public String getEdition() { return edition; }
    public String getVersion() { return version; }
    public String getLoader() { return loader; }
}
