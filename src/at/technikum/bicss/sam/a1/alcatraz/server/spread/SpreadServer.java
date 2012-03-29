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
    String spread_group;
    String server_address;
    // connection and listner
    SpreadConnection connection = new SpreadConnection();
    MessageListener listener = new MessageListener();

    public static SpreadServer getInstance() {
        // create singelton if there is no instance
        if (_spread_instance == null) {
            _spread_instance = new SpreadServer();
        }
        return _spread_instance;
    }

    private SpreadServer() {
        Util.readProps();
        spread_group = Util.getGroupName();
        server_address = "localhost";
        try {
            // connect: address, port (0 = default 4803), privatename, .?., groupmessages
            connection.connect(InetAddress.getByName(server_address), 0, "privatename", 
                               false, true);
            connection.add(listener);
            System.out.println("SERVER: setup listener..");
            SpreadGroup group = new SpreadGroup();
            group.join(connection, spread_group);
            
        } catch (SpreadException ex) {
            System.out.print(ex);
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void multicastPlayerListToGroup(LinkedList<Player> pl) {
        try {
            SpreadMessage msg = new SpreadMessage();
            msg.setObject(pl);
            msg.addGroup(spread_group);
            msg.setReliable();
            connection.multicast(msg);

            System.out.println("SERVER: multicast messagee ..");

        } catch (SpreadException ex) {
            Logger.getLogger(SpreadServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
