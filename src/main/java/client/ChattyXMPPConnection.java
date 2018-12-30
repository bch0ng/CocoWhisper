package client;

import java.io.*;
import java.lang.Error;
import java.net.URL;
import java.util.*;

import jdk.internal.loader.Resource;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.packet.MUCUser;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.jxmpp.jid.*;
import org.jxmpp.jid.impl.JidCreate;
import io.github.cdimascio.dotenv.Dotenv;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;

public class ChattyXMPPConnection
{ 
    private AbstractXMPPConnection connection;
    private ChatManager chatManager;
    private AccountManager accManager;
    private VCardManager vcManager;
    private MamManager mamManager;
    private Roster roster;
    private VCard userVCard;
    private Dotenv dotenv;
    private DeliveryReceiptManager drManager;
    private MultiUserChatManager mucManager;
    private MultiUserChat muc;
    private EntityBareJid userJid;
    private String userName;

    public ChattyXMPPConnection(String username, String password) throws Exception
    {
        this.connect();
        this.login(username, password);
    }

    public ChattyXMPPConnection() throws Exception
    {
        this.connect();
    }

    public void connect() throws Exception
    {
        // Loading in the project's environmental variables from .env file
        dotenv = Dotenv.load();
        // Setting the config options for connection to server
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setXmppDomain(dotenv.get("XMPP_DOMAIN"))
                .setHost(dotenv.get("HOST"))
                .setSecurityMode(
                        dotenv.get("SECURITY_ENABLED").equals("true")
                                ? XMPPTCPConnectionConfiguration.SecurityMode.required
                                : XMPPTCPConnectionConfiguration.SecurityMode.disabled
                ).setPort(Integer.parseInt(dotenv.get("PORT")))
                .build();
        connection = new XMPPTCPConnection(config);
        chatManager = ChatManager.getInstanceFor(connection);
        accManager = AccountManager.getInstance(connection);
        accManager.sensitiveOperationOverInsecureConnection(!dotenv.get("SECURITY_ENABLED").equals("true"));
        vcManager = VCardManager.getInstanceFor(connection);
        mamManager = MamManager.getInstanceFor(connection);
        drManager = DeliveryReceiptManager.getInstanceFor(connection);
        drManager.autoAddDeliveryReceiptRequests();
        mucManager = MultiUserChatManager.getInstanceFor(connection);
        mucManager.addInvitationListener(
                (XMPPConnection conn, MultiUserChat muc, EntityJid inviter, String reason,
                        String password, Message message, MUCUser.Invite invite) ->
                {
                    System.out.println("INVITED!");
                    try {
                        muc.join(Resourcepart.from(userName), password);
                        System.out.println("JOINED!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
        try {
            connection.connect();
        } catch(Exception e) {
            throw new Exception("Could not connect to Server.");
        }
    }

    public void register(String username, String password, String name) throws Exception
    {
        if (!username.matches("^[a-zA-Z0-9_]*$")) {
            throw new Exception("Only letters, numbers, and underscores (_) are allowed.");
        }
        try {
            // Map<String, String> attributes = new HashMap<>();
            // attributes.put("Name", name);
            // Put attributes as 3rd parameter (after password)
            accManager.createAccount(Localpart.from(username), password);
            try {
                connection.login(username, password);
                userVCard = vcManager.loadVCard(userJid);
                if (name == null) {
                    userVCard.setField("Name", username);
                } else {
                    System.out.println(name);
                    userVCard.setField("Name", name);
                }
                userVCard.setAvatar(getClass().getResource("/org/openjfx/images/custom_images/default_friend_avatar_circle.png"));
                vcManager.saveVCard(userVCard);
                //mamManager.enableMamForAllMessages();
            } catch(Exception e) {
                e.printStackTrace();
                throw new Exception("Incorrect Login provided.");
            }
        } catch(XMPPException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    public void login(String username, String password) throws Exception {
        try {
            connection.login(username, password);
            userJid = connection.getUser().asEntityBareJid();
            userVCard = vcManager.loadVCard(userJid);
            userName = userJid.getLocalpart().toString();
        } catch(Exception e) {
            e.printStackTrace();
            throw new Exception("Incorrect Login provided.");
        }
    }

    public VCard getVCard(EntityBareJid user) throws Exception {
        try {
            return vcManager.loadVCard(user);
        } catch (Exception e) {
            throw new Exception("lol");
        }
    }

    public VCard getUserVCard() throws Exception
    {
        return userVCard;
    }
    
    public Collection<RosterEntry> roster() throws NotLoggedInException, NotConnectedException, InterruptedException 
    {
        roster = Roster.getInstanceFor(connection);
        //PrivateDataManager pdManager = PrivateDataManager.getInstanceFor(connection);
        if (!roster.isLoaded()) {
            roster.reloadAndWait();
        }
        return roster.getEntries();
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public MamManager getMamManager() {
        return mamManager;
    }

    public DeliveryReceiptManager getDrManager() { return drManager; }

    public MultiUserChatManager getMucManager() { return mucManager; }

    public MultiUserChat createOrJoinMuc(BareJid friend) throws Exception {
        List<BareJid> participants = new ArrayList<>();
        participants.add(friend);
        return createOrJoinMuc(participants);
    }

    public MultiUserChat createOrJoinMuc(List<BareJid> participants) throws Exception {
        List<String> owners = new ArrayList<>();
                     owners.add(userJid.toString());

        // Adds self to participants list and then sorts alphabetically
        participants.add(userJid.asBareJid());
        participants.sort(Comparator.comparing(BareJid::toString));
        String mucName = participants.remove(0).toString().replaceAll("@", "!").replaceAll("\\.", "-"); // Fencepost approach
        for(BareJid friend : participants) {
            owners.add(friend.toString());
            mucName += ("+" + friend.toString().replaceAll("@", "!").replaceAll("\\.", "-"));
        }
        EntityBareJid chatJid = JidCreate.entityBareFrom(mucName + "@conference." + dotenv.get("XMPP_DOMAIN"));
        muc = mucManager.getMultiUserChat(chatJid);
        try {
            muc.create(Resourcepart.from(userName));
            System.out.println("CREATED");
            Form form = muc.getConfigurationForm();
            Form answerForm = form.createAnswerForm();
                 answerForm.setAnswer("muc#roomconfig_roomowners", owners);
                 answerForm.setAnswer("muc#roomconfig_persistentroom", true);
                 answerForm.setAnswer("muc#roomconfig_allowinvites", true);
                 answerForm.setAnswer("muc#roomconfig_membersonly", true);
                 answerForm.setAnswer("muc#roomconfig_publicroom", false);
                 answerForm.setAnswer("muc#roomconfig_passwordprotectedroom", true);
                 answerForm.setAnswer("muc#roomconfig_roomsecret", Integer.toString(muc.hashCode()));
            muc.sendConfigurationForm(answerForm);
            //muc.join(Resourcepart.from(userName), Integer.toString(muc.hashCode()));
            for (BareJid friend : participants) {
                muc.invite(friend.asEntityBareJidIfPossible(), "Invited");
            }
        } catch (Exception e) {
            try {
                muc.join(Resourcepart.from(userName), Integer.toString(muc.hashCode()));
                System.out.println("JOINED");
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }
        return muc;
    }

    public Chat createChat(BareJid friend) throws Exception
    {
        /*
        chatManager.addIncomingListener(new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                String jidString = from.toString();
                System.out.println(jidString.substring(0, jidString.indexOf("@"))+ ": " + message.getBody());
            }
        });
        */
        EntityBareJid jid = JidCreate.entityBareFrom(friend);
        System.out.println(jid);
        if (!roster.contains(jid)) {
            throw new Exception("User not found!");
        }
        Chat chat = chatManager.chatWith(jid);
        System.out.println("Chat Created!");
        return chat;
    }

    public AccountManager getAccountManager() {
        return accManager;
    }

    public EntityBareJid getLoggedInUserJid() {
        return userJid;
    }
    public String getLoggedInUserName() throws Exception {
        //return accManager.getAccountAttribute("name");
        return userName;
    }

    public void disconnect()
    {
        connection.disconnect();
    }
} 