package com.raytheon;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DriverModel {
    private String protocolState;
    private final ObservableList<ProtocolCommand> commandList = FXCollections.observableArrayList();
    private final ObservableList<Parameter> paramList = FXCollections.observableArrayList();
    private static Logger log = LogManager.getLogger();
    private Map<String, ProtocolCommand> commands = new HashMap<String, ProtocolCommand>();
    private Map<String, Parameter> parameters = new HashMap<String, Parameter>();
    private String state;

    public DriverModel() {
        // do something here, probably need some handles
        log.debug("Created driver model");
    }

    public String maybeString(Object s) {
        if (s == null) {
            return "";
        }
        if (s instanceof String) {
            return (String) s;
        }
        return s.toString();
    }

    public String getString(JSONObject object, String key) {
        if (object.has(key)) {
            Object val = object.get(key);
            return maybeString(val);
        }
        return "";
    }

    public void parseMetadata(JSONObject metadata) {
        JSONObject _commands = metadata.getJSONObject("commands");
        JSONObject _parameters = metadata.getJSONObject("parameters");
        for (Object _name: _commands.keySet()) {
            String name = (String) _name;
            String displayName = getString(_commands.getJSONObject(name), "display_name");
            ProtocolCommand command = new ProtocolCommand(name, displayName);
            commands.put(name, command);
        }

        for (Object _name: _parameters.keySet()) {
            String name = (String) _name;
            JSONObject param = _parameters.getJSONObject(name);
            String displayName = getString(param, "display_name");
            String visibility = getString(param, "visibility");
            String description = getString(param, "description");

            JSONObject value = param.getJSONObject("value");
            String valueDescription = getString(value, "description");
            String valueType = getString(value, "type");
            String units = getString(value, "units");
            Parameter paramObj = new Parameter(name, displayName, description, visibility,
                    valueDescription, valueType, units);
            parameters.put(name, paramObj);
        }
        log.debug(commands);
        log.debug(parameters);
    }

    public void parseCapabilities(JSONArray capes) {
        commandList.removeAll();
        for (int i=0; i<capes.length(); i++) {
            commandList.add(commands.get(capes.getString(i)));
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
