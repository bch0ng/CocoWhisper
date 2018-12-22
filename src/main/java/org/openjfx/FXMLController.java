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
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.iqregister.AccountManager;

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
    private TextField[] loginRegisterFields;
    private boolean registering = false;

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
    @FXML private Hyperlink loginRegisterViewChanger;
    @FXML private TextField nicknameInput;
    @FXML private PasswordField passwordInputChecker;
    @FXML private VBox inputFields;
    @FXML private Label passwordHint;

    public void initialize() {
        try {
            connection = new ChattyXMPPConnection();
        } catch (Exception e) {
            e.getMessage();
        }
        MenuBar menuBar = new MenuBar();
        menuBar.useSystemMenuBarProperty().set(true);
        this.isMaximized = false;
        layout.setVisible(false);
        nicknameInput.setVisible(false);
        nicknameInput.setManaged(false);
        passwordInputChecker.setVisible(false);
        passwordInputChecker.setManaged(false);
        passwordHint.setVisible(false);
        passwordHint.setManaged(false);
        passwordHint.setVisible(false);
        passwordHint.setManaged(false);
        //login();
        titleCont.setPickOnBounds(false);
        titleCont.toFront();

        BooleanBinding loginFieldsNotEmpty = new BooleanBinding() {
            {
                super.bind(usernameInput.textProperty(),
                        passwordInput.textProperty(),
                        passwordInputChecker.managedProperty(),
                        passwordInputChecker.visibleProperty(),
                        passwordInputChecker.textProperty());
            }
            @Override
            protected boolean computeValue()
            {
                if (passwordInputChecker.isManaged() && passwordInputChecker.isVisible()) {
                    return  (!usernameInput.getText().isBlank()
                            && !passwordInput.getText().isBlank()
                            && !passwordInputChecker.getText().isBlank());
                } else {
                    return (!usernameInput.getText().isBlank()
                            && !passwordInput.getText().isBlank());
                }
            }
        };

        passwordInput.textProperty().addListener((obs, oldText, newText) -> {
            if (passwordInput.getText().length() > 0) {
                passwordInput.setStyle("-fx-font-size: 0.5em");
            } else {
                passwordInput.setStyle("-fx-font-size: 1em");
            }
        });
        passwordInputChecker.textProperty().addListener((obs, oldText, newText) -> {
            if (passwordInputChecker.getText().length() > 0) {
                passwordInputChecker.setStyle("-fx-font-size: 0.5em");
            } else {
                passwordInputChecker.setStyle("-fx-font-size: 1em");
            }
        });
        loginRegisterFields = new TextField[4];
        loginRegisterFields[0] = usernameInput;
        loginRegisterFields[1] = nicknameInput;
        loginRegisterFields[2] = passwordInput;
        loginRegisterFields[3] = passwordInputChecker;

        loginButton.visibleProperty().bind(loginFieldsNotEmpty);
        for (TextField field : loginRegisterFields) {
            field.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    if (loginFieldsNotEmpty.get()) {
                        if (registering) {
                            this.register();
                        } else {
                            this.login();
                        }
                    }
                }
            });
        }
        toolbar.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        toolbar.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    public void viewRegister() {
        registering = true;
        passwordInput.clear();
        final PseudoClass registerPseudoClass = PseudoClass.getPseudoClass("register");
        nicknameInput.setVisible(true);
        nicknameInput.setManaged(true);
        passwordInputChecker.setVisible(true);
        passwordInputChecker.setManaged(true);
        passwordInput.focusedProperty().addListener((obs, oldVal, isFocused) -> {
                    if (registering && isFocused) {
                        passwordHint.setVisible(true);
                        passwordHint.setManaged(true);
                    } else {
                        passwordHint.setVisible(false);
                        passwordHint.setManaged(false);
                    }
                });
        usernameInput.pseudoClassStateChanged(registerPseudoClass, true);
        passwordInput.pseudoClassStateChanged(registerPseudoClass, true);
        loginInputGroup.pseudoClassStateChanged(registerPseudoClass, true);
        loginButton.setText("Create an Account!");
        loginButton.setOnAction((ActionEvent e) -> this.register());
        loginRegisterViewChanger.setText("Login");
        loginRegisterViewChanger.setVisited(false);
        loginRegisterViewChanger.setOnAction((ActionEvent e) -> {
            registering = false;
            nicknameInput.setVisible(false);
            nicknameInput.setManaged(false);
            passwordInputChecker.setVisible(false);
            passwordInputChecker.setManaged(false);
            passwordHint.setVisible(false);
            passwordHint.setManaged(false);
            passwordInput.pseudoClassStateChanged(registerPseudoClass, false);
            usernameInput.pseudoClassStateChanged(registerPseudoClass, false);
            loginInputGroup.pseudoClassStateChanged(registerPseudoClass, false);
            passwordInput.clear();
            passwordInputChecker.clear();
            loginButton.setOnAction((ActionEvent event) -> login());
            loginButton.setText("Log In");
            loginRegisterViewChanger.setOnAction((ActionEvent event) -> viewRegister());
            loginRegisterViewChanger.setText("Create an Account");
            loginRegisterViewChanger.setVisited(false);
        });
    }

    private void register() {
        try {
            /** Courtesy of Tomalak (https://stackoverflow.com/questions/3802192/regexp-java-for-password-validation)
                ^                 # start-of-string
                (?=.*[0-9])       # a digit must occur at least once
                (?=.*[a-z])       # a lower case letter must occur at least once
                (?=.*[A-Z])       # an upper case letter must occur at least once
                (?=.*[@#$%^&+=])  # a special character must occur at least once
                (?=\S+$)          # no whitespace allowed in the entire string
                .{8,}             # anything, at least eight places though
                $                 # end-of-string

                Regex used here is courtesy of Eric Miller (http://regexlib.com/UserPatterns.aspx?authorId=5d8befe8-259d-41b3-8691-020504cbb97e)
             */
            if (!passwordInput.getText().equals(passwordInputChecker.getText())) {
                throw new Exception("Passwords were not the same.");
            } else if (!passwordInput.getText().matches("^(?=.*[0-9]+.*)(?=.*[a-zA-Z]+.*)[0-9a-zA-Z]{6,}$")) {
                throw new Exception("Password was not strong enough.");
            }
            connection.register(usernameInput.getText(), passwordInput.getText(), nicknameInput.getText());
            registering = false;
            viewLayout();
        } catch(Exception ex) {
            errorMessage(ex.getMessage());
        }
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
            view.getChildren().addAll(title, new Separator());
            VBox friendsList = new VBox();
            Label myProfileTitle = new Label("My Profile");
            myProfileTitle.getStyleClass().add("sub-title");
            friendsList.getChildren().addAll(myProfileTitle, new Separator());
            ToggleGroup friendsListGroup = new ToggleGroup();
            RadioButton loggedInUserInfo = new RadioButton(
                    connection.getUserVCard().getField("Name"));
            loggedInUserInfo.getStyleClass().add("friendBtn");
            loggedInUserInfo.getStyleClass().remove("radio-button");
            loggedInUserInfo.getStyleClass().add("toggle-button");
            VBox.setVgrow(loggedInUserInfo, Priority.ALWAYS);
            loggedInUserInfo.setMaxWidth(Double.MAX_VALUE);
            loggedInUserInfo.setToggleGroup(friendsListGroup);
            friendsList.getChildren().add(loggedInUserInfo);
            for (RosterEntry friend : friends) {
                //Button friendInfo = new Button();
                RadioButton friendInfo = new RadioButton();
                friendInfo.getStyleClass().add("friendBtn");
                friendInfo.getStyleClass().remove("radio-button");
                friendInfo.getStyleClass().add("toggle-button");
                VBox.setVgrow(friendInfo, Priority.ALWAYS);
                friendInfo.setMaxWidth(Double.MAX_VALUE);
                friendInfo.setText(friend.getName());
                friendInfo.setToggleGroup(friendsListGroup);
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
        usernameInput.disableProperty().setValue(true);
        passwordInput.disableProperty().setValue(true);
        rt.setByAngle(360);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.play();
        try {
            connection.login(usernameInput.getText(), passwordInput.getText());
            rt.stop();
            viewLayout();
        } catch (Exception e) {
            e.printStackTrace();
            rt.stop();
            cocoLoginLogo.setRotate(0);
            usernameInput.disableProperty().setValue(false);
            passwordInput.disableProperty().setValue(false);
        }
    }

    private void errorMessage(String message) {
        passwordInput.clear();
        passwordInputChecker.clear();
        Label errorMessage = new Label(message);
        errorMessage.getStyleClass().add("error-message");
        inputFields.getChildren().add(errorMessage);
        inputFields.setStyle("-fx-effect: dropshadow(three-pass-box, #fff2f0, 30, 0, 0, 0);");
        for (TextField field : loginRegisterFields) {
            field.textProperty().addListener((obs, oldText, newText) -> {
                if (usernameInput.getText().length() > 0) {
                    inputFields.getChildren().remove(errorMessage);
                    inputFields.setStyle("-fx-effect: none");
                }
            });
        }
    }

    public void viewLayout() {
        friends.requestFocus();
        login.setVisible(false);
        layout.setVisible(true);
        viewFriendsList();

    }
}