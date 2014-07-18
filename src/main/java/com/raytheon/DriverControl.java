package com.raytheon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class DriverControl {
    private ZMQ.Socket command_socket;
    private static Logger logger = LogManager.getLogger();
    private String portAgentConfigFile = "/Users/pcable/IdeaProjects/driver_control/src/main/resources/port_agent.yaml";
    private String startupConfigFile = "/Users/pcable/IdeaProjects/driver_control/src/main/resources/startup_config.yaml";

    public DriverControl(String host, int port) {
        logger.debug("Initialize DriverControl");
        ZContext context = new ZContext();
        command_socket = context.createSocket(ZMQ.REQ);
        command_socket.connect("tcp://" + host + ":" + port);
        logger.debug("Command socket connected!");
    }

    private String getConfig(String configFile) {
        JSONObject config;
        Map map;
        try {
            Yaml yaml = new Yaml();
            map = (Map) yaml.load(new FileInputStream(configFile));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        config = new JSONObject(map);
        return config.toString();
    }

    private void command_loop() {
        sendCommand(build_command(DriverCommands.PING, "ping from java"));
        sendCommand(build_command(DriverCommands.CONFIGURE, getConfig(portAgentConfigFile)));
        sendCommand(build_command(DriverCommands.SET_INIT_PARAMS, getConfig(startupConfigFile)));
        sendCommand(build_command(DriverCommands.CONNECT));
        sendCommand(build_command(DriverCommands.DISCOVER_STATE));
    }

    private String sendCommand(JSONObject command) {
        command_socket.send(command.toString());
        String reply = command_socket.recvStr();
        logger.debug("received reply: " + reply);
        return reply;
    }

    private JSONObject build_command(DriverCommands command, String... args) {
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
        logger.debug("BUILT COMMAND: " + message);
        return message;
    }

    public static void main(String[] args) {
        logger.info("Starting DriverControl");
        DriverControl controller = new DriverControl("localhost", 60812);
        EventListener listener = new EventListener("localhost", 52857);
        listener.start();
        controller.command_loop();
    }
}
