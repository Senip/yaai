/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.client;

import at.technikum.bicss.sam.a1.alcatraz.common.IClient;
import at.technikum.bicss.sam.a1.alcatraz.common.IServer;
import at.technikum.bicss.sam.a1.alcatraz.common.Util;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Rudolf Galler <ic10b039@technikum-wien.at> [1010258039]
 */
public class ClientHost {

    public static void main(String[] args) {
        Util.readProps();
        String server_adr = Util.getServerAddress();
        String name = new String("PLAYER2");
        int port = Util.getRMIPort();
        //System.setProperty("java.rmi.server.hostname", "10.0.0.4");

        /*
         * Register own Client-Services
         */
        Registry rmireg = null;
        System.out.println(name + ": Starting up registry on client...");
        try {
            rmireg = LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            System.err.println(name + ": Not able to create Registry:\n" 
                    + e.getMessage() + "\ntry to bind to get active registry");
        }
        try {
            rmireg = LocateRegistry.getRegistry(port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        System.out.println(name + ": Binding own services...");
        try {
            IClient client = new ClientImpl();
            //rmireg.rebind("rmi://localhost:1099/Alcatraz/ClientImpl/ + name", server);
            Naming.rebind("rmi://localhost:1099/Alcatraz/ClientImpl/" + name, client);
            Util.printRMIReg(rmireg);
        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println(name + ": Alcatraz Client running.");


        /*
         * Register Player at Server
         */
        try {

//            System.out.println("Locating RMI-Registry of Server");
//            Registry rmireg = LocateRegistry.getRegistry(server_adr, port);
//            Util.printRMIReg(rmireg);
//            String rmi_adr = new String("rmi://" + "localhost" + ":" + port + "/Alcatraz/ServerImpl");
//            System.out.println("Looking up:" + rmi_adr);
//            IServer server = (IServer) rmireg.lookup(rmi_adr);

            String rmi_adr = new String("rmi://" + server_adr + ":" + port + "/Alcatraz/ServerImpl");
            System.out.println(name + ": Looking up:" + rmi_adr);
            IServer server = (IServer) Naming.lookup(rmi_adr);


            InetAddress local_addr = InetAddress.getLocalHost();
            System.out.println(local_addr.getHostAddress());
            server.register(name, local_addr.getHostName().toString());
         

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
