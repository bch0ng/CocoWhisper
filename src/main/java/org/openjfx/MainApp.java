package org.openjfx;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {
    private double xOffset = 0; // x-location of window
    private double yOffset = 0; // y-location of window

    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scene.fxml"));
        Parent root = loader.load();
        // Moves window when pressed and dragged around
        root.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        Scene scene = new Scene(root, 387, 660);
        // Sends stage to FXMLController.java and initializes
        // handling when the window unfocuses.
        ((FXMLController) loader.getController()).setStage(stage);
        ((FXMLController) loader.getController()).handleUnfocusedStage();
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        // Required to round the window borders
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws Exception
    {
        // For testing purposes
        //Client c = new Client();
        //c.main(args);

        // Connecting GUI to client package.
        //XMPPConnection x = new XMPPConnection("test1", "testing1234");
        //System.out.println("Success!");
        Application.launch(args);
    }

}
