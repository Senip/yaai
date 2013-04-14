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
import at.technikum.bicss.sam.b6.alcatraz.server.spread.events.ObjectChangedEvent;
import at.technikum.bicss.sam.b6.alcatraz.server.spread.events.ObjectChangedListner;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

public class SpreadServer implements ObjectChangedListner 
{
    // Constans
    private final static InetAddress DEFAULT_ADDR = null;
    private final static int         DEFAULT_PORT = 0;
    
    // spread configuration 
    private String groupName;
    
    // Master Server 
    private String       groupMasterServer        = null;
    private String       groupMasterServerAddress = null;
    private Boolean      i_am_MasterServer        = false;
    private LinkedList   serverList               = new LinkedList();
        
    // keep track of playerlist to send it to new group members
    private PlayerList       playerList   = new PlayerList();
    
    // connection and listner   
    private SpreadConnection connection   = new SpreadConnection();
    private MessageListener  listener     = new MessageListener(this);
    private SpreadGroup      group        = new SpreadGroup();
    
    // Synchronisation
    private final Object syncGroupMasterServerAddress = new Object();
    
    // Logger    
    private static Logger l               = Util.getLogger();
        
    /**
     * Open Spread Server Connection
     * 
     * @param address       Spread Server Address      null = default localhost
     * @param port          Spread Server Port            0 = default 4803
     * @param privateName   Name of this connection    must be unique per spread server
     * @param groupName     Name of the group          null = don't join a group
     * 
     * @return Connection
     */    
    public SpreadServer(InetAddress address, int port, String privateName, String groupName) throws SpreadException
    {        
        this.groupName = groupName;
    
        // set listener
        playerList.addObjectChangedListner(this);   
        
        // Connect
        connection.connect(address, port, privateName, false, (groupName != null));
        
        // Join Group
        group.join(connection, this.groupName);
        
        //Add Listener    
        l.debug("SPREAD: Setup listener");   
        /*
         * Spread 4.2.0     SpreadConnection.java
         * 
         * The Spread Listener (1601: private class Listener extends Thread)
         * does not provide any kind of error handling in case the connection
         * brakes. Then the error message is simply printed to System.out 
         * (1656, 1866). Thus we can't discover if we lost the connection.
         * 
         * Spread does not provide any service to check if the connection is alive.
         * All methods which performe read() are private, and all methods which are
         * public, check if a listener is attached. Anyway read() would be hard to 
         * handle.
         * 
         * Therefore the only way is to multicast and to check if write() fails. 
         * If so, we assume that the connection is dead.
         * 
         */
        connection.add(listener);
                
    }
    
    public void sync()
    {
        /*
         * The constructor attaces the Listener and returns. 
         * i.e. this server is not in sync with the other server.
         * 
         * After the master received the memebership information, it will 
         * multicast the relevant information.
         * 
         * If this is the first server it sets its own address.
         * 
         * In any case all relevant information is received, once the master
         * server address is set.
         */
        
        boolean sync;
        
        synchronized(syncGroupMasterServerAddress)
        {
            sync = (groupMasterServerAddress != null);
        
            while(!sync) 
            {
                try { syncGroupMasterServerAddress.wait(); break; } 
                catch (InterruptedException e) {  }
            };   
        }
    }
    
    public boolean isSynced()
    {
        synchronized(syncGroupMasterServerAddress)
        {
            return (groupMasterServerAddress != null);
        }
    }
    
    /**
     * Close Spread Server Connection
     */
    public void close()
    {
        try { group.leave();                               } catch(Exception e) { }
        try { connection.remove(listener);                 } catch(Exception e) { }
        try { connection.disconnect();                     } catch(Exception e) { }
        try { playerList.removeObjectChangedListner(this); } catch(Exception e) { }
    }
    
    public static InetAddress getAddr(String name)
    {
        InetAddress addr = DEFAULT_ADDR; 
        
        try 
        {
            addr = InetAddress.getByName(name); 
        } 
        catch (UnknownHostException e) 
        {
            l.warn("Can't resolve spread host address: " + name + "\n" + e.getMessage());
            l.info("Using default address...");
        }
        
        return addr;
    }
    
    public static int getPort(String port)
    {
        int intPort = DEFAULT_PORT;
                
        try
        {
            intPort = Integer.parseInt(port);
        }
        catch(NumberFormatException e)
        {
            l.warn("Can't parse spread host port: " + port + "\n" + e.getMessage());
            l.info("Using default address... (localhost)");
        }
        
        return intPort;
    }
    
    // getters and setters for internal data structure 
    public void addMemberServer(String name) {
        serverList.add(name);
    }

    public void updateMemberServer(LinkedList serverList) {
        this.serverList = serverList;
    }
    
    public LinkedList getMemberServer() {
        return serverList;
    }
    

    public SpreadGroup getPrivateGroup() {
        return connection.getPrivateGroup();
    }

    public void setMasterServer(String ms) 
    {      
        groupMasterServer = ms;
        
        if (ms.equalsIgnoreCase(getPrivateGroup().toString())) 
        {
            this.i_am_MasterServer = true;
              
            synchronized(syncGroupMasterServerAddress)
            {
                this.groupMasterServerAddress = Util.getMyServerAddress();
                syncGroupMasterServerAddress.notifyAll();
            
                l.debug("SPREAD: Master Server Host Address: " + this.groupMasterServerAddress);
            }
        }
    }
    
    public void setMasterServerAddress(String address) 
    {
        synchronized(syncGroupMasterServerAddress)
        {
            this.groupMasterServerAddress = address;  
            syncGroupMasterServerAddress.notifyAll();
        }
    }

    public String getMasterServerAddress() 
    {
        synchronized(syncGroupMasterServerAddress)
        {
            if(!isSynced())
            {
                sync();
            }
            return this.groupMasterServerAddress;
        }
    }

    public boolean i_am_MasterServer() {
        return i_am_MasterServer;
    }

    public void setPlayerList(LinkedList<Player> ll) {
        playerList.setLinkedList(ll);
    }

    public PlayerList getPlayerList() {
        return playerList;
    }
    // remove a server and start election if appropriate 

    public void removeServer(String server) 
    {
        // remove server from list caused by spread leave or disconnect
        l.debug("SPREAD: Remove server node from list (" + server + ")");
        serverList.remove(server);
        
        // the master has gone
        if (server.equalsIgnoreCase(groupMasterServer)) 
        {
            // elect new master server and master messages other nodes
            // election:
            if (getPrivateGroup().toString().equalsIgnoreCase((String) serverList.getFirst())) 
            {
                l.debug("SPREAD: This server node is new MASTER");
                // new master updates other server nodes
                setMasterServer(getPrivateGroup().toString());
            }
        }

        if (i_am_MasterServer) 
        {
            this.multicastMasterServerInformation();
            this.multicastMasterHostAddress();
            this.multicastServerList();
        }
    }

    // Spread Group Messages
    private void multicastMessage(AlcatrazMessage message) 
    {
        try 
        {
            SpreadMessage msg = new SpreadMessage();
            msg.setObject(message);     // Send one Java object 
            msg.addGroup(groupName);    // Specify a group to send the message to
            msg.setReliable();          // Set the message to be reliable.
            msg.setFifo();              // Set the delivery method to FiFo
            msg.setSelfDiscard(true);   // This message should not be sent back to the user who is sending it
            connection.multicast(msg);  // Send the message!

            l.debug("SPREAD: Multicast message: " + message.getHeader() + "\n" + message.getBody());
        } 
        catch (SpreadException e) 
        {
            // We assume that the connection is dead
            l.fatal("SPREAD: " + e.getMessage());
            System.exit(1);
        } 
    }
    
    public void multicastPlayerList() 
    {
        // update servers nodes 
        multicastMessage(new AlcatrazMessage(MessageHeader.PLAYER_LIST, playerList.getLinkedList()));
    }

    public void multicastServerList() 
    {
        multicastMessage(new AlcatrazMessage(MessageHeader.SERVER_LIST, serverList));
    }

    public void multicastMasterServerInformation() 
    {
        multicastMessage(new AlcatrazMessage(MessageHeader.MASTER_SERVER, groupMasterServer));
    }

    public void multicastMasterHostAddress() 
    {
        synchronized(syncGroupMasterServerAddress)
        {
        multicastMessage(new AlcatrazMessage(MessageHeader.MASTER_SERVER_ADDRESS, groupMasterServerAddress));
        }
    }

    @Override
    public void updateObject(ObjectChangedEvent event) 
    {
        // the playerList was change
        // spread modified playerList to other server nodes
        l.debug("SPREAD: PlayerList was changed.");
        this.multicastPlayerList();
    }
}
