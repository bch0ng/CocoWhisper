package org.openjfx;

import client.ChattyXMPPConnection;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import jdk.internal.jline.internal.Log;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;

import java.io.ByteArrayInputStream;
import java.util.List;

public class ChatController {

    private Stage stage;
    private Chat chat;
    private VCard friendVCard;
    private double xOffset;
    private double yOffset;
    private RosterEntry friend;
    private ChattyXMPPConnection connection;
    private DeliveryReceiptManager drManager;
    private Scene scene;


    @FXML private BorderPane titleCont;
    @FXML private AnchorPane titlebar;
    @FXML private TitlebarController titlebarController;
    @FXML private VBox chatroomInfo;
    @FXML private ScrollPane viewChat;
    @FXML private VBox chats;
    @FXML private TextArea msgContainer;

    public void initialize() {
        titlebar.setPickOnBounds(false);
        titlebar.toFront();
        xOffset = 0;
        yOffset = 0;
        viewChat.vvalueProperty().bind(chats.heightProperty());
        chatroomInfo.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        chatroomInfo.setOnMouseDragged((MouseEvent event) -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    public void setupChat(Stage stage, Scene scene, VCard friendVCard, ChattyXMPPConnection connection, RosterEntry friend) {
        this.stage = stage;
        this.scene = scene;
        this.friendVCard = friendVCard;
        this.friend = friend;
        this.connection = connection;
        this.drManager = connection.getDrManager();
        titlebarController.setStage(this.stage);
        viewChat.setFitToHeight(true);
        viewChat.setFitToWidth(true);
        msgContainer.requestFocus();

        VBox chatInfoContainer = new VBox();
        chatInfoContainer.setAlignment(Pos.CENTER);
        chatInfoContainer.setSpacing(10);

        Circle chatroomAvatar = new Circle();
        chatroomAvatar.setFill(new ImagePattern(new Image(new ByteArrayInputStream(friendVCard.getAvatar()))));
        chatroomAvatar.setRadius(25);

        //Label chatroomName = new Label(friendVCard.getField("Name"));
        /* TESTING PURPOSES ONLY! */
        Label chatroomName = new Label(friendVCard.getNickName());
        /* END OF TESTING CODE */
        chatInfoContainer.getChildren().addAll(chatroomAvatar, chatroomName);
        chatroomInfo.getChildren().add(chatInfoContainer);
        try {
            ChatManager chatManager = connection.getChatManager();
            MamManager mamManager = connection.getMamManager();
            System.out.println("MAM: " + mamManager.isSupported());
            try {
                MamManager.MamQueryArgs mamQueryArgs = MamManager.MamQueryArgs.builder()
                        .limitResultsToJid(friend.getJid())
                        .setResultPageSizeTo(10)
                        .queryLastPage()
                        .build();
                MamManager.MamQuery mamQuery = mamManager.queryArchive(mamQueryArgs);
                List<Message> messages = mamQuery.getMessages();
                for (Message message : messages) {
                    if (message.getFrom().asEntityBareJidIfPossible().equals(friend.getJid().asEntityBareJidIfPossible())) {
                        HBox messageContainer = new HBox();
                        messageContainer.setAlignment(Pos.TOP_LEFT);
                        messageContainer.setSpacing(10);
                        Circle messageAvatar = new Circle();
                        messageAvatar.setFill(new ImagePattern(new Image(new ByteArrayInputStream(friendVCard.getAvatar()))));
                        messageAvatar.setRadius(20);
                        messageContainer.getChildren().add(messageAvatar);
                        VBox nameAndMessage = new VBox();
                        nameAndMessage.setSpacing(5);
                        Label name = new Label(message.getFrom().getLocalpartOrNull().toString());
                        name.getStyleClass().add("friend-message-name");
                        Text messageText = new Text(message.getBody());
                        messageText.getStyleClass().add("friend-message-text");
                        TextFlow messageBody = new TextFlow(messageText);
                        messageBody.getStyleClass().add("friend-message-body");
                        nameAndMessage.getChildren().addAll(name, messageBody);
                        messageContainer.getChildren().add(nameAndMessage);
                        chats.getChildren().add(messageContainer);
                    } else if (message.getFrom().asEntityBareJidIfPossible().equals(connection.getLoggedInUserJid())) {
                        HBox messageContainer = new HBox();
                        messageContainer.setAlignment(Pos.TOP_RIGHT);
                        messageContainer.setSpacing(10);
                        Text messageText = new Text(message.getBody());
                        messageText.getStyleClass().add("user-message-text");
                        TextFlow messageBody = new TextFlow(messageText);
                        messageBody.getStyleClass().add("user-message-body");
                        messageContainer.getChildren().add(messageBody);
                        chats.getChildren().add(messageContainer);
                    }
                }
            } catch (Exception e) {
                System.out.println("NOOOOOO");
                e.printStackTrace();
            }

            chatManager.addIncomingListener((EntityBareJid from, Message message, Chat chat) -> {
                HBox messageContainer = new HBox();
                messageContainer.setAlignment(Pos.TOP_LEFT);
                messageContainer.setSpacing(10);
                Circle messageAvatar = new Circle();
                messageAvatar.setFill(new ImagePattern(new Image(new ByteArrayInputStream(friendVCard.getAvatar()))));
                messageAvatar.setRadius(20);
                messageContainer.getChildren().add(messageAvatar);
                VBox nameAndMessage = new VBox();
                nameAndMessage.setSpacing(5);
                Label name = new Label(from.getLocalpart().toString());
                name.getStyleClass().add("friend-message-name");
                Text messageText = new Text(message.getBody());
                messageText.getStyleClass().add("friend-message-text");
                TextFlow messageBody = new TextFlow(messageText);
                messageBody.getStyleClass().add("friend-message-body");
                nameAndMessage.getChildren().addAll(name, messageBody);
                messageContainer.getChildren().add(nameAndMessage);
                Platform.runLater(() -> chats.getChildren().add(messageContainer));
            });
            chat = connection.createChat(friend.getJid());
        } catch (Exception e) {
            e.printStackTrace();
        }
        drManager.addReceiptReceivedListener((Jid fromJid, Jid toJid, String receiptId, Stanza receipt) -> {
            System.out.println(receiptId);
            TextFlow msg = (TextFlow) scene.lookup("#" + receiptId);
            msg.pseudoClassStateChanged(PseudoClass.getPseudoClass("pending"), false);
            System.out.println("Delivered!");
        });
    }

    public void sendMsg() {
        try {
            String msgBody = msgContainer.getText();
            Message msg = new Message(friend.getJid());
            String msgID = Integer.toString(msg.hashCode());
            System.out.println(msgID);
            msg.setStanzaId(msgID);
            msg.setFrom(connection.getLoggedInUserJid());
            msg.setBody(msgBody);
            msg.setType(Message.Type.chat);
            chat.send(msg);
            msgContainer.clear();

            HBox messageContainer = new HBox();
            messageContainer.setAlignment(Pos.TOP_RIGHT);
            messageContainer.setSpacing(10);
            System.out.println(msgBody);
            Text messageText = new Text(msgBody);
            messageText.getStyleClass().add("user-message-text");
            TextFlow messageBody = new TextFlow(messageText);
            messageBody.getStyleClass().add("user-message-body");
            messageBody.pseudoClassStateChanged(PseudoClass.getPseudoClass("pending"), true);
            messageBody.setId(msg.getStanzaId());
            messageContainer.getChildren().add(messageBody);
            chats.getChildren().add(messageContainer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
