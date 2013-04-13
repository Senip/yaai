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
    private String     group_master_server;
    private String     group_master_server_address;
    private Boolean    i_am_master_server = false;
    private LinkedList serverList         = new LinkedList();
    
    // keep track of playerlist to send it to new group members
    private PlayerList       playerList   = new PlayerList();
    
    // connection and listner   
    private SpreadConnection connection   = new SpreadConnection();
    private MessageListener  listener     = new MessageListener(this);
    private SpreadGroup      group        = new SpreadGroup();

    // Logger    
    private static Logger l               = Util.getLogger();
        
    public SpreadServer() {
    }
       
    public SpreadServer(InetAddress address, int port, String privateName, String groupName) throws SpreadException
    { 
        open(address, port, privateName, groupName);
    }
    
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
    public SpreadConnection open(InetAddress address, int port, String privateName, String groupName) throws SpreadException
    {        
        this.groupName = groupName;
    
        // set listener
        playerList.addObjectChangedListner(this);   
        
        // Connect
        connection.connect(address, port, privateName, false, (groupName != null));
        connection.add(listener);
                
        l.debug("SPREAD: Setup listener");
        
        // Join Group
        group.join(connection, this.groupName);

        return connection;
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

    public void updateMemberServer(LinkedList sl) {
        serverList = sl;
    }
    
    public LinkedList getMemberServer() {
        return serverList;
    }
    

    public SpreadGroup getPrivateGroup() {
        return connection.getPrivateGroup();
    }

    public void setMasterServer(String ms) 
    {
        group_master_server = ms;
        
        if (ms.equalsIgnoreCase(getPrivateGroup().toString())) 
        {
            this.i_am_master_server = true;
            this.group_master_server_address = Util.getMyServerAddress();

            l.debug("SPREAD: Master Server Host Address: "
                    + this.group_master_server_address);

        }
    }

    public boolean isMasterServer() {
        return i_am_master_server;
    }

    public void setPlayerList(LinkedList<Player> ll) {
        playerList.setLinkedList(ll);
    }

    public PlayerList getPlayerList() {
        return playerList;
    }
    // remove a server and start election if appropriate 

    void removeServer(String server) 
    {
        // remove server from list caused by spread leave or disconnect
        l.debug("SPREAD: Remove server node from list (" + server + ")");
        serverList.remove(server);
        
        // the master has gone
        if (server.equalsIgnoreCase(group_master_server)) 
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

        if (i_am_master_server) 
        {
            this.multicastMasterServerInformation();
            this.multicastMasterHostAddress();
            this.multicastServerList();
        }

    }

    // Spread Group Messages
    private void multicastMessage(AlcatrazMessage a_msg) 
    {
        try 
        {
            SpreadMessage msg = new SpreadMessage();
            msg.setObject(a_msg);       // Send one Java object 
            msg.addGroup(groupName);    // Specify a group to send the message to
            msg.setReliable();          // Set the message to be reliable.
            msg.setFifo();              // Set the delivery method to FiFo
            msg.setSelfDiscard(true);   // This message should not be sent back to the user who is sending it
            connection.multicast(msg);  // Send the message!

            l.debug("SPREAD: multicast message: " + a_msg.getHeader() + "\n" + a_msg.getBody());
        } 
        catch (SpreadException e) 
        {
            l.fatal(e.getMessage(), e);
        } 
        catch (NullPointerException e) 
        {
            l.fatal(e.getMessage(), e);
        }
    }

    public void multicastPlayerList() 
    {
        // update servers nodes 
        multicastMessage(new AlcatrazMessage(MessageHeader.PLAYER_LIST,
                playerList.getLinkedList()));
    }

    public void multicastServerList() 
    {
        multicastMessage(new AlcatrazMessage(MessageHeader.SERVER_LIST,
                serverList));
    }

    public void multicastMasterServerInformation() 
    {
        multicastMessage(new AlcatrazMessage(MessageHeader.MASTER_SERVER,
                group_master_server));
    }

    public void multicastMasterHostAddress() 
    {
        multicastMessage(new AlcatrazMessage(MessageHeader.MASTER_SERVER_ADDRESS,
                group_master_server_address));
    }

    @Override
    public void updateObject(ObjectChangedEvent event) 
    {
        // the playerList was change
        // spread modified playerList to other server nodes
        l.debug("SPREAD: PlayerList was changed.");
        this.multicastPlayerList();
    }

    public void setMasterServerAddress(String msa) {
        this.group_master_server_address = msa;
    }

    public String getMasterServerAddress() {
        return this.group_master_server_address;
    }
}
