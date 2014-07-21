package com.raytheon;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pcable on 7/17/14.
 *
 */
public class DriverSample {
    private String raw;
    private Map<String, Object> values;
    private String stream_name;
    private String quality_flag;
    private String preferred_timestamp;
    private double port_timestamp;
    private double driver_timestamp;
    private String pkt_format_id;
    private int pkt_version;

    public DriverSample(String s) {
        raw = s;
        JSONObject json = new JSONObject(s);
        stream_name = json.getString("stream_name");
        quality_flag = json.getString("quality_flag");
        preferred_timestamp = json.getString("preferred_timestamp");
        port_timestamp =  json.getDouble("port_timestamp");
        driver_timestamp =  json.getDouble("driver_timestamp");
        pkt_format_id =  json.getString("pkt_format_id");
        pkt_version =  json.getInt("pkt_version");
        JSONArray json_values =  json.getJSONArray("values");
        values = new HashMap<String, Object>();

        for (int i=0; i<json_values.length(); i++) {
            JSONObject element = (JSONObject) json_values.get(i);
            values.put(element.getString("value_id"), element.get("value"));
        }
    }
    public String toString() {
        return this.stream_name + " " + this.values;
    }

    public String getRaw() {
        return raw;
    }

    public Map getValues() {
        return values;
    }

    public String getQuality_flag() {
        return quality_flag;
    }

    public String getPreferred_timestamp() {
        return preferred_timestamp;
    }

    public double getPort_timestamp() {
        return port_timestamp;
    }

    public double getDriver_timestamp() {
        return driver_timestamp;
    }

    public String getPkt_format_id() {
        return pkt_format_id;
    }

    public int getPkt_version() {
        return pkt_version;
    }

    public Object getValue(String key) { return values.get(key); }
}
