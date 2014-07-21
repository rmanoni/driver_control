package com.raytheon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

/**
 * Created by pcable on 7/18/14.
 */
public class EventListener extends Thread {

    private ZContext context;
    private ZMQ.Socket event_socket;
    private static Logger log = LogManager.getLogger();
    private Thread event_thread;
    private boolean keepRunning = true;
    private DriverModel model;

    public EventListener(String host, int port, DriverModel model) {
        this.model = model;
        log.debug("Initialize EventListener");
        context = new ZContext();
        event_socket = context.createSocket(ZMQ.SUB);
        // connect to the event port
        event_socket.connect("tcp://" + host + ":" + port);
        event_socket.subscribe(new byte[0]);
        log.debug("Event socket connected!");
    }

    public void run() {
        while (keepRunning) {
            ZMsg msg = ZMsg.recvMsg(event_socket, ZMQ.NOBLOCK);
            if (msg == null) {
                try { Thread.sleep(100); } catch (InterruptedException e1) { }
                continue;
            }
            for (Object aMsg : msg) {
                String data = aMsg.toString();
                if (data == null) break;
                JSONObject event = new JSONObject(data);
                double time = event.getDouble("time");
                String type = event.getString("type");

                if (MessageTypes.SAMPLE.equals(type)) {
                    DriverSample sample = new DriverSample(event.getString("value"));
                    log.info(sample);
                } else if (MessageTypes.CONFIG_CHANGE.equals(type)) {
                    log.info("Received CONFIG_CHANGE event: " + event);
                    model.setParams(event.getJSONObject("value"));
                } else {
                    log.info(event);
                }
            }
        }
        context.close();
    }

    public void shutdown() {
        keepRunning = false;
    }

    private JSONObject receive(ZMQ.Socket subscriber)
    {
        String data = subscriber.recvStr();
        return data == null ? null : new JSONObject(data);
    }
}
