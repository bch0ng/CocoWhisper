package client;

import java.io.*;
import java.lang.Error;
import java.util.Collection;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.MessageListener;
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
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import io.github.cdimascio.dotenv.Dotenv;

public class XMPPConnection  
{ 
    private AbstractXMPPConnection connection;
    private ChatManager chatManager;
    private Roster roster;
    private Dotenv dotenv;
    
    public XMPPConnection(String username, String password) throws XMPPException, SmackException, IOException, InterruptedException
    {
        // Loading in the project's environmental variables from .env file
        dotenv = Dotenv.load();
        // Setting the config options for connection to server
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(username, password)
                .setXmppDomain(dotenv.get("XMPP_DOMAIN"))
                .setHost(dotenv.get("HOST"))
                .setSecurityMode(
                        dotenv.get("SECURITY_ENABLED").equals("true")
                        ? XMPPTCPConnectionConfiguration.SecurityMode.required
                        : XMPPTCPConnectionConfiguration.SecurityMode.disabled
                ).setPort(Integer.parseInt(dotenv.get("PORT")))
                .build();

          connection = new XMPPTCPConnection(config);
          try {
              connection.connect();
          } catch(Exception e) {
              throw new Error("Could not connect to Server.");
          }
          try {
              connection.login();
          } catch(Exception e) {
              throw new Error("Incorrect Login provided.");
          }
          chatManager = ChatManager.getInstanceFor(connection);
    }
    
    public Collection<RosterEntry> roster() throws NotLoggedInException, NotConnectedException, InterruptedException 
    {
        roster = Roster.getInstanceFor(connection);
        if (!roster.isLoaded()) {
            roster.reloadAndWait();
        }
        return roster.getEntries();
    }

    public ChatManager getChatManager()
    {
        return chatManager;
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
    
    public void disconnect()
    {
        connection.disconnect();
    }
} 