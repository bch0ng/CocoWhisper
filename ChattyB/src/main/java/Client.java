import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jxmpp.jid.BareJid;


public class Client {
    public static void main(String[] args) throws XMPPException, SmackException, IOException, InterruptedException {
        Scanner in = new Scanner(System.in);
        while (true) {
            // Asking for Login Info
            System.out.println("What is your username?");
            String username = in.next();
            System.out.println("What is your password?");
            String password = in.next();
            
            // Creates XMPP connection with given login information
            XMPPConnection conn = new XMPPConnection(username, password);
            
            System.out.println("Successfully logged in!");
            
            // Creates a chat with desired user
            System.out.println("Who would you like to chat with?");
            boolean chatWithSelected = false;
            Chat chat = null;
            Collection<RosterEntry> allUsers = conn.roster();
            HashMap<String, BareJid> users = new HashMap<>();
            if (allUsers.isEmpty()) {
                System.out.println("No users.");
            } else {
                System.out.println("Users:");
                for (RosterEntry user : allUsers) {
                    users.put(user.getName(), user.getJid());
                    System.out.println(" " + user.toString());
                }
            }
            while (!chatWithSelected) {
                String chatWith = in.next();
                try {
                    chat = conn.createChat(users.get(chatWith));
                    chatWithSelected = true;
                } catch(Exception e) {
                    System.out.println("User not found! Please try again.");
                }
            }
            
            // Send chats until user logs out
            Scanner inLine = new Scanner(System.in);
            String input = "";
            while (!input.toLowerCase().equals("logout")) {
                input = inLine.nextLine();
                if (input.toLowerCase().equals("logout")) {
                    System.out.println("hi");
                    conn.disconnect();
                    System.out.println("Disconnected");
                } else {
                    chat.send(input);
                }
            }
            break;
        }
    }
}
