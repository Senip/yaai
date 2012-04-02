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
import at.technikum.bicss.sam.a1.alcatraz.common.Util;
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

            Util.printDebugMessage("SPREAD", "got message: " + obj.getHeader());

            if (!spread_server.isMasterServer()) {
                // update Master Server
                if (obj.getHeader() == MessageHeader.MASTER_SERVER) {

                    Util.printDebugMessage("SPREAD", "Master Server has changed: "
                            + obj.getBody());
                    spread_server.setMasterServer((String) obj.getBody());

                } else if (obj.getHeader() == MessageHeader.PLAYER_LIST) {

                    Util.printDebugMessage("SPREAD", "PlayerList update: "
                            + obj.getBody());

                    spread_server.setPlayerList((LinkedList<Player>) obj.getBody());
                } else if (obj.getHeader() == MessageHeader.SERVER_LIST) {
                    Util.printDebugMessage("SPREAD", "ServerList update: "
                            + obj.getBody() + "\n");
                    spread_server.updateMemberServer((LinkedList) obj.getBody());
                } else if (obj.getHeader() == MessageHeader.MASTER_SERVER_ADDRESS) {
                    Util.printDebugMessage("SPREAD", "Master server address update: "
                            + obj.getBody() + "\n");
                    spread_server.setMasterServerAddress((String) obj.getBody());
                }
            }
        } catch (SpreadException spreadException) {
            System.out.print(spreadException);
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage message) {
        MembershipInfo msi = message.getMembershipInfo();

        Util.printDebugMessage("SPREAD", "Membership Message recieved");
        Util.printDebugMessage("SPREAD", "Members: " + msi.getMembers());
        Util.printDebugMessage("SPREAD", "Group Members: "
                + msi.getMembers().length);
        Util.printDebugMessage("SPREAD", "Group ID: " + msi.getGroupID());
        // there is a join
        if (msi.isCausedByJoin()) {

            Util.printDebugMessage("SPREAD", "Was caused by: JOIN");
            Util.printDebugMessage("SPREAD", "Joined Member: " + msi.getJoined());

            System.out.print("\n");

            // if this is the first server arriving in that group 
            if (msi.getMembers().length == 1) {
                // first server => this is the master
                spread_server.addMemberServer(spread_server.getPrivateGroup().toString());
                spread_server.setMasterServer(spread_server.getPrivateGroup().toString());


                Util.printDebugMessage("SPREAD", "This node is the Master Server: "
                        + spread_server.getPrivateGroup().toString());

            } else if (spread_server.isMasterServer()) {
                spread_server.addMemberServer(msi.getJoined().toString());
                spread_server.multicastServerList();
                spread_server.multicastMasterServerInformation();
                spread_server.multicastMasterHostAddress();
                spread_server.multicastPlayerList();
            }
        }

        // someone leaves
        if (msi.isCausedByLeave()) {
            Util.printDebugMessage("SPREAD", "Was caused by: LEAVE");
            Util.printDebugMessage("SPREAD", "Left Member: " + msi.getLeft());

            spread_server.removeServer(msi.getLeft().toString());


        }

        // disconnect
        if (msi.isCausedByDisconnect()) {
            Util.printDebugMessage("SPREAD", "Was caused by: DISCONNECT");
            Util.printDebugMessage("SPREAD", "Disconnected Member"
                    + msi.getDisconnected());
            spread_server.removeServer(msi.getDisconnected().toString());




        }
        // network error
        if (msi.isCausedByNetwork()) {

            Util.printDebugMessage("SPREAD", "Was caused by: NETWORK ERROR");
        }
    }
}
