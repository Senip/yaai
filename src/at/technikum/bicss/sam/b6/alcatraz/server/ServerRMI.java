/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.server;

import at.technikum.bicss.sam.b6.alcatraz.common.Player;
import at.technikum.bicss.sam.b6.alcatraz.common.AlcatrazServerException;
import at.technikum.bicss.sam.b6.alcatraz.common.IClient;
import at.technikum.bicss.sam.b6.alcatraz.common.IServer;
import at.technikum.bicss.sam.b6.alcatraz.common.Util;
import at.technikum.bicss.sam.b6.alcatraz.server.spread.PlayerList;
import at.technikum.bicss.sam.b6.alcatraz.server.spread.Spread;
import at.technikum.bicss.sam.b6.alcatraz.server.spread.SpreadServer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import spread.SpreadException;

/**
 *
 * @author 
 */
public class ServerRMI extends UnicastRemoteObject implements IServer 
{

    private PlayerList    playerList;
    private SpreadServer  spreadServer;
    static private Logger l = Util.getLogger();

    public ServerRMI() throws RemoteException 
    {
        super();
        
        // create initial spread server instance
        Util.readProps();
        
        spreadServer = Spread.open();            
        playerList   = spreadServer.getPlayerList();
        
    }
    
    public void close()
    {
        spreadServer = Spread.close();
        playerList   = null; 
    }

    /**
     * Broadcast the Player List to all Clients
     * 
     * Don't call this from un-synchronized Context!
     */
    private void broadcastPlayerList() 
    {
        playerList.renumberIDs();
        l.info("SERVER: Broadcasting Playerlist");
            
        boolean success;
        
        do
        {
            success = true;
            
            /*
             * The Playerlist is sent to all Players
             * If a Player disconnected, he is removed from the list
             * and the list is retransmitted
             * This is done until it succeeded once, or no clients are left.
             */
            for (Player p : playerList) 
            {
                String rmi_uri = Util.buildRMIString(p.getAddress(), p.getPort(),
                                 Util.getClientRMIPath(), p.getName());
                l.debug("SERVER: Send Playerlist to " + rmi_uri);
                
                try 
                {
                    IClient c = (IClient) Naming.lookup(rmi_uri);
                    c.updatePlayerList(playerList.getLinkedList());
                } 
                catch (Exception e) 
                {
                    l.info("SERVER: Error while broadcasting playerlist:\n" + e.getMessage());
                    l.info("SERVER: Deregistered unreachable Player: " + p.getName());
                    playerList.remove(p);
                    success = false;
                }
                
                if(!success) { break; }
            }
        }
        while(!success);
    }

    @Override
    public synchronized LinkedList<Player> getPlayerList()
    {        
        // Check if all Players are still there and send the list to the newcommer
        broadcastPlayerList();
        return playerList.getLinkedList();
    }
    
    @Override
    public synchronized void register(String name, String address, int port) throws RemoteException, AlcatrazServerException 
    {
        Player player = new Player(name, 0, address, port, false);
        l.info("SERVER: New player wants to register:\n" + player.toString());

        if (playerList.getPlayerByName(name) != null) 
        {
            AlcatrazServerException e =
                new AlcatrazServerException("Player with name " + name + " already registered.\n"
                                          + "Name must be unique, please use a different name.");
            l.warn(e.getMessage());
            throw e;
        }
        
        if (playerList.getLinkedList().size() >= Util.NUM_MAX_PLAYER) 
        {
            AlcatrazServerException e =
                    new AlcatrazServerException("This game is already full! "
                    + "(max. " + Util.NUM_MAX_PLAYER + " Players)\n" + ""
                    + "Please try some time later.");
            l.warn(e.getMessage());
            throw e;
        }

        playerList.add(player);

        l.info("SERVER: Registered new Player:\n" + player.toString());
        broadcastPlayerList();
    }

    @Override
    public synchronized void deregister(String name) throws RemoteException, AlcatrazServerException 
    {
        l.info("SERVER: Player wants to deregister" + name);
        
        Player player = playerList.getPlayerByName(name);
        
        if (player == null) 
        {
            AlcatrazServerException e = new AlcatrazServerException("Playername " + name + " not found!");
            l.warn(e.getMessage());
            throw e;
        } 
        else 
        {
            playerList.remove(player);
            l.info("SERVER: Deregistered Player " + name);
            broadcastPlayerList();
        }
    }

    @Override
    public synchronized void setStatus(String name, boolean ready) throws RemoteException, AlcatrazServerException 
    {
        l.info("Player " + name + " wants to set readystatus to " + ready);
        
        Player player = playerList.getPlayerByName(name);

        if (player == null) 
        {
            AlcatrazServerException e = 
                    new AlcatrazServerException("Playername " + name + " not found!");
            l.warn(e.getMessage());
            throw e;
        }
        else 
        {
            player.setReady(ready);
            broadcastPlayerList();
            if (playerList.allReady()) 
            {
                spreadServer.setPlayerList(new LinkedList());
                playerList = spreadServer.getPlayerList();
            }
            playerList.triggerObjectChangedEvent();
        }
    }

    @Override
    public String getMasterServer() throws RemoteException 
    {
        return spreadServer.getMasterServerAddress();
    }
}
