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

    // replace with configuration
    
    String spread_group = "alcatraz";
    
    SpreadConnection connection = new SpreadConnection();
    MessageListener listener = new MessageListener();
    
    public SpreadServer() {
        try {


            connection.connect(InetAddress.getByName("192.168.1.100"), 0,
                    "privatename", false, true);

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
    
    public void multicastPlayerListToGroup(LinkedList<Player> pl){
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
