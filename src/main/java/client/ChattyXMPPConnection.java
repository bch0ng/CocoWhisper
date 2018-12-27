package client;

import java.io.*;
import java.lang.Error;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.mam.MamManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import io.github.cdimascio.dotenv.Dotenv;
import org.jxmpp.jid.parts.Localpart;
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
                userVCard = vcManager.loadVCard(connection.getUser().asEntityBareJid());
                if (name == null) {
                    userVCard.setField("Name", username);
                } else {
                    System.out.println(name);
                    userVCard.setField("Name", name);
                }
                userVCard.setAvatar(getClass().getResource("/org/openjfx/images/custom_images/default_friend_avatar_circle.png"));
                vcManager.saveVCard(userVCard);
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
            userVCard = vcManager.loadVCard(connection.getUser().asEntityBareJid());
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

    public Chat createChat(BareJid chatWith) throws Exception
    {
        chatManager.addIncomingListener(new IncomingChatMessageListener() {
            @Override
            public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                String jidString = from.toString();
                System.out.println(jidString.substring(0, jidString.indexOf("@"))+ ": " + message.getBody());
            }
        });
        EntityBareJid jid = JidCreate.entityBareFrom(chatWith);
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

    public String getLoggedInUserName() throws Exception {
        return accManager.getAccountAttribute("name");
    }

    public void disconnect()
    {
        connection.disconnect();
    }
} 