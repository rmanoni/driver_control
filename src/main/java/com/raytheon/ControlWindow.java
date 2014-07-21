package com.raytheon;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private static org.apache.logging.log4j.Logger log = LogManager.getLogger();

    @FXML
    private void initialize() {
        commandColumn.setCellValueFactory(new PropertyValueFactory<ProtocolCommand, String>("name"));
        commandNameColumn.setCellValueFactory(new PropertyValueFactory<ProtocolCommand, String>("displayName"));
        parameterNameColumn.setCellValueFactory(new PropertyValueFactory<ProtocolCommand, String>("displayName"));
        parameterValueColumn.setCellValueFactory(new PropertyValueFactory<ProtocolCommand, String>("value"));
    }

    public void setModel(DriverModel model) {
        this.model = model;
        commandTable.setItems(model.commandList);
        parameterTable.setItems(model.paramList);
    }
}
