package org.openjfx;

import client.ChattyXMPPConnection;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jivesoftware.smack.roster.RosterEntry;
import java.util.Collection;

public class FXMLController {
    // Size in pixels (px) for titlebar icons.
    private static final int TRAFFIC_LIGHT_ICONS_SIZE = 12;

    // Decoration image paths
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
    }

    /**
     * Changes decorations to light-gray when window is
     * not focused, and back to default colors when focused.
     */
    public void handleUnfocusedStage() {
        this.stage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            this.isFocused = isNowFocused;
            if (!isNowFocused) {
                // Changing all three decorations to light gray
                closeButton.getStyleClass().add("unfocused-button");
                minimizeButton.getStyleClass().add("unfocused-button");
                resizeButton.getStyleClass().add("unfocused-button");
            } else {
                // Reverting all three decorations back to default
                closeButton.getStyleClass().remove("unfocused-button");
                minimizeButton.getStyleClass().remove("unfocused-button");
                resizeButton.getStyleClass().remove("unfocused-button");
            }
        });
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