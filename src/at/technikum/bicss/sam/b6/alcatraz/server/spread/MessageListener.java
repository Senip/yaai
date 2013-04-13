/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.server.spread;

/**
 *
 * @author
 */
import at.technikum.bicss.sam.b6.alcatraz.common.Player;
import at.technikum.bicss.sam.b6.alcatraz.common.Util;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import spread.AdvancedMessageListener;
import spread.MembershipInfo;
import spread.SpreadException;
import spread.SpreadMessage;

public class MessageListener implements AdvancedMessageListener 
{
    // Spread Server
    private SpreadServer spreadServer;
    
    // Logger    
    private static Logger l = Util.getLogger();

    /**
     * Constructor
     */
    public MessageListener(SpreadServer sp) 
    {
        super();
        spreadServer = sp;
    }

    /**
     * Overridden Method for receiving regular Messages
     *
     * @param message
     */
    @Override
    public void regularMessageReceived(SpreadMessage message) 
    {
        try 
        {
            AlcatrazMessage obj = (AlcatrazMessage) message.getObject();

            l.debug("SPREAD: got message: " + obj.getHeader());

            if (!spreadServer.isMasterServer()) 
            {
                // update Master Server
                if      (obj.getHeader() == MessageHeader.MASTER_SERVER) 
                {
                    l.debug("SPREAD: Master Server has changed: "    + obj.getBody());
                    spreadServer.setMasterServer((String) obj.getBody());
                } 
                else if (obj.getHeader() == MessageHeader.PLAYER_LIST) 
                {
                    l.debug("SPREAD: PlayerList update: "            + obj.getBody());
                    spreadServer.setPlayerList((LinkedList<Player>) obj.getBody());
                } 
                else if (obj.getHeader() == MessageHeader.SERVER_LIST) 
                {
                    l.debug("SPREAD: ServerList update: "            + obj.getBody() + "\n");
                    spreadServer.updateMemberServer((LinkedList) obj.getBody());
                } 
                else if (obj.getHeader() == MessageHeader.MASTER_SERVER_ADDRESS) 
                {
                    l.debug("SPREAD: Master server address update: " + obj.getBody() + "\n");
                    spreadServer.setMasterServerAddress((String) obj.getBody());
                }
            }
        } 
        catch (SpreadException e) 
        {
            l.fatal("Spread Exception " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage message) 
    {
        MembershipInfo msi = message.getMembershipInfo();

        l.debug("SPREAD: Membership Message recieved");
        l.debug("SPREAD: Members: " + msi.getMembers());
        if(msi.getMembers() != null)
        {
            l.debug("SPREAD: Group Members: " + msi.getMembers().length);
            l.debug("SPREAD: Group ID: "      + msi.getGroupID());
        }
        // there is a join
        if (msi.isCausedByJoin()) 
        {
            l.debug("SPREAD: Was caused by: JOIN");
            l.debug("SPREAD: Joined Member: " + msi.getJoined());

            // if this is the first server arriving in that group 
            if (msi.getMembers().length == 1) 
            {
                // first server => this is the master
                spreadServer.addMemberServer(spreadServer.getPrivateGroup().toString());
                spreadServer.setMasterServer(spreadServer.getPrivateGroup().toString());

                l.debug("SPREAD: This node is the Master Server: \n"
                        + spreadServer.getPrivateGroup().toString());
            } 
            else if (spreadServer.isMasterServer()) 
            {
                spreadServer.addMemberServer(msi.getJoined().toString());
                spreadServer.multicastServerList();
                spreadServer.multicastMasterServerInformation();
                spreadServer.multicastMasterHostAddress();
                spreadServer.multicastPlayerList();
            }
        }

        // someone leaves
        if (msi.isCausedByLeave()) 
        {
            l.debug("SPREAD: Was caused by: LEAVE");
            l.debug("SPREAD: Left Member: " + msi.getLeft());

            spreadServer.removeServer(msi.getLeft().toString());
        }

        // disconnect
        if (msi.isCausedByDisconnect()) 
        {
            l.debug("SPREAD: Was caused by: DISCONNECT");
            l.debug("SPREAD: Disconnected Member"
                    + msi.getDisconnected());
            spreadServer.removeServer(msi.getDisconnected().toString());
        }
        
        // network error
        if (msi.isCausedByNetwork()) 
        {
            l.debug("SPREAD: Was caused by: NETWORK ERROR");
            System.exit(1);
        }
    }
}
