/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.server;

import at.technikum.bicss.sam.a1.alcatraz.common.IServer;
import at.technikum.bicss.sam.a1.alcatraz.common.Util;
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
        int port = Util.getRMIPort();
        System.setProperty("java.rmi.server.hostname", "10.0.0.2");

        /*
         * Register Server-Services
         */
        Registry rmireg = null;
        System.out.println("SERVER: Starting up registry...");
        try {
            rmireg = LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            System.err.println("SERVER: Not able to create Registry:" 
                    + e.getMessage() + "\ntry to bind to get active registry");
        }
        try {
            rmireg = LocateRegistry.getRegistry(port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        System.out.println("SERVER: Bind...");
        try {
            IServer server = new ServerImpl();
            //rmireg.rebind("rmi://localhost:1099/Alcatraz/ServerImpl", server);
            Naming.rebind("rmi://localhost:1099/Alcatraz/ServerImpl", server);
            Util.printRMIReg(rmireg);
            System.out.println("SERVER: Alcatraz running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
