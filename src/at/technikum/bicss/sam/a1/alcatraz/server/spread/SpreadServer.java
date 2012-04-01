/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.server.spread;

/**
 *
 * @author Gabriel Pendl | ic10b026@technikum-wien.at
 */
import at.technikum.bicss.sam.a1.alcatraz.common.Player;
import at.technikum.bicss.sam.a1.alcatraz.common.Util;
import at.technikum.bicss.sam.a1.alcatraz.server.spread.events.ObjectChangedEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;
import at.technikum.bicss.sam.a1.alcatraz.server.spread.events.ObjectChangedListner;

public class SpreadServer implements ObjectChangedListner{
    // SpreadServer 
    // singelton instance

    private static SpreadServer _spread_instance = null;
    // spread configuration 
    private String spread_group_name;
    private String server_address;
    private Boolean i_am_master_server = false;
    private LinkedList server_list = new LinkedList();
    private String group_master_server;
    // keep track of playerlist to send it to new group members
    private PlayerList player_list;
    // connection and listner
    SpreadConnection connection = new SpreadConnection();
    MessageListener listener = new MessageListener(this);

    // get Singelton
    public static SpreadServer getInstance() {
        // create singelton if there is no instance yet
        if (_spread_instance == null) {
            _spread_instance = new SpreadServer();
        }
        return _spread_instance;
    }

    private SpreadServer() {
        Util.readProps();
        spread_group_name = Util.getGroupName();
        server_address = "localhost";
        try {
            // connect: address, port (0 = default 4803), privatename, .?., groupmessages
            connection.connect(InetAddress.getByName(server_address), 0,
                    "alcatraz", false, true);
            connection.add(listener);
            System.out.println("SERVER: setup listener..");
            SpreadGroup group = new SpreadGroup();
            group.join(connection, spread_group_name);

        } catch (SpreadException ex) {
            System.out.print(ex);
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex){
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, null, ex);
         
        }
    }

    // getters and setters for internal data structure 
    public void updatePlayerListOnSpreadUpdate(PlayerList pl){
        player_list = pl;
    }
    
    public void addMemberServer(String name) {
        server_list.add(name);
    }

    public void updateMemberServer(LinkedList sl) {
        server_list = sl;
    }

    public SpreadGroup getPrivateGroup() {
        return connection.getPrivateGroup();
    }

    void setMasterServer(String ms) {
        group_master_server = ms;
        if (ms.equalsIgnoreCase(getPrivateGroup().toString())) {
            i_am_master_server = true;
        }
    }

    public boolean isMasterServer() {
        return i_am_master_server;
    }


    // remove a server and start election if appropriate 
    void removeServer(String server) {
        // remove server from list caused by spread leave or disconnect
        server_list.remove(server);
        // the master has gone
        if (server.equalsIgnoreCase(group_master_server)) {
            // elect new master server and master messages other nodes
            // election:
            if (getPrivateGroup().toString().equalsIgnoreCase(
                    (String) server_list.getFirst())) {

                System.out.print("\nI am the master!!!!\n");
                // new master updates other server nodes
                i_am_master_server = true;
                group_master_server = getPrivateGroup().toString();

            }
        }

        if (i_am_master_server) {
            this.multicastMasterServerInformation();
            this.multicastServerList();
        }

    }

    // Spread Group Messages
    private void multicastMessage(AlcatrazMessage a_msg) {
        try {
            SpreadMessage msg = new SpreadMessage();
            msg.setObject(a_msg);
            msg.addGroup(spread_group_name);
            msg.setReliable();
            msg.setFifo();
            connection.multicast(msg);

            System.out.println("SERVER: multicast message ... " + 
                                a_msg.getHeader() + "\n" + a_msg.getBody());

        } catch (SpreadException ex) {
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, 
                                                               null, ex);
        } catch (NullPointerException ex){
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

    @Override
    public void updateObject(ObjectChangedEvent event) {
        
        for(Player p : player_list){
            System.out.print(p.getName() + "\n");
        }
        this.multicastPlayerList();
        System.out.print("updated: ");
        
        // update nodes
        
        
    }


    void setPlayerList(LinkedList<Player> ll) {
        player_list.setLinkedList(ll);
    }
}
