package com.raytheon;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ControlWindow {
    @FXML
    private TableView<ProtocolCommand> commandTable;
    @FXML
    private TableView<Parameter> parameterTable;
    @FXML
    private TableColumn<ProtocolCommand, String> commandColumn;
    @FXML
    private TableColumn<ProtocolCommand, String> commandNameColumn;
    @FXML
    private TableColumn<Parameter, String> parameterNameColumn;
    @FXML
    private TableColumn<Parameter, String> parameterValueColumn;
    @FXML
    private TableColumn<Parameter, String> parameterNewValueColumn;
    @FXML
    public TextArea console;
    @FXML
    private TextField stateField;
    @FXML
    private TextField statusField;
    @FXML
    private Button sendParamButton;


    private DriverModel model;
    private DriverControl controller;
    private EventListener listener;
    private static org.apache.logging.log4j.Logger log = LogManager.getLogger();

    // Listeners
    private ChangeListener<String> stateListener = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
            stateField.setText((observableValue).getValue());
        }
    };

    private ChangeListener<Boolean> settableListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean s, Boolean s2) {
            parameterNewValueColumn.setEditable(observableValue.getValue());
            sendParamButton.setVisible(observableValue.getValue());
        }
    };

    private ListChangeListener<DriverSample> sampleChangeListener = new ListChangeListener<DriverSample>() {
        @Override
        public void onChanged(Change<? extends DriverSample> change) {
            while (change.next()) {
                for (DriverSample sample : change.getAddedSubList()) {
                    console.appendText(sample.toString() + "\n\n");
                }
            }
        }
    };

    @FXML
    private void initialize() {
        commandColumn.setCellValueFactory(new PropertyValueFactory<ProtocolCommand, String>("name"));
        commandNameColumn.setCellValueFactory(new PropertyValueFactory<ProtocolCommand, String>("displayName"));

        parameterNameColumn.setCellValueFactory(new PropertyValueFactory<Parameter, String>("displayName"));
        parameterValueColumn.setCellValueFactory(new PropertyValueFactory<Parameter, String>("value"));
        parameterNewValueColumn.setCellValueFactory(new PropertyValueFactory<Parameter, String>("newValue"));
        parameterNewValueColumn.setCellFactory(TextFieldTableCell.<Parameter>forTableColumn());

        parameterNewValueColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<Parameter, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<Parameter, String> t) {
                        log.debug("setOnEditCommit");
                        t.getTableView().getItems().get(t.getTablePosition().getRow()).setNewValue(t.getNewValue());
                    }
                }
        );
    }

    public void setup(DriverModel model, DriverControl controller, EventListener listener) {
        this.model = model;
        this.controller = controller;
        this.listener = listener;
        commandTable.setItems(model.commandList);
        parameterTable.setItems(model.paramList);
        this.model.getStateProperty().addListener(stateListener);
        this.model.getParamsSettableProperty().addListener(settableListener);
        this.model.sampleList.addListener(sampleChangeListener);
    }

    public void selectCommand(MouseEvent event) {
        try {
            log.debug("received event: " + event);
            TableView source = (TableView) event.getSource();
            int row = source.getSelectionModel().getSelectedIndex();
            ProtocolCommand command = model.commandList.get(row);
            log.debug("maybe I clicked: " + command);
            controller.execute(command.getName());
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public void sendParams() {
        log.debug("clicked send params");
        Map<String, Object> values = new HashMap<String, Object>();
        for (Parameter p: model.parameters.values()) {
            Object sendValue;
            String newValue = p.getNewValue();
            String oldValue = p.getValue();
            if (newValue.equals("")) continue;
            if (newValue.equals(oldValue)) continue;
            String type = p.getValueType();
            if (type.equals("int")) {
                sendValue = Integer.parseInt(newValue);
            } else if (type.equals("float")) {
                sendValue = Double.parseDouble(newValue);
            } else {
                sendValue = newValue;
            }
            values.put(p.getName(), sendValue);
        }
        for (Parameter p: model.parameters.values()) {
            p.setNewValue("");
        }
        controller.setResource(new JSONObject(values).toString());
    }
}
