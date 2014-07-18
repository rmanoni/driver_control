package com.raytheon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.yaml.snakeyaml.Yaml;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class DriverControl {
    private ZContext context;
    private ZMQ.Socket event_socket;
    private ZMQ.Socket command_socket;
    private String host;
    private int event_port;
    private int command_port;
    private static Logger logger = LogManager.getLogger();
    private Thread event_thread;
    private String portAgentConfigFile;
    private String startupConfigFile;

    public String getPortAgentConfigFile() {
        return portAgentConfigFile;
    }

    @Option(name="-portconfig", usage="Sets the path to the port agent config file")
    public void setPortAgentConfigFile(String portAgentConfigFile) {
        this.portAgentConfigFile = portAgentConfigFile;
    }

    public String getStartupConfigFile() {
        return startupConfigFile;
    }

    @Option(name="-startupconfig", usage="Sets the path to the startup config file")
    public void setStartupConfigFile(String startupConfigFile) {
        this.startupConfigFile = startupConfigFile;
    }

    public DriverControl() {}

    public class EventLoop implements Runnable {
        public void run() {
            while (true) {
                logger.trace("in event loop");
                JSONObject data = receive(event_socket);
                if (data == null) break;
                double time = data.getDouble("time");
                String type = data.getString("type");

                if (MessageTypes.SAMPLE.equals(type)) {
                    DriverSample sample = new DriverSample(data.getString("value"));
                    logger.info(sample);
                }
            }
            context.close();
        }
    }

    public void initialize() {
        logger.debug("Initialize DriverControl");
        context = new ZContext();
        event_socket = context.createSocket(ZMQ.SUB);
        command_socket = context.createSocket(ZMQ.REQ);
        // connect to the event port
        event_socket.connect("tcp://" + host + ":" + event_port);
        event_socket.subscribe(new byte[0]);
        logger.debug("Event socket connected!");
        // connect to the command port
        command_socket.connect("tcp://" + host + ":" + command_port);
        logger.debug("Command socket connected!");
    }

    private void start_event_loop() {
        event_thread = new Thread(new EventLoop());
        event_thread.start();
    }

    private String getConfig(String configfile) {
        JSONObject config;
        Map map;
        try {
            Yaml yaml = new Yaml();
            map = (Map) yaml.load(new FileInputStream(configfile));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        config = new JSONObject(map);
        return config.toString();
    }

    private String getStartupConfig() {
        JSONObject config = new JSONObject();
        JSONObject parameters = new JSONObject();
        config.put("parameters", parameters);
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

    private JSONObject receive(ZMQ.Socket subscriber)
    {
        String data = subscriber.recvStr();
        return data == null ? null : new JSONObject(data);
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
        DriverControl controller = new DriverControl();
        CmdLineParser parser = new CmdLineParser(controller);
        try {
            parser.parseArgument(args);
            controller.initialize();
            controller.start_event_loop();
            controller.command_loop();
        } catch (CmdLineException e) {
            logger.error("Unable to parse command line arguments.");
        }
        try {
            controller.event_thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getHost() {
        return host;
    }

    @Option(name="-host", usage="Sets the hostname for the driver")
    public void setHost(String host) {
        this.host = host;
    }

    public int getEvent_port() {
        return event_port;
    }

    @Option(name="-event_port", usage="Sets the port for events")
    public void setEvent_port(int event_port) {
        this.event_port = event_port;
    }

    public int getCommand_port() {
        return command_port;
    }

    @Option(name="-command_port", usage="Sets the port for commands")
    public void setCommand_port(int command_port) {
        this.command_port = command_port;
    }
}
