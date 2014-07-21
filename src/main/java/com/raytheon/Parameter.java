package com.raytheon;

import javafx.beans.property.SimpleStringProperty;

public class Parameter {
    private SimpleStringProperty name;
    private SimpleStringProperty displayName;
    private SimpleStringProperty description;
    private SimpleStringProperty visibility;
    private SimpleStringProperty valueDescription;
    private SimpleStringProperty value;
    private SimpleStringProperty valueType;
    private SimpleStringProperty units;

    public Parameter(String name, String displayName, String desc, String vis, String val_desc, String valType, String units) {
        this.name = new SimpleStringProperty(name);
        this.displayName = new SimpleStringProperty(displayName);
        this.description = new SimpleStringProperty(desc);
        this.visibility = new SimpleStringProperty(vis);
        this.valueDescription = new SimpleStringProperty(val_desc);
        this.units = new SimpleStringProperty(units);
        this.valueType = new SimpleStringProperty(valType);
    }

    public SimpleStringProperty getDisplayName() {
        return displayName;
    }

    public SimpleStringProperty getDescription() {
        return description;
    }

    public SimpleStringProperty getVisibility() {
        return visibility;
    }

    public SimpleStringProperty getValueDescription() {
        return valueDescription;
    }

    public Object getValue() {
        String _type = valueType.toString();
        String _value = value.toString();
        if (_type.equals("int")) return Integer.parseInt(_value);
        if (_type.equals("float")) return Double.parseDouble(_value);
        return _value;
    }

    public void setValue(SimpleStringProperty value) {
        this.value = value;
    }

    public SimpleStringProperty getValueType() {
        return valueType;
    }

    public SimpleStringProperty getUnits() {
        return units;
    }

    public String toString() {
        return "name: " + name + " displayName: " + displayName;
    }
}
