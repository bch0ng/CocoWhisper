package org.openjfx;

import client.ChattyXMPPConnection;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jivesoftware.smack.roster.RosterEntry;

import java.net.URL;
import java.util.Collection;

public class FXMLController {
    // Size in pixels (px) for titlebar icons.
    private static final int TRAFFIC_LIGHT_ICONS_SIZE = 12;

    // Decoration image paths
    private static final String CLOSE_DECO_DEFAULT_PATH = "images/titlebar_icons/medium/red.png";
    private static final String CLOSE_DECO_HOVER_PATH = "images/titlebar_icons/medium/red_hover.png";
    private static final String CLOSE_DECO_ACTIVE_PATH = "images/titlebar_icons/medium/red_active.png";
    private static final String MIN_DECO_DEFAULT_PATH = "images/titlebar_icons/medium/yellow.png";
    private static final String MIN_DECO_HOVER_PATH = "images/titlebar_icons/medium/yellow_hover.png";
    private static final String MIN_DECO_ACTIVE_PATH = "images/titlebar_icons/medium/yellow_active.png";
    private static final String RESIZE_DECO_DEFAULT_PATH = "images/titlebar_icons/medium/green.png";
    private static final String RESIZE_DECO_HOVER_PATH = "images/titlebar_icons/medium/green_hover.png";
    private static final String RESIZE_DECO_ACTIVE_PATH = "images/titlebar_icons/medium/green_active.png";
    private static final String UNFOCUSED_DECO_PATH = "images/titlebar_icons/medium/unfocused.png";

    private Stage stage;            // Stage given by MainApp.java
    private boolean isFocused;      // True if window is focused; else false
    private boolean isMaximized;    // True if window is bordered fullscreen, else false
    private double oldWindowX;      // x-position of window on screen (in px)
    private double oldWindowY;      // y-position of window on screen (in px)
    private ChattyXMPPConnection connection;

    // CSS ID's used in scene.fxml
    @FXML private HBox titlebar;
    @FXML private Button closeButton;
    @FXML private Button minimizeButton;
    @FXML private Button resizeButton;
    @FXML private HBox content;
    @FXML private VBox nav;
    @FXML private VBox navMain;
    @FXML private VBox navSecondary;
    @FXML private VBox contacts;
    @FXML private BorderPane layout;
    @FXML private VBox test;
    @FXML private Button friends;
    @FXML private VBox view;

    public void initialize() {
        this.isMaximized = false;
        try {
            connection = new ChattyXMPPConnection("test1", "testing1234");
            System.out.println("worked!");
        } catch (Exception e) {
            System.out.println("oh no");
        }
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
                .getResource(CLOSE_DECO_DEFAULT_PATH).toExternalForm());
        closeImageView.setFitWidth(TRAFFIC_LIGHT_ICONS_SIZE);
        closeImageView.setFitHeight(TRAFFIC_LIGHT_ICONS_SIZE);
        closeButton.setGraphic(closeImageView);

        ImageView minimizeImageView = new ImageView(getClass()
                .getResource(MIN_DECO_DEFAULT_PATH).toExternalForm());
        minimizeImageView.setFitWidth(TRAFFIC_LIGHT_ICONS_SIZE);
        minimizeImageView.setFitHeight(TRAFFIC_LIGHT_ICONS_SIZE);
        minimizeButton.setGraphic(minimizeImageView);

        ImageView resizeImageView = new ImageView(getClass()
                .getResource(RESIZE_DECO_DEFAULT_PATH).toExternalForm());
        resizeImageView.setFitWidth(TRAFFIC_LIGHT_ICONS_SIZE);
        resizeImageView.setFitHeight(TRAFFIC_LIGHT_ICONS_SIZE);
        resizeButton.setGraphic(resizeImageView);

        // On mouse hover enter on decorations, show descriptive icons
        titlebar.setOnMouseEntered(new EventHandler<>() {
            @Override
            public void handle(MouseEvent t)
            {
                closeImageView.setImage(new Image(getClass()
                        .getResource(CLOSE_DECO_HOVER_PATH)
                        .toExternalForm()));
                closeButton.setGraphic(closeImageView);
                minimizeImageView.setImage(new Image(getClass()
                        .getResource(MIN_DECO_HOVER_PATH)
                        .toExternalForm()));
                minimizeButton.setGraphic(minimizeImageView);
                resizeImageView.setImage(new Image(getClass()
                        .getResource(RESIZE_DECO_HOVER_PATH)
                        .toExternalForm()));
                resizeButton.setGraphic(resizeImageView);
            }
        });
        // On mouse hover exit on decorations, show default icons
        titlebar.setOnMouseExited(new EventHandler<>() {
            @Override
            public void handle(MouseEvent t)
            {
                if (isFocused) {
                    setDefaultTitlebarIcons(closeImageView, minimizeImageView, resizeImageView, getClass()
                            .getResource(CLOSE_DECO_DEFAULT_PATH), getClass()
                            .getResource(MIN_DECO_DEFAULT_PATH), getClass()
                            .getResource(RESIZE_DECO_DEFAULT_PATH));
                } else {
                    setDefaultTitlebarIcons(closeImageView, minimizeImageView, resizeImageView, getClass()
                            .getResource(UNFOCUSED_DECO_PATH), getClass()
                            .getResource(UNFOCUSED_DECO_PATH), getClass()
                            .getResource(UNFOCUSED_DECO_PATH));
                }
            }
        });
        closeButton.setOnMousePressed((MouseEvent e) ->
                closeImageView.setImage(new Image(getClass()
                        .getResource(CLOSE_DECO_ACTIVE_PATH)
                        .toExternalForm())));
        minimizeButton.setOnMousePressed((MouseEvent e) ->
                minimizeImageView.setImage(new Image(getClass()
                        .getResource(MIN_DECO_ACTIVE_PATH)
                        .toExternalForm())));
        resizeButton.setOnMousePressed((MouseEvent e) ->
                resizeImageView.setImage(new Image(getClass()
                        .getResource(RESIZE_DECO_ACTIVE_PATH)
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
        this.stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            this.isFocused = isNowFocused;
            if (!isNowFocused) {
                // Changing all three decorations to light gray
                closeImageView.setImage(new Image(getClass()
                        .getResource(UNFOCUSED_DECO_PATH)
                        .toExternalForm()));
                closeButton.setGraphic(closeImageView);
                minimizeImageView.setImage(new Image(getClass()
                        .getResource(UNFOCUSED_DECO_PATH)
                        .toExternalForm()));
                minimizeButton.setGraphic(minimizeImageView);
                resizeImageView.setImage(new Image(getClass()
                        .getResource(UNFOCUSED_DECO_PATH)
                        .toExternalForm()));
                resizeButton.setGraphic(resizeImageView);
            } else {
                // Reverting all three decorations back to default
                setDefaultTitlebarIcons(closeImageView, minimizeImageView, resizeImageView, getClass()
                        .getResource(CLOSE_DECO_DEFAULT_PATH), getClass()
                        .getResource(MIN_DECO_DEFAULT_PATH), getClass()
                        .getResource(RESIZE_DECO_DEFAULT_PATH));
            }
        });
    }

    /** HELPER FUNCTION
     * Sets decorations to their default traffic light colors.
     *
     * @param closeImageView        image container for the close decoration
     * @param minimizeImageView     image container for the minimize decoration
     * @param resizeImageView       image container for the resize decoration
     * @param closeDecoPath          URL to default close decoration icon
     * @param minimizeDecoPath       URL to default minimize decoration icon
     * @param resizeDecoPath         URL to default resize decoration icon
     */
    private void setDefaultTitlebarIcons(ImageView closeImageView, ImageView minimizeImageView,
            ImageView resizeImageView, URL closeDecoPath, URL minimizeDecoPath, URL resizeDecoPath)
    {
        closeImageView.setImage(new Image(closeDecoPath
                .toExternalForm()));
        closeImageView.setFitWidth(TRAFFIC_LIGHT_ICONS_SIZE);
        closeImageView.setFitHeight(TRAFFIC_LIGHT_ICONS_SIZE);
        closeButton.setGraphic(closeImageView);
        minimizeImageView.setImage(new Image(minimizeDecoPath
                .toExternalForm()));
        minimizeImageView.setFitWidth(TRAFFIC_LIGHT_ICONS_SIZE);
        minimizeImageView.setFitHeight(TRAFFIC_LIGHT_ICONS_SIZE);
        minimizeButton.setGraphic(minimizeImageView);
        resizeImageView.setImage(new Image(resizeDecoPath
                .toExternalForm()));
        resizeImageView.setFitWidth(TRAFFIC_LIGHT_ICONS_SIZE);
        resizeImageView.setFitHeight(TRAFFIC_LIGHT_ICONS_SIZE);
        resizeButton.setGraphic(resizeImageView);
    }

    /**
     * Closes window when close decoration is pressed.
     *
     * @param event mouse press by the user
     */
    public void handleCloseButtonAction(ActionEvent event) {
        this.stage.close();
    }

    /**
     * Minimizes window when the close decoration is pressed.
     *
     * @param event mouse press by the user
     */
    public void handleMinimizeButtonAction(ActionEvent event) {
        this.stage.toBack();
        this.stage.setIconified(true);
    }

    /**
     * Resizes window (to windowed fullscreen or back to original size)
     * when the resize decoration is pressed.
     *
     * @param event mouse press by the user
     */
    public void handleResizeButtonAction(ActionEvent event) {
        if (!isMaximized) {
            this.isMaximized = true;
            // Remembers old window position (for when resize decoration is pressed again)
            this.oldWindowX = this.stage.getX();
            this.oldWindowY = this.stage.getY();

            // Resizing window be windows fullscreen
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            this.stage.setX(bounds.getMinX());
            this.stage.setY(bounds.getMinY());
            this.stage.setWidth(bounds.getWidth());
            this.stage.setHeight(bounds.getHeight());
        } else {
            this.isMaximized = false;
            // Revert back to smaller window
            this.stage.setX(this.oldWindowX);
            this.stage.setY(this.oldWindowY);
            this.stage.setWidth(390);
            this.stage.setHeight(660);
        }
    }

    /**
     * Creates a reference to the stage
     * from MainApp.java and binds layout
     * width and height to the stage width and height.
     *
     * @param stage application window
     */
    public void setStage(Stage stage)
    {
        this.stage = stage;
        ReadOnlyDoubleProperty stageHeight = this.stage.heightProperty();
        ReadOnlyDoubleProperty stageWidth = this.stage.widthProperty();
        layout.prefHeightProperty().bind(stageHeight);
        layout.prefWidthProperty().bind(stageWidth);
        nav.prefHeightProperty().bind(stageHeight);
        friends.requestFocus();
    }

    public void doSomething() {
        System.out.println("Doing something");
    }

    public void viewFriendsList() {
        view.getChildren().clear();
        Label title = new Label();
        title.getStyleClass().add("title");
        try {
            Collection<RosterEntry> friends = connection.roster();
            title.setText("Friends " + friends.size());
            view.getChildren().add(title);
            view.getChildren().add(new Separator());
            VBox friendsList = new VBox();
            for (RosterEntry friend : friends) {
                Button friendInfo = new Button();
                friendInfo.setText(friend.getName());
                friendsList.getChildren().add(friendInfo);
            }
            view.getChildren().add(friendsList);
        } catch(Exception e) {
            title.setText("Unable to display Friends :(");
            view.getChildren().add(title);
            e.printStackTrace();
        }
    }

    public void viewChatList() {

    }

    public void viewMore() {

    }

    public void muteNotifications() {

    }

    public void viewSettings() {

    }
}