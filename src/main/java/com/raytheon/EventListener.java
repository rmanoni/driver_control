package com.raytheon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * Created by pcable on 7/18/14.
 */
public class EventListener extends Thread {

    private ZContext context;
    private ZMQ.Socket event_socket;
    private static Logger logger = LogManager.getLogger();
    private Thread event_thread;

    public EventListener(String host, int port) {
        logger.debug("Initialize EventListener");
        context = new ZContext();
        event_socket = context.createSocket(ZMQ.SUB);
        // connect to the event port
        event_socket.connect("tcp://" + host + ":" + port);
        event_socket.subscribe(new byte[0]);
        logger.debug("Event socket connected!");
    }

    public void run() {
        while (true) {
            logger.trace("in event loop");
            String data = event_socket.recvStr();
            if (data == null) break;
            JSONObject event = new JSONObject(data);
            double time = event.getDouble("time");
            String type = event.getString("type");
            if (MessageTypes.SAMPLE.equals(type)) {
                DriverSample sample = new DriverSample(event.getString("value"));
                logger.info(sample);
            } else {
                logger.info(event);
            }
        }
        context.close();
    }

    private JSONObject receive(ZMQ.Socket subscriber)
    {
        String data = subscriber.recvStr();
        return data == null ? null : new JSONObject(data);
    }
}
