package org.openjfx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.EventObject;

public class FXMLController {
    private static final int TRAFFIC_LIGHT_ICONS_SIZE = 12;

    private Stage stage;

    @FXML
    private HBox titlebar;
    @FXML
    private Button closeButton;
    @FXML
    private Button minimizeButton;
    @FXML
    private Button resizeButton;
    @FXML
    private HBox content;
    @FXML
    private VBox nav;
    @FXML
    private VBox contacts;

    public void initialize() {
        this.createTitlebar();

    }

    private void createTitlebar() {
        ImageView closeImageView = new ImageView(getClass()
                .getResource("images/titlebar_icons/small/small_red.png").toExternalForm());
        closeImageView.setFitWidth(TRAFFIC_LIGHT_ICONS_SIZE);
        closeImageView.setFitHeight(TRAFFIC_LIGHT_ICONS_SIZE);
        closeButton.setGraphic(closeImageView);
        ImageView minimizeImageView = new ImageView(getClass()
                .getResource("images/titlebar_icons/small/small_yellow.png").toExternalForm());
        minimizeImageView.setFitWidth(TRAFFIC_LIGHT_ICONS_SIZE);
        minimizeImageView.setFitHeight(TRAFFIC_LIGHT_ICONS_SIZE);
        minimizeButton.setGraphic(minimizeImageView);
        ImageView resizeImageView = new ImageView(getClass()
                .getResource("images/titlebar_icons/small/small_green.png").toExternalForm());
        resizeImageView.setFitWidth(TRAFFIC_LIGHT_ICONS_SIZE);
        resizeImageView.setFitHeight(TRAFFIC_LIGHT_ICONS_SIZE);
        resizeButton.setGraphic(resizeImageView);
        titlebar.setOnMouseEntered(new EventHandler<>() {
            @Override
            public void handle(MouseEvent t)
            {
                closeImageView.setImage(new Image(getClass()
                        .getResource("images/titlebar_icons/small/small_red_hover.png")
                        .toExternalForm()));
                closeButton.setGraphic(closeImageView);
                minimizeImageView.setImage(new Image(getClass()
                        .getResource("images/titlebar_icons/small/small_yellow_hover.png")
                        .toExternalForm()));
                minimizeButton.setGraphic(minimizeImageView);
                resizeImageView.setImage(new Image(getClass()
                        .getResource("images/titlebar_icons/small/small_green_hover.png")
                        .toExternalForm()));
                resizeButton.setGraphic(resizeImageView);
            }
        });
        titlebar.setOnMouseExited(new EventHandler<>() {
            @Override
            public void handle(MouseEvent t)
            {
                setDefaultTitlebarIcons(closeImageView, minimizeImageView, resizeImageView, getClass()
                        .getResource("images/titlebar_icons/small/small_red.png"), getClass()
                                .getResource("images/titlebar_icons/small/small_yellow.png"), getClass()
                                        .getResource("images/titlebar_icons/small/small_green.png"));
            }
        });
        closeButton.setOnMousePressed((MouseEvent e) ->
                closeImageView.setImage(new Image(getClass()
                        .getResource("images/titlebar_icons/small/small_red_active.png")
                        .toExternalForm())));
        minimizeButton.setOnMousePressed((MouseEvent e) ->
                minimizeImageView.setImage(new Image(getClass()
                        .getResource("images/titlebar_icons/small/small_yellow_active.png")
                        .toExternalForm())));
        resizeButton.setOnMousePressed((MouseEvent e) ->
                resizeImageView.setImage(new Image(getClass()
                        .getResource("images/titlebar_icons/small/small_green_active.png")
                        .toExternalForm())));
    }

    public void handleUnfocusedStage() {
        ImageView closeImageView = new ImageView();
        ImageView minimizeImageView = new ImageView();
        ImageView resizeImageView = new ImageView();
        stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                closeImageView.setImage(new Image(getClass()
                        .getResource("images/titlebar_icons/small/small_unfocused.png")
                        .toExternalForm()));
                closeButton.setGraphic(closeImageView);
                minimizeImageView.setImage(new Image(getClass()
                        .getResource("images/titlebar_icons/small/small_unfocused.png")
                        .toExternalForm()));
                minimizeButton.setGraphic(minimizeImageView);
                resizeImageView.setImage(new Image(getClass()
                        .getResource("images/titlebar_icons/small/small_unfocused.png")
                        .toExternalForm()));
                resizeButton.setGraphic(resizeImageView);
            } else {
                setDefaultTitlebarIcons(closeImageView, minimizeImageView, resizeImageView, getClass()
                        .getResource("images/titlebar_icons/small/small_red.png"), getClass()
                        .getResource("images/titlebar_icons/small/small_yellow.png"), getClass()
                        .getResource("images/titlebar_icons/small/small_green.png"));
            }
        });
    }

    private void setDefaultTitlebarIcons(ImageView closeImageView, ImageView minimizeImageView, ImageView resizeImageView, URL resource, URL resource2, URL resource3) {
        closeImageView.setImage(new Image(resource
                .toExternalForm()));
        closeButton.setGraphic(closeImageView);
        minimizeImageView.setImage(new Image(resource2
                .toExternalForm()));
        minimizeButton.setGraphic(minimizeImageView);
        resizeImageView.setImage(new Image(resource3
                .toExternalForm()));
        resizeButton.setGraphic(resizeImageView);
    }

    public void handleCloseButtonAction(ActionEvent event) {
        stage.close();
    }

    public void handleMinimizeButtonAction(ActionEvent event) {
        stage.toBack();
        stage.setIconified(true);
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }
}