package com.raytheon;

import org.json.JSONObject;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Main {

    public static void main(String[] args) {
	    ZContext context = new ZContext();
        ZMQ.Socket subscriber = context.createSocket(ZMQ.SUB);
        subscriber.connect("tcp://localhost:58967");
        System.out.println("connected");
        subscriber.subscribe(new byte[0]);

        while (true) {
            JSONObject data = receive(subscriber);
            if (data == null) break;
            double time = data.getDouble("time");
            String type = data.getString("type");

            if (type.equals(MessageTypes.SAMPLE.toString())) {
                Sample sample = new Sample(data.getString("value"));
                System.out.println(sample);
            }
        }
        context.close();
    }

    private static JSONObject receive(ZMQ.Socket subscriber)
    {
        String data = subscriber.recvStr();
        return data == null ? null : new JSONObject(data);
    }
}
