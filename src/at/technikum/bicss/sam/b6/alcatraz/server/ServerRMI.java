/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.server;

import at.technikum.bicss.sam.b6.alcatraz.common.AlcatrazClientInitGameException;
import at.technikum.bicss.sam.b6.alcatraz.common.AlcatrazClientStateException;
import at.technikum.bicss.sam.b6.alcatraz.common.AlcatrazServerException;
import at.technikum.bicss.sam.b6.alcatraz.common.IClient;
import at.technikum.bicss.sam.b6.alcatraz.common.IServer;
import at.technikum.bicss.sam.b6.alcatraz.common.Player;
import at.technikum.bicss.sam.b6.alcatraz.common.Util;
import at.technikum.bicss.sam.b6.alcatraz.server.spread.PlayerList;
import at.technikum.bicss.sam.b6.alcatraz.server.spread.SpreadServer;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 *
 * @author 
 */
public class ServerRMI extends UnicastRemoteObject implements IServer 
{

    private PlayerList    playerList;
    private SpreadServer  spreadServer;
    private static Logger l = Util.getLogger();
    private IServer       masterServer;
    private String        masterServerAddr;

    public ServerRMI(SpreadServer spreadServer) throws RemoteException 
    {
        super();
        
        // create initial spread server instance
        Util.readProps();
        
        this.spreadServer = spreadServer;            
        this.playerList   = spreadServer.getPlayerList();
        
        l.info("SERVER: Setup Connection, Master Server is " + spreadServer.getMasterServerAddress());
    }

    /**
     * Broadcast the Player List to all Clients
     * 
     * Don't call this from un-synchronized Context!
     * 
     * @return success
     */
    private boolean broadcastPlayerList() 
    {
        return broadcastPlayerList(null);
    }
    
    private boolean broadcastPlayerList(Player firstPlayer) 
    {
        playerList.renumberIDs();
        l.info("SERVER: Broadcasting Playerlist");
        
        boolean isFirstPlayer = true;
        boolean success       = true;    
        boolean retry;
        
        do
        {
            retry = false;
            
            /*
             * The Playerlist is sent to all Players
             * If a Player disconnected, he is removed from the list
             * and the list is retransmitted
             * This is done until it succeeded once, or no clients are left.
             */          
            for(Iterator<Player> i = playerList.iterator(); i.hasNext(); )
            {
                Player p;

                if(isFirstPlayer && firstPlayer != null)
                {
                    p = firstPlayer;
                }
                else
                {
                    p = i.next();                
                    if(p == firstPlayer)
                    {
                        continue;
                    }
                }

                String rmi_uri = Util.buildRMIString(p.getAddress(), p.getPort(),
                                 Util.getClientRMIPath(), p.getName());
                l.debug("SERVER: Send Playerlist to \n" + rmi_uri);

                try 
                {
                    IClient client = (IClient) Naming.lookup(rmi_uri);
                    client.updatePlayerList(playerList.getLinkedList(), false);
                }
                catch (AlcatrazClientInitGameException e)
                {
                    l.info("SERVER: Client Error: " + e.getMessage());

                    if(isFirstPlayer)
                    {
                        p.setReady(false);
                        retry   = true;
                        success = false;
                    }
                    else
                    {
                        // First Client was able to reach all Players
                        // We're now inGame
                    }
                }
                catch(AlcatrazClientStateException e)
                {
                    l.info("SERVER: Deregistered Player '" + p.getName() + "' (is allready inGame)");
                    success = false;
                }
                catch (NotBoundException | MalformedURLException | RemoteException e) 
                {
                    l.info("SERVER: Error while broadcasting playerlist:\n" + e.getMessage());
                    success = false;
                }

                if(retry)
                {
                    // Re-Send PlayerList to first Player...
                    break;
                }
                
                if(!success)    
                {              
                    l.info("SERVER: Deregistered unreachable Player: " + p.getName());      
                    i.remove();
                    break;
                }
                
                isFirstPlayer = false;
            }
            
        } while(retry); // ...until it works
        
        return success;
    }
    
    private void masterLock() throws RemoteException
    {
        if(!spreadServer.i_am_MasterServer())
        {
            RemoteException e = new RemoteException("Please contact the master!");
            l.warn(e.getMessage());
            throw e;
        }
    }

    @Override
    public synchronized LinkedList<Player> getPlayerList() throws RemoteException
    {
        masterLock();
        
        // Check if all Players are still there and send the list to the newcommer
        // while(!broadcastPlayerList());
        return playerList.getLinkedList();
    }
    
    @Override
    public synchronized Player register(String name, String address, int port) throws RemoteException, AlcatrazServerException 
    {
        masterLock();
        
        Player player = new Player(name, 0, address, port, false);
        l.info("SERVER: New player wants to register:\n" + player.toString());

        if (playerList.getPlayerByName(name) != null) 
        {
            AlcatrazServerException e =
                new AlcatrazServerException("Player with name '" + name + "' already registered.\n"
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
        
        try // Check inbound connection
        {
            Naming.lookup(player.getRmiURI());
        }
        catch(NotBoundException | MalformedURLException | RemoteException ex)
        {
            AlcatrazServerException e =
                    new AlcatrazServerException("Failed to connect (Check your firewall inbound rules)!\n"
                    + ex.getMessage());
            l.warn(e.getMessage());
            throw e;            
        }
        
        playerList.add(player);

        l.info("SERVER: Registered new Player:\n" + player.toString());
        while(!broadcastPlayerList()) { };
        
        return player;
    }

    @Override
    public synchronized void deregister(String name) throws RemoteException, AlcatrazServerException 
    {
        masterLock();
        
        l.info("SERVER: Player '" + name + "'wants to deregister");
        
        Player player = playerList.getPlayerByName(name);
        
        if (player == null) 
        {
            AlcatrazServerException e = new AlcatrazServerException("Playername '" + name + "' not found!");
            l.warn(e.getMessage());
            throw e;
        } 
        else 
        {
            playerList.remove(player);
            l.info("SERVER: Deregistered Player '" + name + "'");
            while(!broadcastPlayerList()) {};
        }
    }

    @Override
    public synchronized void setStatus(String name, boolean ready) throws RemoteException, AlcatrazServerException 
    {
        masterLock();
        
        l.info("SERVER: Player '" + name + "' wants to set readystatus to " + (ready ? "'ready'" : "'wait'"));
        Player player = playerList.getPlayerByName(name);

        if (player == null) 
        {
            AlcatrazServerException e = 
                    new AlcatrazServerException("Playername '" + name + "' not found!");
            l.warn(e.getMessage());
            throw e;
        }
        else 
        {
            player.setReady(ready);
            
            // Broadcast Player List: Start with player            
            while(!broadcastPlayerList(player)) {};
            
            if(!player.isReady())                       // Game start failed
            {
                AlcatrazServerException e = 
                        new AlcatrazServerException("Game start failed. Please try again!");
                l.warn(e.getMessage());
                throw e;
            } 
            else if (playerList.allReady() &&           // If Game started
                     playerList.count() >= Util.NUM_MIN_PLAYER) 
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
