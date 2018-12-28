package org.openjfx;

import client.ChattyXMPPConnection;
import javafx.animation.*;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.Iterator;

public class FXMLController {
    // Size in pixels (px) for titlebar icons.

    private static final int TRAFFIC_LIGHT_ICONS_SIZE = 12;

    // Decoration image paths
    private static final String UNFOCUSED_DECO_PATH = "images/titlebar_icons/medium/unfocused.png";

    private Stage stage;            // Stage given by MainApp.java
    private ChattyXMPPConnection connection;
    private double xOffset = 0; // x-location of window
    private double yOffset = 0; // y-location of window
    private TextField[] loginRegisterFields;
    private boolean registering = false;
    private Label errorMessage;
    private HBox currentlySelectedFriend;

    // CSS ID's used in scene.fxml
    @FXML private BorderPane titleCont;
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
    @FXML private HBox titleContainer;

    @FXML private BorderPane userInfoContainer;
    @FXML private Label userInfoName;

    @FXML private TitlebarController titlebarController;
    @FXML private AnchorPane titlebar;

    public void initialize() {
        try {
            connection = new ChattyXMPPConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MenuBar menuBar = new MenuBar();
        menuBar.useSystemMenuBarProperty().set(true);
        //layout.setVisible(false);
        login.setVisible(false);
        login();
        nicknameInput.setVisible(false);
        nicknameInput.setManaged(false);
        passwordInputChecker.setVisible(false);
        passwordInputChecker.setManaged(false);
        passwordHint.setVisible(false);
        passwordHint.setManaged(false);
        passwordHint.setVisible(false);
        passwordHint.setManaged(false);
        //login();
        titlebar.setPickOnBounds(false);
        titlebar.toFront();
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
                    return  (!usernameInput.getText().trim().isEmpty()
                            && !passwordInput.getText().trim().isEmpty()
                            && !passwordInputChecker.getText().trim().isEmpty());
                } else {
                    return (!usernameInput.getText().trim().isEmpty()
                            && !passwordInput.getText().trim().isEmpty());
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
        /*
        toolbar.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        toolbar.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        */
    }

    public void viewRegister() {
        registering = true;
        clearErrorMessage();
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
            clearErrorMessage();
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
     * Creates a reference to the stage
     * from MainApp.java and binds layout
     * width and height to the stage width and height.
     *
     * @param stage application window
     */
    public void setStage(Stage stage)
    {
        this.stage = stage;
        titlebarController.setStage(stage);
        //this.handleUnfocusedStage();
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
        titleContainer.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        titleContainer.setOnMouseDragged((MouseEvent event) -> {
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
        Label friendCount = new Label();
        title.getStyleClass().add("title");
        try {
            Collection<RosterEntry> friends = connection.roster();
            title.setText("Friends");
            friendCount.setText(Integer.toString(friends.size()));
            friendCount.getStyleClass().add("friend-count-title");
            titleContainer.getChildren().removeAll();
            titleContainer.getChildren().addAll(title, friendCount);
            titleContainer.setAlignment(Pos.CENTER_LEFT);
            view.getChildren().addAll(titleContainer, new Separator());
            VBox friendsList = new VBox();
            Label myProfileTitle = new Label("My Profile");
            myProfileTitle.getStyleClass().add("sub-title");
            friendsList.getChildren().addAll(myProfileTitle, new Separator());
            Iterator iter = friends.iterator();
            boolean isCurrentUser = true;   // Current user will always be first "friend" printed
            while (isCurrentUser || iter.hasNext()) {
                //VBox friendInfoContainer = new VBox();
                HBox friendInfo = new HBox();
                friendInfo.setAlignment(Pos.CENTER_LEFT);
                friendInfo.setSpacing(10);
                friendInfo.getStyleClass().add("friend-container");
                Label friendName = new Label();
                friendName.getStyleClass().add("friend-name");
                VCard friendVCard;
                RosterEntry friend;
                if (isCurrentUser) {
                    // For adding current user's information
                    friend = null;
                    friendVCard = connection.getUserVCard();
                } else {
                    friend = (RosterEntry) iter.next();
                    friendVCard = connection.getVCard(friend.getJid().asEntityBareJidIfPossible());
                }
                //friendName.setText(friendVCard.getField("Name"));
                /* TESTING PURPOSES ONLY! */
                friendName.setText(friendVCard.getNickName());
                /* END OF TESTING CODE */
                Circle friendAvatar = new Circle();
                friendAvatar.setFill(new ImagePattern(new Image(new ByteArrayInputStream(friendVCard.getAvatar()))));
                System.out.println("SOMETHING!");
                friendAvatar.setRadius(25);
                BorderPane friendAvatarContainer = new BorderPane(friendAvatar);
                friendAvatarContainer.getStyleClass().add("friend-avatar");
                friendAvatarContainer.setOnMousePressed((MouseEvent m) -> {
                    try {
                        FXMLLoader userInfoLoader = new FXMLLoader(getClass().getResource("/org/openjfx/fxml/user_info.fxml"));
                        Parent userInfoRoot = userInfoLoader.load();
                        Scene userInfoScene = new Scene(userInfoRoot, 390, 660);
                        Stage userInfoStage = new Stage();
                        userInfoStage.setScene(userInfoScene);
                        userInfoStage.show();
                    } catch (Exception e) {
                        System.out.println("HELLO!");
                        e.printStackTrace();
                    }
                    //viewChatRoom();
                });
                friendInfo.getChildren().addAll(friendAvatarContainer, friendName);
                //friendInfoContainer.getChildren().add(friendInfo);
                friendInfo.setOnMouseClicked((MouseEvent m) -> {
                    if(m.getButton().equals(MouseButton.PRIMARY)){
                        if (m.getClickCount() == 1) {
                            if (currentlySelectedFriend != null) {
                                currentlySelectedFriend.pseudoClassStateChanged(PseudoClass.getPseudoClass("friend-selected"), false);
                            }
                            currentlySelectedFriend = friendInfo;
                            friendInfo.pseudoClassStateChanged(PseudoClass.getPseudoClass("friend-selected"), true);
                        } else if (m.getClickCount() == 2) {
                            try {
                                FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/org/openjfx/fxml/chatroom.fxml"));
                                Parent chatRoot = chatLoader.load();
                                Scene chatScene = new Scene(chatRoot, 380, 620);
                                Stage chatStage = new Stage();
                                ((ChatController) chatLoader.getController()).setupChat(chatStage, chatScene, friendVCard, connection, friend);
                                chatScene.getStylesheets().add(getClass().getResource("/org/openjfx/css/titlebar.css").toExternalForm());
                                chatScene.getStylesheets().add(getClass().getResource("/org/openjfx/css/chatroom.css").toExternalForm());
                                chatScene.setFill(Color.TRANSPARENT);
                                chatStage.initStyle(StageStyle.TRANSPARENT);
                                chatStage.setScene(chatScene);
                                chatStage.show();
                            } catch (Exception e) {
                                System.out.println("HI");
                                e.printStackTrace();
                            }
                        }
                    }
                });
                friendsList.getChildren().add(friendInfo);
                // Creates the Friends label and subtitle only once (after user's profile)
                if (isCurrentUser) {
                    Label friendsSubTitle = new Label("Friends");
                    friendsSubTitle.getStyleClass().add("sub-title");
                    friendsList.getChildren().addAll(friendsSubTitle, new Separator());
                    isCurrentUser = false;
                }
            }
            view.getChildren().add(friendsList);
        } catch(Exception e) {
            title.setText("Unable to display Friends :(");
            view.getChildren().add(title);
            e.printStackTrace();
        }
    }

    public void viewChatRoom() {

    }

    public void viewChatList() {
        clearActiveIcons();
        activeIcon(chats);
        view.getChildren().clear();
        Label title = new Label();
        Label friendCount = new Label();
        title.getStyleClass().add("title");
        title.setText("Chats");
        HBox titleContainer = new HBox(title);
        titleContainer.setAlignment(Pos.CENTER_LEFT);
        titleContainer.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        titleContainer.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        view.getChildren().addAll(titleContainer, new Separator());
        try {
            MamManager mamManager = connection.getMamManager();
        } catch (Exception e) {

        }
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
            //connection.login(usernameInput.getText(), passwordInput.getText());
            connection.login("test1", "lololol000");
            rt.stop();
            viewLayout();
        } catch (Exception e) {
            e.printStackTrace();
            //errorMessage(e.getMessage());
            rt.stop();
            cocoLoginLogo.setRotate(0);
            usernameInput.disableProperty().setValue(false);
            passwordInput.disableProperty().setValue(false);
        }
    }

    private void errorMessage(String message) {
        passwordInput.clear();
        passwordInputChecker.clear();
        errorMessage = new Label(message);
        errorMessage.getStyleClass().add("error-message");
        inputFields.getChildren().add(errorMessage);
        inputFields.setStyle("-fx-effect: dropshadow(three-pass-box, #fff2f0, 30, 0, 0, 0);");
        for (TextField field : loginRegisterFields) {
            field.textProperty().addListener((obs, oldText, newText) -> {
                if (field.getText().length() > 0) {
                    clearErrorMessage();
                }
            });
        }
    }

    private void clearErrorMessage() {
        inputFields.getChildren().remove(errorMessage);
        inputFields.setStyle("-fx-effect: none");
    }

    public void viewLayout() {
        friends.requestFocus();
        login.setVisible(false);
        layout.setVisible(true);
        viewFriendsList();

    }


}