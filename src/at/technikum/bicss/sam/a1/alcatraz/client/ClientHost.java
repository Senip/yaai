/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.client;

import at.technikum.bicss.sam.a1.alcatraz.common.IClient;
import at.technikum.bicss.sam.a1.alcatraz.common.IServer;
import at.technikum.bicss.sam.a1.alcatraz.common.Player;
import at.technikum.bicss.sam.a1.alcatraz.common.Util;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedList;

/**
 *
 * @auth8or Rudolf Galler <ic10b039@technikum-wien.at> [1010258039]
 */
public class ClientHost {

    private String server_adr = null;
    private String own_addr = null;
    private String name = null; //new String(args[1]);
    private int client_port = 0;
    private int server_port = 0;
    private Registry rmireg = null;
    private ClientGUI gui = null;

    public static void main(String[] args) {
        new ClientHost();
    }

    public ClientHost() {
        Util.readProps();
        gui = new ClientGUI(this);
        gui.setVisible(true);

        client_port = Util.getClientRMIPort();
        server_port = Util.getServerRMIPort();

        // Check which of the servers is reachable
        for (String s_addr : Util.getServerAddress()) {
            try {
                Socket sock = new Socket();
                SocketAddress sock_addr = new InetSocketAddress(s_addr, server_port);
                sock.connect(sock_addr, 500);
                server_adr = s_addr;
                System.out.println(name + ": Reached server " + server_adr);
                /*
                 * remember own address with which the server was reached
                 */
                own_addr = sock.getLocalAddress().getHostAddress();
                sock.getLocalAddress().isReachable(server_port);
                break;
            } catch (Exception e) {
                System.err.println("Could not reach server at " + s_addr + " on " + server_port);
                System.err.println(e.toString());
            }
        }
        /*
         * http://docs.oracle.com/javase/1.4.2/docs/guide/rmi/javarmiproperties.html
         * use own address to be associated with remote stubs for locally
         * created remote objects
         */
        System.setProperty("java.rmi.server.hostname", own_addr);

        /*
         * Register own Client-Services
         */

        System.out.println(name + ": Set up own registry...");
        try {
            rmireg = LocateRegistry.createRegistry(client_port);
        } catch (RemoteException e) {
            System.err.println(name + ": Not able to create registry on port: " + client_port);
            System.err.println(e.getMessage());
            System.err.println("try to search for active registry");
        }
        try {
            rmireg = LocateRegistry.getRegistry(client_port);
            System.out.println(name + ": Got registry on port " + client_port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        System.out.println(name + ": Alcatraz Client running.");

    }

    public void registerPlayer(String name) {

        /*
         * Binding own services
         */
        System.out.println(name + ": Binding own services...");
        try {
            IClient client = new ClientImpl(this);
            //rmireg.rebind("rmi://localhost:1099/Alcatraz/ClientImpl/ + name", server);
            Naming.rebind("rmi://localhost:" + client_port + "/Alcatraz/ClientImpl/" + name, client);
            Util.printRMIReg(rmireg);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

            String rmi_adr = new String("rmi://" + server_adr + ":" + server_port + "/Alcatraz/ServerImpl");
            System.out.println(name + ": Looking up:" + rmi_adr);
            IServer server = (IServer) Naming.lookup(rmi_adr);


            //InetAddress local_addr = InetAddress.getLocalHost();
            server.register(name, own_addr, client_port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processPlayerList(LinkedList<Player> pl) {
        gui.updatePlayerList(pl);

        int ctr = 0;
        for (Player p : pl) {
            if (p.isReady()) {
                ctr++;
            }
        }
        if (ctr == pl.size()) {
            // los gehts mit alcazraz 
            Util.warnUser(gui, "Game ready!");
        }
    }
}
