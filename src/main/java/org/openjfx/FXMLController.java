package org.openjfx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;

public class FXMLController {
    // Size in pixels (px) for titlebar icons.
    private static final int TRAFFIC_LIGHT_ICONS_SIZE = 12;

    // Stage given by MainApp.java
    private Stage stage;

    // CSS ID's used in scene.fxml
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

    /**
     * Creates a custom titlebar with custom decorations
     * (the close, minimize, and resize buttons)
     * imitating the MacOS traffic light icons.
     */
    private void createTitlebar() {
        /*
        // Creates image containers with default icons,
        // then sets their width and height, and then
        // adds the image to their respective button.
        */
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

        // On mouse hover enter on decorations, show descriptive icons
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
        // On mouse hover exit on decorations, show default icons
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

    /**
     * Changes decorations to light-gray when window is
     * not focused, and back to default colors when focused.
     */
    public void handleUnfocusedStage() {
        // Image containers that are put inside of Buttons
        ImageView closeImageView = new ImageView();
        ImageView minimizeImageView = new ImageView();
        ImageView resizeImageView = new ImageView();
        stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                // Changing all three decorations to light gray
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
                // Reverting all three decorations back to default
                setDefaultTitlebarIcons(closeImageView, minimizeImageView, resizeImageView, getClass()
                        .getResource("images/titlebar_icons/small/small_red.png"), getClass()
                        .getResource("images/titlebar_icons/small/small_yellow.png"), getClass()
                        .getResource("images/titlebar_icons/small/small_green.png"));
            }
        });
    }

    /** HELPER FUNCTION
     * Sets decorations to their default traffic light colors.
     *
     * @param closeImageView        image container for the close decoration
     * @param minimizeImageView     image container for the minimize decoration
     * @param resizeImageView       image container for the resize decoration
     * @param closeDecoURL          URL to default close decoration icon
     * @param minimizeDecoURL       URL to default minimize decoration icon
     * @param resizeDecoURL         URL to default resize decoration icon
     */
    private void setDefaultTitlebarIcons(ImageView closeImageView, ImageView minimizeImageView,
            ImageView resizeImageView, URL closeDecoURL, URL minimizeDecoURL, URL resizeDecoURL)
    {
        closeImageView.setImage(new Image(closeDecoURL
                .toExternalForm()));
        closeButton.setGraphic(closeImageView);
        minimizeImageView.setImage(new Image(minimizeDecoURL
                .toExternalForm()));
        minimizeButton.setGraphic(minimizeImageView);
        resizeImageView.setImage(new Image(resizeDecoURL
                .toExternalForm()));
        resizeButton.setGraphic(resizeImageView);
    }

    /**
     * Closes window when close decoration is pressed.
     *
     * @param event mouse press by the user
     */
    public void handleCloseButtonAction(ActionEvent event) {
        stage.close();
    }

    /**
     * Minimizes window when close decoration is pressed.
     *
     * @param event mouse press by the user
     */
    public void handleMinimizeButtonAction(ActionEvent event) {
        stage.toBack();
        stage.setIconified(true);
    }

    /**
     * Creates a reference to the stage
     * from MainApp.java
     *
     * @param stage application window
     */
    public void setStage(Stage stage){
        this.stage = stage;
    }
}