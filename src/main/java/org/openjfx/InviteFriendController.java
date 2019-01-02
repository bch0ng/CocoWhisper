package org.openjfx;

import client.ChattyXMPPConnection;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.muc.Affiliate;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.FullJid;
import org.jxmpp.jid.impl.JidCreate;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.util.*;

public class InviteFriendController {
    private HashMap<RosterEntry, HBox> invited;
    private MultiUserChat muc;
    private Stage stage;

    @FXML private VBox inviteListVBox;
    @FXML private HBox invitedFriends;

    public void initialize() {
        invited = new HashMap<>();
    }

    public void getInviteList(ChattyXMPPConnection connection, MultiUserChat muc, Stage stage) throws Exception {
        this.muc = muc;
        this.stage = stage;

        List<EntityBareJid> currentOccupants = new ArrayList<>();
        for (EntityFullJid occupant : muc.getOccupants()) {
            EntityBareJid jid =  JidCreate.entityBareFrom(occupant.getResourcepart().toString()
                    + "@" + occupant.getDomain().toString().replace("conference.", ""));
            currentOccupants.add(jid);
        }
        Collection<RosterEntry> roster = connection.roster();
        for (RosterEntry person : roster) {
            if (!currentOccupants.contains(person.getJid().asEntityBareJidIfPossible())) {
                VCard personVCard = connection.getVCard(person.getJid().asEntityBareJidIfPossible());
                Circle messageAvatar = new Circle();
                        messageAvatar.setFill(new ImagePattern(new Image(new ByteArrayInputStream(personVCard.getAvatar()))));
                        messageAvatar.setRadius(20);
                HBox personInfoContainer = new HBox(messageAvatar, new Label(personVCard.getNickName()));
                        HBox.setHgrow(personInfoContainer, Priority.ALWAYS);
                        personInfoContainer.setAlignment(Pos.CENTER_LEFT);
                        personInfoContainer.setSpacing(10);
                BorderPane checkCircle = new BorderPane();
                            checkCircle.getStyleClass().add("check-circle");
                            checkCircle.setPrefSize(20, 20);
                VBox checkCircleAligner = new VBox(checkCircle);
                        checkCircleAligner.setAlignment(Pos.CENTER);
                HBox personContainer = new HBox(personInfoContainer, checkCircleAligner);
                        personContainer.setAlignment(Pos.CENTER);
                        personContainer.getStyleClass().add("invite-container");
                        personContainer.setOnMouseClicked((MouseEvent m) -> {
                            if (!invited.containsKey(person)) {
                                Label invite = new Label(personVCard.getNickName());
                                        invite.setAlignment(Pos.CENTER);
                                Label removePill = new Label();
                                        removePill.setAlignment(Pos.CENTER);
                                        removePill.getStyleClass().add("invite-pill-remover");
                                        removePill.setOnMouseClicked((MouseEvent m2) -> {
                                            HBox invitePill = invited.remove(person);
                                            checkCircle.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), false);
                                            invitedFriends.getChildren().remove(invitePill);
                                        });
                                HBox invitePill = new HBox(invite, removePill);
                                        invitePill.setSpacing(7);
                                        invitePill.getStyleClass().add("invite-pill");
                                invited.put(person, invitePill);
                                checkCircle.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), true);
                                invitedFriends.getChildren().add(invitePill);
                            } else {
                                HBox invitePill = invited.remove(person);
                                checkCircle.pseudoClassStateChanged(PseudoClass.getPseudoClass("active"), false);
                                invitedFriends.getChildren().remove(invitePill);
                            }
                        });
                inviteListVBox.getChildren().add(personContainer);
            }
        }
    }

    public void sendInvite() {
        for (RosterEntry person : invited.keySet()) {
            try {
                muc.invite(person.getJid().asEntityBareJidIfPossible(), "Invited");
                stage.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Could not invite " + person.getJid().asEntityBareJidIfPossible());
            }
        }
    }

    public void cancelInvite() {
        stage.close();
    }
}
