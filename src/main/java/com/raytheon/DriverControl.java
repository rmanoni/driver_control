package com.raytheon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.InputStream;
import java.util.Map;

public class DriverControl {
    private ZMQ.Socket command_socket;
    private static Logger log = LogManager.getLogger();
    private String portAgentFile = "/port_agent.yaml";
    private String startupConfigFile = "/startup_config.yaml";
    private DriverModel model;

    public DriverControl(String host, int port, DriverModel model) {
        this.model = model;
        log.debug("Initialize DriverControl");
        ZContext context = new ZContext();
        command_socket = context.createSocket(ZMQ.REQ);
        command_socket.connect("tcp://" + host + ":" + port);
        log.debug("Command socket connected!");
    }

    private String getConfig(String configFile) {
        JSONObject config;
        Map map;
        Yaml yaml = new Yaml();
        InputStream configStream = getClass().getResourceAsStream(configFile);
        map = (Map) yaml.load(configStream);
        config = new JSONObject(map);
        return config.toString();
    }

    protected void ping() {
        sendCommand(buildCommand(DriverCommandEnum.PING, "ping from java"));
    }

    protected void configure() {
        sendCommand(buildCommand(DriverCommandEnum.CONFIGURE, getConfig(portAgentFile)));
    }

    protected void init() {
        sendCommand(buildCommand(DriverCommandEnum.SET_INIT_PARAMS, getConfig(startupConfigFile)));
    }

    protected void connect() {
        sendCommand(buildCommand(DriverCommandEnum.CONNECT));
    }

    protected void discover() {
        sendCommand(buildCommand(DriverCommandEnum.DISCOVER_STATE));
    }

    protected void stop() {
        sendCommand(buildCommand(DriverCommandEnum.STOP_DRIVER));
    }

    protected void getMetadata() {
        String reply = sendCommand(buildCommand(DriverCommandEnum.GET_CONFIG_METADATA));
        reply = reply.substring(1,reply.length()-1).replace("\\\"", "\"");
        model.parseMetadata(new JSONObject(reply));
    }

    protected void getCapabilities() {
        String reply = sendCommand(buildCommand(DriverCommandEnum.GET_CAPABILITIES));
        JSONArray capes = new JSONArray(reply).getJSONArray(0);
        model.parseCapabilities(capes);
    }

    protected void execute(String command) {
        sendCommand(buildCommand(DriverCommandEnum.EXECUTE_RESOURCE, command));
    }

    protected void getProtocolState() {
        String state = sendCommand(buildCommand(DriverCommandEnum.GET_RESOURCE_STATE));
        model.setState(state);
    }

    protected void getResource(String... resources) {
        String reply = sendCommand(buildCommand(DriverCommandEnum.GET_RESOURCE, resources));
        model.setParams(new JSONObject(reply));
    }

    private String sendCommand(JSONObject command) {
        command_socket.send(command.toString());
        String reply = command_socket.recvStr();
        log.debug("received reply: " + reply);
        return reply;
    }

    private JSONObject buildCommand(DriverCommandEnum command, String... args) {
        JSONObject message = new JSONObject();
        JSONObject keyword_args = new JSONObject();
        JSONArray message_args = new JSONArray();

        for (String arg: args) {
            try {
                message_args.put(new JSONObject(arg));
            } catch (JSONException e) {
                message_args.put(arg);
            }
        }
        message.put("cmd", command);
        message.put("args", message_args);
        message.put("kwargs", keyword_args);
        log.debug("BUILT COMMAND: " + message);
        return message;
    }
}
