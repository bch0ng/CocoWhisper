package org.openjfx;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/openjfx/fxml/scene.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 390, 660);
        // Sends stage to FXMLController.java and initializes
        // handling when the window unfocuses.
        ((FXMLController) loader.getController()).setStage(stage);
        scene.getStylesheets().add(getClass().getResource("/org/openjfx/css/titlebar.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/org/openjfx/css/colors.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/org/openjfx/css/styles.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/org/openjfx/css/nav.css").toExternalForm());
        // Required to round the window borders
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws Exception
    {
        Application.launch(args);
    }

}
