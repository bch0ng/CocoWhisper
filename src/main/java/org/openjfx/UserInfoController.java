package org.openjfx;

import client.ChattyXMPPConnection;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.ByteArrayInputStream;

public class UserInfoController {
    private VCard infoVCard;
    private ChattyXMPPConnection connection;
    private RosterEntry user;

    @FXML private VBox userInfo;
    @FXML private VBox privateChatBtn;

    public void initialize() {

    }

    public void setupInfo(VCard infoVCard, ChattyXMPPConnection connection, RosterEntry user) {
        this.infoVCard = infoVCard;
        this.connection = connection;
        this.user = user;
        Label userInfoName = new Label(infoVCard.getNickName());
        userInfoName.getStyleClass().add("title");
        Circle userInfoAvatar = new Circle();
        userInfoAvatar.setFill(new ImagePattern(new Image(new ByteArrayInputStream(infoVCard.getAvatar()))));
        userInfoAvatar.setRadius(50);
        HBox userInfoAvatarContainer = new HBox(userInfoAvatar);
        userInfoAvatarContainer.setAlignment(Pos.CENTER);
        userInfoAvatarContainer.getStyleClass().add("friend-avatar");
        userInfo.getChildren().addAll(userInfoAvatarContainer, userInfoName);
        privateChatBtn.setOnMouseReleased((MouseEvent m) -> {
            try {
                FXMLLoader chatLoader = new FXMLLoader(getClass().getResource("/org/openjfx/fxml/chatroom.fxml"));
                Parent chatRoot = chatLoader.load();
                Scene chatScene = new Scene(chatRoot, 380, 620);
                Stage chatStage = new Stage();
                ((ChatController) chatLoader.getController()).setupChat(chatStage, chatScene, infoVCard, connection, user);
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
        });


    }
}
