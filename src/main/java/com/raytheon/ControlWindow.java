package com.raytheon;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;

public class ControlWindow {
    @FXML
    private TableView<ProtocolCommand> commandTable;
    @FXML
    private TableView<Parameter> parameterTable;
    @FXML
    private TableColumn commandColumn;
    @FXML
    private TableColumn commandNameColumn;
    @FXML
    private TableColumn parameterNameColumn;
    @FXML
    private TableColumn parameterValueColumn;
    @FXML
    public TextArea console;

    private DriverModel model;
    private DriverControl controller;
    private EventListener listener;
    private static org.apache.logging.log4j.Logger log = LogManager.getLogger();
    private ChangeListener changeListener = new ChangeListener() {
        @Override
        public void changed(ObservableValue observableValue, Object o, Object o2) {
            console.setText(((SimpleStringProperty) observableValue).get());
        }
    };

    @FXML
    private void initialize() {
        commandColumn.setCellValueFactory(new PropertyValueFactory<ProtocolCommand, String>("name"));
        commandNameColumn.setCellValueFactory(new PropertyValueFactory<ProtocolCommand, String>("displayName"));
        parameterNameColumn.setCellValueFactory(new PropertyValueFactory<ProtocolCommand, String>("displayName"));
        parameterValueColumn.setCellValueFactory(new PropertyValueFactory<ProtocolCommand, String>("value"));
    }

    public void setup(DriverModel model, DriverControl controller, EventListener listener) {
        this.model = model;
        this.controller = controller;
        this.listener = listener;
        commandTable.setItems(model.commandList);
        parameterTable.setItems(model.paramList);
        SimpleStringProperty state = this.model.getStateProperty();
        state.addListener(changeListener);
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
            e.printStackTrace();
        }
    }
}
