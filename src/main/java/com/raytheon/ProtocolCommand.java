package com.raytheon;

public class ProtocolCommand {
    private final String name;
    private final String displayName;

    public ProtocolCommand(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String toString() {
        return "name: " + name + " displayName: " + displayName;
    }
}
