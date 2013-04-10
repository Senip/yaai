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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;
import at.technikum.bicss.sam.b6.alcatraz.server.spread.events.ObjectChangedListner;

public class SpreadServer implements ObjectChangedListner 
{
    // SpreadServer 
    // singelton instance
    private static SpreadServer _spread_instance = null;
    
    // spread configuration 
    private String spread_group_name;
    private String server_address;
    
    // Master Server 
    private Boolean i_am_master_server = false;
    private String group_master_server;
    private String group_master_server_address;
    private LinkedList server_list     = new LinkedList();
    
    // keep track of playerlist to send it to new group members
    private PlayerList player_list     = new PlayerList();
    
    // connection and listner   
    SpreadConnection connection        = new SpreadConnection();
    MessageListener listener           = new MessageListener(this);

    // get Singelton
    public static SpreadServer getInstance() 
    {
        // create singelton if there is no instance yet

        if (_spread_instance == null) 
        {
            try 
            {
                _spread_instance = new SpreadServer();
            } 
            catch (NullPointerException ex) 
            {
                Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return _spread_instance;
    }

    private SpreadServer() 
    {
        Util.readProps();
        spread_group_name = Util.getGroupName();
        server_address    = "localhost";
    
        // set listener
        player_list.addObjectChangedListner(this);

        try 
        {
            // connect: address, port (0 = default 4803), privatename, .?., groupmessages
            connection.connect(InetAddress.getByName(server_address), 0,
                    "alcatraz", false, true);
            connection.add(listener);
            Util.handleDebugMessage("SPREAD", "Setup listener");
            SpreadGroup group = new SpreadGroup();
            group.join(connection, spread_group_name);

        } catch (SpreadException ex) 
        {
            System.out.print(ex);
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (UnknownHostException ex) 
        {
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (NullPointerException ex) 
        {
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // getters and setters for internal data structure 
    public void addMemberServer(String name) {
        server_list.add(name);
    }

    public void updateMemberServer(LinkedList sl) {
        server_list = sl;
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

            Util.handleDebugMessage("SPREAD", "Master Server Host Address: "
                    + this.group_master_server_address);

        }
    }

    public boolean isMasterServer() {
        return i_am_master_server;
    }

    public void setPlayerList(LinkedList<Player> ll) {
        player_list.setLinkedList(ll);
    }

    public PlayerList getPlayerList() {
        return player_list;
    }
    // remove a server and start election if appropriate 

    void removeServer(String server) 
    {
        // remove server from list caused by spread leave or disconnect
        Util.handleDebugMessage("SPREAD",
                "remove server node from list (" + server + ")");
        server_list.remove(server);
        
        // the master has gone
        if (server.equalsIgnoreCase(group_master_server)) 
        {
            // elect new master server and master messages other nodes
            // election:
            if (getPrivateGroup().toString().equalsIgnoreCase(
                    (String) server_list.getFirst())) 
            {

                Util.handleDebugMessage("SPREAD", "This server node is new MASTER");
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
            msg.setObject(a_msg);
            msg.addGroup(spread_group_name);
            msg.setReliable();
            msg.setFifo();
            connection.multicast(msg);

            Util.handleDebugMessage("SPREAD", "multicast message: "
                    + a_msg.getHeader() + "\n" + a_msg.getBody());

        } 
        catch (SpreadException ex) 
        {
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE,
                    null, ex);
        } 
        catch (NullPointerException ex) 
        {
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE,
                    null, ex);
        }

    }

    public void multicastPlayerList() {
        // update servers nodes 
        multicastMessage(new AlcatrazMessage(MessageHeader.PLAYER_LIST,
                player_list.getLinkedList()));

    }

    void multicastServerList() {

        multicastMessage(new AlcatrazMessage(MessageHeader.SERVER_LIST,
                server_list));
    }

    void multicastMasterServerInformation() {

        multicastMessage(new AlcatrazMessage(MessageHeader.MASTER_SERVER,
                group_master_server));
    }

    void multicastMasterHostAddress() {
        multicastMessage(new AlcatrazMessage(MessageHeader.MASTER_SERVER_ADDRESS,
                group_master_server_address));
    }

    @Override
    public void updateObject(ObjectChangedEvent event) {
        // the player_list was change
        // spread modified player_list to other server nodes
        Util.handleDebugMessage("SPREAD", "PlayerList was changed.");
        this.multicastPlayerList();

    }

    public void setMasterServerAddress(String msa) {
        this.group_master_server_address = msa;

    }

    public String getMasterServerAddress() {
        return this.group_master_server_address;
    }
}
