package org.openjfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.ByteArrayInputStream;

public class ChatController {

    private Stage stage;
    private VCard friendVCard;


    @FXML private BorderPane titleCont;
    @FXML private AnchorPane titlebar;
    @FXML private TitlebarController titlebarController;
    @FXML private VBox chatroomInfo;

    public void initialize() {
        titlebar.setPickOnBounds(false);
        titlebar.toFront();
        titleCont.setPickOnBounds(false);
        titleCont.toFront();
    }

    public void setupChat(Stage stage, VCard friendVCard) {
        this.stage = stage;
        this.friendVCard = friendVCard;
        titlebarController.setStage(this.stage);

        VBox chatInfoContainer = new VBox();
        chatInfoContainer.setAlignment(Pos.CENTER);
        chatInfoContainer.setSpacing(10);

        Circle chatroomAvatar = new Circle();
        chatroomAvatar.setFill(new ImagePattern(new Image(new ByteArrayInputStream(friendVCard.getAvatar()))));
        System.out.println("SOMETHING!");
        chatroomAvatar.setRadius(25);

        //Label chatroomName = new Label(friendVCard.getField("Name"));
        /* TESTING PURPOSES ONLY! */
        Label chatroomName = new Label(friendVCard.getNickName());
        chatroomName.getStyleClass().add("title");
        /* END OF TESTING CODE */
        chatInfoContainer.getChildren().addAll(chatroomAvatar, chatroomName);
        chatroomInfo.getChildren().add(chatInfoContainer);
    }
}
