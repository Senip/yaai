/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.server.spread;

/**
 *
 * @author Gabriel Pendl
 */
import at.technikum.bicss.sam.a1.alcatraz.common.Player;
import java.util.LinkedList;
import spread.AdvancedMessageListener;
import spread.MembershipInfo;
import spread.SpreadException;
import spread.SpreadMessage;

public class MessageListener implements AdvancedMessageListener {

    SpreadServer spread_server;

    /**
     * Constructor
     */
    public MessageListener(SpreadServer sp) {
        super();
        spread_server = sp;
    }

    /**
     * Overridden Method for receiving regular Messages
     *
     * @param message
     */
    @Override
    public void regularMessageReceived(SpreadMessage message) {
        try {
            AlcatrazMessage obj = (AlcatrazMessage) message.getObject();
            System.out.print("got messege " + obj.getHeader());
            if (!spread_server.isMasterServer()) {
                // update Master Server
                if (obj.getHeader() == MessageHeader.MASTER_SERVER) {

                    System.out.print("\nMASTER SERVER UPDATE: " + obj.getBody() + "\n");
                    spread_server.setMasterServer((String) obj.getBody());
                   
                }else if(obj.getHeader() == MessageHeader.PLAYER_LIST){
                    System.out.print("\nPlayerList UPDATE: " + obj.getBody() + "\n");
                    spread_server.setPlayerList((LinkedList<Player>) obj.getBody());
                }else if(obj.getHeader() == MessageHeader.SERVER_LIST){
                    System.out.print("\nServerList UPDATE: " + obj.getBody() + "\n");
                    spread_server.updateMemberServer((LinkedList) obj.getBody());                    
                }
            }
        } catch (SpreadException spreadException) {
            System.out.print(spreadException);
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage message) {
        MembershipInfo msi = message.getMembershipInfo();

        // there is a join
        if (msi.isCausedByJoin()) {

            System.out.print("join\n");
            System.out.print("Group id: " + msi.getGroupID());
            System.out.print("\n");
            System.out.print("Members: " + msi.getMembers());
            System.out.print("\n");
            System.out.print("Group Members: " + msi.getMembers().length);
            System.out.print("\n");
            System.out.print("Joined: " + msi.getJoined());
            System.out.print("\n");
            System.out.print("My private Group: " + spread_server.getPrivateGroup());
            System.out.print("\n");
            // if this is the first server arriving in that group 
            if (msi.getMembers().length == 1) {
                // first server => this is the master
                spread_server.addMemberServer(spread_server.getPrivateGroup().toString());
                spread_server.setMasterServer(spread_server.getPrivateGroup().toString());

            } else if (spread_server.isMasterServer()) {
                spread_server.addMemberServer(msi.getJoined().toString());
                spread_server.multicastServerList();
                spread_server.multicastMasterServerInformation();
                spread_server.multicastPlayerList();
            }
        }

        // someone leaves
        if (msi.isCausedByLeave()) {
            System.out.print("leave");           
            spread_server.removeServer(msi.getLeft().toString());
            
            System.out.print("Group id: " + msi.getGroupID());
            System.out.print("\n");
            System.out.print("Members: " + msi.getMembers());
            System.out.print("\n");
            System.out.print("Joined: " + msi.getLeft());
            System.out.print("\n");
            System.out.print("My private Group: " + spread_server.getPrivateGroup());
            System.out.print("\n");
        }

        // disconnect
        if (msi.isCausedByDisconnect()) {
            System.out.print("disconnect");
            spread_server.removeServer(msi.getDisconnected().toString());
            
            System.out.print("Group id: " + msi.getGroupID());
            System.out.print("\n");
            System.out.print("Members: " + msi.getMembers());
            System.out.print("\n");
            System.out.print("Disconnected: " + msi.getDisconnected());
            System.out.print("My private Group: " + spread_server.getPrivateGroup());
            System.out.print("\n");
        }
        // network error
        if (msi.isCausedByNetwork()) {

            System.out.print("network error");
        }
    }
}
