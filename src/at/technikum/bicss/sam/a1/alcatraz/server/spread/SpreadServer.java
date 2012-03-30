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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import spread.SpreadConnection;
import spread.SpreadException;
import spread.SpreadGroup;
import spread.SpreadMessage;

public class SpreadServer {
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
    private LinkedList<Player> player_list;
    // connection and listner
    SpreadConnection connection = new SpreadConnection();
    MessageListener listener = new MessageListener(this);

    // get Singelton
    public static SpreadServer getInstance() {
        // create singelton if there is no instance
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
            connection.connect(InetAddress.getByName(server_address), 0, "alcatraz",
                    false, true);
            connection.add(listener);
            System.out.println("SERVER: setup listener..");
            SpreadGroup group = new SpreadGroup();
            group.join(connection, spread_group_name);

        } catch (SpreadException ex) {
            System.out.print(ex);
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    public void multicastPlayerListToGroup(LinkedList<Player> pl) {
        // player_list
        player_list = pl;

        try {
            SpreadMessage msg = new SpreadMessage();
            msg.setObject(new AlcatrazMessage(MessageHeader.PLAYER_LIST, pl));
            msg.addGroup(spread_group_name);
            msg.setReliable();
            connection.multicast(msg);

            System.out.println("SERVER: multicast messagee ..");

        } catch (SpreadException ex) {
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        public void multicastPlayerList() {

        try {
            SpreadMessage msg = new SpreadMessage();
            msg.setObject(new AlcatrazMessage(MessageHeader.PLAYER_LIST, player_list));
            msg.addGroup(spread_group_name);
            msg.setReliable();
            connection.multicast(msg);

            System.out.println("SERVER: multicast messagee ..");

        } catch (SpreadException ex) {
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void multicastServerList() {
        try {
            SpreadMessage msg = new SpreadMessage();
            msg.setObject(new AlcatrazMessage(MessageHeader.SERVER_LIST, server_list));
            msg.addGroup(spread_group_name);
            msg.setReliable();
            connection.multicast(msg);

            System.out.println("SERVER: multicast message ... server list:" + server_list);
        } catch (SpreadException ex) {
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    void multicastMasterServerInformation() {
        try {
            SpreadMessage msg = new SpreadMessage();
            msg.setObject(new AlcatrazMessage(MessageHeader.MASTER_SERVER, group_master_server));
            msg.addGroup(spread_group_name);
            msg.setReliable();
            connection.multicast(msg);

            System.out.println("SERVER: multicast message ... server list:" + server_list);
        } catch (SpreadException ex) {
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public boolean isMasterServer() {
        return i_am_master_server;
    }

    void setPlayerList(LinkedList<Player> pl) {
        player_list = pl;
    }

    void removeServer(String server) {
        // remove server from list
        server_list.remove(server);
        // the master has gone
        if(server.equalsIgnoreCase(group_master_server)){
            // elect new master server and master messages are senden
            // election:
            if(getPrivateGroup().toString().equalsIgnoreCase((String) server_list.getFirst())){
                System.out.print("\nI am the master!!!!\n");
                // new master updates other servers
                i_am_master_server = true;
                group_master_server = getPrivateGroup().toString();
             
            }
        }
        
        if(i_am_master_server){
            this.multicastMasterServerInformation();
            this.multicastServerList();
        }  
            
    }
}
