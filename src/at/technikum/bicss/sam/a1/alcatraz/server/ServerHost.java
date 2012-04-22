/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.server;

import at.technikum.bicss.sam.a1.alcatraz.common.IServer;
import at.technikum.bicss.sam.a1.alcatraz.common.Util;
import at.technikum.bicss.sam.a1.alcatraz.server.spread.PlayerList;
import at.technikum.bicss.sam.a1.alcatraz.server.spread.SpreadServer;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Rudolf Galler <ic10b039@technikum-wien.at> [1010258039]
 */
public class ServerHost {

    public static void main(String args[]) {
        Util.readProps();
        int port = Util.getServerRMIPort();
        System.setProperty("java.rmi.server.hostname", Util.getMyServerAddress());

        /*
         * 
         * Register Server-Services
         */
        Registry rmireg = null;
        System.out.println("SERVER: Set up own registry...");
        try {
            rmireg = LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            System.err.println("SERVER: Not able to create registry on port " + port);                      
            System.err.println(e.getMessage());
            System.err.println("try to search for active registry");
        }
        try {
            rmireg = LocateRegistry.getRegistry(port);
            System.out.println("SERVER: Got registry on port " + port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        System.out.println("SERVER: Bind...");
        try {
            IServer server = new ServerRMI();
            String rmi_uri = Util.buildRMIString(Util.getMyServerAddress(), 
                    Util.getServerRMIPort(), Util.getServerRMIPath());
            Naming.rebind(rmi_uri, server);
            Util.logRMIReg(rmireg);
            System.out.println("SERVER: Alcatraz running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
       