package org.openjfx;

import client.ChattyXMPPConnection;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class ChatController {

    private Stage stage;
    private Chat chat;
    //private VCard friendVCard;
    private double xOffset;
    private double yOffset;
    private RosterEntry friend;
    private ChattyXMPPConnection connection;
    private DeliveryReceiptManager drManager;
    private Scene scene;
    private MultiUserChat muc;


    @FXML private BorderPane titleCont;
    @FXML private AnchorPane titlebar;
    @FXML private TitlebarController titlebarController;
    @FXML private HBox chatroomInfo;
    @FXML private ScrollPane viewChat;
    @FXML private VBox chats;
    @FXML private TextArea msgContainer;
    @FXML private Button msgSubmitBtn;
    @FXML private HBox chatBtns;

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

    public void setupChat(Stage stage, Scene scene, VCard friendVCardOLD, ChattyXMPPConnection connection, RosterEntry friend) {
        this.stage = stage;
        this.scene = scene;
        //this.friendVCard = friendVCard;
        this.friend = friend;
        this.connection = connection;
        this.drManager = connection.getDrManager();
        titlebarController.setStage(this.stage);
        viewChat.setFitToHeight(true);
        viewChat.setFitToWidth(true);
        msgContainer.requestFocus();

        HBox chatInfoContainer = new HBox();
        chatInfoContainer.setSpacing(10);

        Label chatroomName = new Label("");
        Circle chatroomAvatar = new Circle();
        try {
            VCard v = connection.getVCard(friend.getJid().asEntityBareJidIfPossible());
            //Label chatroomName = new Label(friendVCard.getField("Name"));
            /* TESTING PURPOSES ONLY! */
            chatroomName.setText(v.getNickName());
            /* END OF TESTING CODE */
            chatroomAvatar.setFill(new ImagePattern(new Image(new ByteArrayInputStream(v.getAvatar()))));
            chatroomAvatar.setRadius(25);
        } catch (Exception e) {
            System.out.println("Avatar exception");
            e.printStackTrace();
        }

        chatInfoContainer.getChildren().addAll(chatroomAvatar, chatroomName);
        chatroomInfo.getChildren().add(chatInfoContainer);
        try {
            ChatManager chatManager = connection.getChatManager();
            MamManager mamManager = connection.getMamManager();

            muc = connection.createOrJoinMuc(friend.getJid());
            Button invite = new Button("Invite");
            invite.setOnAction((ActionEvent a) -> {

            });
            Button leave = new Button("Leave");
            leave.setOnAction((ActionEvent a) -> {
                try {
                    // if participants == 2 then destroy and remove from bookmarks, else just leave and remove from bookmarks
                    muc.destroy("Left", null);
                    muc.leave();
                    stage.close();
                    System.out.println("Left!");
                } catch (Exception e) {
                    System.out.println("could not leave :(");
                    e.printStackTrace();
                }
            });
            //HBox.setHgrow(chatBtns, Priority.ALWAYS);
            chatBtns.setSpacing(10);
            chatBtns.getChildren().addAll(invite, leave);

            muc.addMessageListener((Message msg) -> {
                HBox messageContainer = new HBox();
                messageContainer.setSpacing(10);
                Text messageText = new Text(msg.getBody());
                TextFlow messageBody = new TextFlow(messageText);
                if (connection.getLoggedInUserJid().getLocalpart().toString().equals(msg.getFrom().getResourceOrEmpty().toString())) {
                    messageContainer.setAlignment(Pos.TOP_RIGHT);
                    messageText.getStyleClass().add("user-message-text");
                    messageBody.getStyleClass().add("user-message-body");
                    //messageBody.pseudoClassStateChanged(PseudoClass.getPseudoClass("pending"), true);
                    messageBody.setId(msg.getStanzaId());
                    messageContainer.getChildren().add(messageBody);
                } else {
                    messageContainer.setAlignment(Pos.TOP_LEFT);
                    Circle messageAvatar = new Circle();
                    VCard friendVCard;
                    try {
                        String friendJid = msg.getFrom().getResourceOrEmpty().toString()
                                + "@" + msg.getFrom().getDomain().toString().replaceAll("conference\\.", "");
                        friendVCard = connection.getVCard(JidCreate.entityBareFrom(friendJid));
                        messageAvatar.setFill(new ImagePattern(new Image(new ByteArrayInputStream(friendVCard.getAvatar()))));
                        messageAvatar.setRadius(20);
                        messageContainer.getChildren().add(messageAvatar);
                    } catch (Exception e) {
                        System.out.println("Avatar exception");
                        e.printStackTrace();
                    }
                    VBox nameAndMessage = new VBox();
                    nameAndMessage.setSpacing(5);
                    Label name = new Label(msg.getFrom().getResourceOrEmpty().toString());
                    name.getStyleClass().add("friend-message-name");
                    messageText.getStyleClass().add("friend-message-text");
                    messageBody.getStyleClass().add("friend-message-body");
                    nameAndMessage.getChildren().addAll(name, messageBody);
                    messageContainer.getChildren().add(nameAndMessage);
                }
                Platform.runLater(() -> chats.getChildren().add(messageContainer));
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        drManager.addReceiptReceivedListener((Jid fromJid, Jid toJid, String receiptId, Stanza receipt) -> {
            /*
            System.out.println(receiptId);
            TextFlow msg = (TextFlow) scene.lookup("#" + receiptId);
            msg.pseudoClassStateChanged(PseudoClass.getPseudoClass("pending"), false);
            System.out.println("Delivered!");
            */
        });

        BooleanBinding messageEmpty = new BooleanBinding() {
            {
                super.bind(msgContainer.textProperty());
            }
            @Override
            protected boolean computeValue()
            {
                return msgContainer.getText().trim().isEmpty();
            }
        };
        msgSubmitBtn.disableProperty().bind(messageEmpty);
        msgContainer.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    event.consume();
                    if (event.isShiftDown()) {
                        msgContainer.appendText(System.getProperty("line.separator"));
                    } else {
                        if (!messageEmpty.get()) {
                            this.sendMsg();
                        }
                    }
                }
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
            muc.sendMessage(msg);
            msgContainer.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
