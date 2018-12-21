package org.openjfx;

import client.ChattyXMPPConnection;
import javafx.animation.*;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
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
    private double xOffset = 0; // x-location of window
    private double yOffset = 0; // y-location of window

    // CSS ID's used in scene.fxml
    @FXML private BorderPane titleCont;
    @FXML private ToolBar toolbar;
    @FXML private HBox titlebar;
    @FXML private Button closeButton;
    @FXML private Button minimizeButton;
    @FXML private Button resizeButton;
    @FXML private VBox nav;
    @FXML private BorderPane layout;
    @FXML private VBox leftMainNav;
    @FXML private VBox view;
    @FXML private Button friends;
    @FXML private Button chats;
    @FXML private Button more;
    @FXML private Button mute;
    @FXML private Button settings;
    @FXML private BorderPane login;
    @FXML private StackPane testing;
    @FXML private TextField usernameInput;
    @FXML private PasswordField passwordInput;
    @FXML private Button loginButton;
    @FXML private ImageView cocoLoginLogo;
    @FXML private VBox loginInputGroup;

    public void initialize() {
        MenuBar menuBar = new MenuBar();
        menuBar.useSystemMenuBarProperty().set(true);
        this.isMaximized = false;
        layout.setVisible(false);
        titleCont.setPickOnBounds(false);
        titleCont.toFront();
        System.out.println(testing.getChildren());

        BooleanBinding loginFieldsNotEmpty = new BooleanBinding() {
            {
                super.bind(usernameInput.textProperty(),
                        passwordInput.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return (!usernameInput.getText().isEmpty()
                        && !passwordInput.getText().isEmpty());
            }
        };
        passwordInput.textProperty().addListener((obs, oldText, newText) -> {
            if (passwordInput.getText().length() > 0) {
                passwordInput.setStyle("-fx-font-size: 0.5em");
            } else {
                passwordInput.setStyle("-fx-font-size: 1em");
            }
        });
        loginButton.visibleProperty().bind(loginFieldsNotEmpty);
        usernameInput.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER){
                if (loginFieldsNotEmpty.get()) {
                    this.login();
                }
            }
        });
        passwordInput.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER){
                if (loginFieldsNotEmpty.get()) {
                    this.login();
                }
            }
        });
        toolbar.setOnMousePressed((MouseEvent event) -> {
            System.out.println("kek");
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        toolbar.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    /**
     * Changes decorations to light-gray when window is
     * not focused, and back to default colors when focused.
     */
    private void handleUnfocusedStage() {
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
        titlebar.setOnMouseEntered((MouseEvent t) -> {
            if (!this.stage.isFocused()) {
                closeButton.getStyleClass().remove("unfocused-button");
                minimizeButton.getStyleClass().remove("unfocused-button");
                resizeButton.getStyleClass().remove("unfocused-button");
            }
            closeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), true);
            minimizeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), true);
            resizeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), true);
        });
        titlebar.setOnMouseExited((MouseEvent t) -> {
            if (!this.stage.isFocused()) {
                closeButton.getStyleClass().add("unfocused-button");
                minimizeButton.getStyleClass().add("unfocused-button");
                resizeButton.getStyleClass().add("unfocused-button");
            }
            closeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), false);
            minimizeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), false);
            resizeButton.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), false);
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
        this.handleUnfocusedStage();
        ReadOnlyDoubleProperty stageHeight = this.stage.heightProperty();
        ReadOnlyDoubleProperty stageWidth = this.stage.widthProperty();
        layout.prefHeightProperty().bind(stageHeight);
        layout.prefWidthProperty().bind(stageWidth);
        nav.prefHeightProperty().bind(stageHeight);

        // Moves window when pressed and dragged around
        leftMainNav.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        leftMainNav.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    public void doSomething() {
        System.out.println("Doing something");
    }

    private void clearActiveIcons() {
        friends.getStyleClass().remove("active-icon");
        chats.getStyleClass().remove("active-icon");
        more.getStyleClass().remove("active-icon");
        mute.getStyleClass().remove("active-icon");
        settings.getStyleClass().remove("active-icon");
    }
    private void activeIcon(Button icon) {
        icon.getStyleClass().add("active-icon");
    }

    public void viewFriendsList() {
        clearActiveIcons();
        activeIcon(friends);
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
        clearActiveIcons();
        activeIcon(chats);
    }

    public void viewMore() {

    }

    public void muteNotifications() {

    }

    public void viewSettings() {

    }

    public void login() {
        RotateTransition rt = new RotateTransition(Duration.millis(1000), cocoLoginLogo);
        try {
            usernameInput.disableProperty().setValue(true);
            passwordInput.disableProperty().setValue(true);
            rt.setByAngle(360);
            rt.setCycleCount(Animation.INDEFINITE);
            rt.setInterpolator(Interpolator.LINEAR);
            rt.play();
            connection = new ChattyXMPPConnection(usernameInput.getText(), passwordInput.getText());
            rt.stop();
            friends.requestFocus();
            login.setVisible(false);
            layout.setVisible(true);
            viewFriendsList();
            System.out.println("worked!");
        } catch (Exception e) {
            rt.stop();
            cocoLoginLogo.setRotate(0);
            usernameInput.disableProperty().setValue(false);
            passwordInput.disableProperty().setValue(false);
            passwordInput.clear();
            Label errorMessage = new Label(e.getMessage());
            errorMessage.getStyleClass().add("error-message");
            loginInputGroup.getChildren().add(errorMessage);
            loginInputGroup.setStyle("-fx-effect: dropshadow(three-pass-box, #fff2f0, 30, 0, 0, 0);");
            usernameInput.textProperty().addListener((obs, oldText, newText) -> {
                if (usernameInput.getText().length() > 0) {
                    loginInputGroup.getChildren().remove(errorMessage);
                    loginInputGroup.setStyle("-fx-effect: none");
                }
            });
            passwordInput.textProperty().addListener((obs, oldText, newText) -> {
                if (passwordInput.getText().length() > 0) {
                    loginInputGroup.getChildren().remove(errorMessage);
                    loginInputGroup.setStyle("-fx-effect: none");
                }
            });
            //usernameInput.setStyle("-fx-border-style: solid solid none solid; -fx-border-width: 5; -fx-border-color: red;");
            //passwordInput.setStyle("-fx-border-style: solid solid none solid; -fx-border-width: 5; -fx-border-color: red;");
            System.out.println("oh no");
        }
    }
}