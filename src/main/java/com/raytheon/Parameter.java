package com.raytheon;

import javafx.beans.property.SimpleStringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Parameter {
    private SimpleStringProperty name;
    private SimpleStringProperty displayName;
    private SimpleStringProperty description;
    private SimpleStringProperty visibility;
    private SimpleStringProperty valueDescription;
    private SimpleStringProperty value;
    private SimpleStringProperty valueType;
    private SimpleStringProperty units;
    private static Logger log = LogManager.getLogger();

    public Parameter(String name, String displayName, String desc, String vis, String val_desc, String valType, String units) {
        this.name = new SimpleStringProperty(name);
        this.displayName = new SimpleStringProperty(displayName);
        this.description = new SimpleStringProperty(desc);
        this.visibility = new SimpleStringProperty(vis);
        this.valueDescription = new SimpleStringProperty(val_desc);
        this.units = new SimpleStringProperty(units);
        this.valueType = new SimpleStringProperty(valType);
        this.value = new SimpleStringProperty();
    }

    public String getDisplayName() {
        return displayName.get();
    }

    public String getDescription() {
        return description.get();
    }

    public String getVisibility() {
        return visibility.get();
    }

    public String getValueDescription() {
        return valueDescription.get();
    }

    public String getValue() {
        return value.get();
//        if (value == null) return "";
//        if (value.contains("null"));
//        if (valueType.equals("int")) return Integer.parseInt(value);
//        if (valueType.equals("float")) return Double.parseDouble(value);
//        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public String getValueType() {
        return valueType.get();
    }

    public String getUnits() {
        return units.get();
    }

    public String toString() {
        return "name: " + name + " displayName: " + displayName;
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }
}
