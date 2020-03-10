package com.srgpanov.dotaskillstimer.data;

public class VersionRemoteDB {
    private long version;

    public VersionRemoteDB(long version) {
        this.version = version;
    }

    public VersionRemoteDB() {
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
