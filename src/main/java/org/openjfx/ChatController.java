package org.openjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ChatController {

    private Stage stage;

    @FXML private BorderPane titleCont;
    @FXML private AnchorPane titlebar;
    @FXML private TitlebarController titlebarController;

    public void initialize() {
        titlebar.setPickOnBounds(false);
        titlebar.toFront();
        titleCont.setPickOnBounds(false);
        titleCont.toFront();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        titlebarController.setStage(this.stage);
    }

}
