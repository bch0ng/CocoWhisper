package org.openjfx;

import client.Client;
import client.XMPPConnection;
import javafx.application.Application;
import static javafx.application.Application.launch;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scene.fxml"));
        Parent root = loader.load();
        root.setOnMousePressed((MouseEvent event) -> {
            System.out.println("lol");
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        Scene scene = new Scene(root, 387, 660);
        ((FXMLController) loader.getController()).setStage(stage);
        ((FXMLController) loader.getController()).handleUnfocusedStage();
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("JavaFX and Gradle");
        stage.setScene(scene);
        stage.show();

        ToolBar toolBar = new ToolBar();
    }

    public static void main(String[] args) throws Exception
    {
        // For testing purposes
        //Client c = new Client();
        //c.main(args);
        //XMPPConnection x = new XMPPConnection("test1", "testing1234");
        //System.out.println("Success!");
        Application.launch(args);
    }

}
