package com.raytheon;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by pcable on 7/17/14.
 *
 */
public class Sample {
    private String raw;
    private JSONObject json;
    private JSONArray values;
    private String stream_name;
    private String quality_flag;
    private String preferred_timestamp;
    private double port_timestamp;
    private double driver_timestamp;
    private String pkt_format_id;
    private int pkt_version;

    public Sample(String s) {
        this.raw = s;
        this.json = new JSONObject(s);
        this.stream_name = this.json.getString("stream_name");
        this.quality_flag = this.json.getString("quality_flag");
        this.preferred_timestamp = this.json.getString("preferred_timestamp");
        this.port_timestamp = this.json.getDouble("port_timestamp");
        this.driver_timestamp = this.json.getDouble("driver_timestamp");
        this.pkt_format_id = this.json.getString("pkt_format_id");
        this.pkt_version = this.json.getInt("pkt_version");
        this.values = this.json.getJSONArray("values");
    }
    public String toString() {
        return this.stream_name + " " + this.values;
    }

    public JSONObject getJson() {
        return this.json;
    }

    public String getRaw() {
        return raw;
    }

    public JSONArray getValues() {
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
}
