package com.raytheon;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Main extends Application {
    private static Logger logger = LogManager.getLogger();
    private EventListener listener;
    private DriverControl controller;
    private DriverModel model;

    private int getPort(String filename) throws Exception {
        Path path = Paths.get(filename);
        String contents = new String(Files.readAllBytes(path));
        return Integer.parseInt(contents.trim());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Map params = getParameters().getNamed();
        int event_port = getPort((String) params.get("event_file"));
        int command_port = getPort((String) params.get("command_file"));

        String driver_host = "localhost";
        model = new DriverModel();
        listener = new EventListener(driver_host, event_port, model);
        controller = new DriverControl(driver_host, command_port, model);

        listener.start();
        controller.ping();
        controller.getProtocolState();
        if (model.getState().equals("DRIVER_STATE_UNKNOWN")) {
            controller.configure();
            controller.init();
            controller.connect();
            controller.discover();
        }
        controller.getMetadata();
        controller.getCapabilities();
        if (model.getState().equals("DRIVER_STATE_COMMAND")) {
            controller.execute("DRIVER_EVENT_ACQUIRE_SAMPLE");
        }

        Parent root = FXMLLoader.load(getClass().getResource("/ControlWindow.fxml"));
        primaryStage.setTitle("DriverControl");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    @Override
    public void stop() {
        logger.info("Shutting down listener");
        listener.shutdown();
        logger.info("Shutting down driver");
        //controller.stop();
        try {
            listener.join();
        } catch (InterruptedException e) {
            System.exit(1);
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
