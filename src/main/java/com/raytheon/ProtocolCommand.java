package com.raytheon;

import javafx.beans.property.SimpleStringProperty;

public class ProtocolCommand {
    private final SimpleStringProperty name;
    private final SimpleStringProperty displayName;

    public ProtocolCommand(String name, String displayName) {
        this.name = new SimpleStringProperty(name);
        this.displayName = new SimpleStringProperty(displayName);
    }

    public SimpleStringProperty getName() {
        return name;
    }

    public SimpleStringProperty getDisplayName() {
        return displayName;
    }

    public String toString() {
        return "name: " + name + " displayName: " + displayName;
    }
}
