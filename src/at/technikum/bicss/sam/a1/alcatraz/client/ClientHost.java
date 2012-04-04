/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.client;

import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.Prisoner;
import at.technikum.bicss.sam.a1.alcatraz.common.IClient;
import at.technikum.bicss.sam.a1.alcatraz.common.IServer;
import at.technikum.bicss.sam.a1.alcatraz.common.Move;
import at.technikum.bicss.sam.a1.alcatraz.common.Player;
import at.technikum.bicss.sam.a1.alcatraz.common.Util;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.LinkedList;

/**
 *
 * @auth8or Rudolf Galler <ic10b039@technikum-wien.at> [1010258039]
 */
public class ClientHost implements MoveListener {

    private String _serveraddr = null;
    private int _serverport = 0;
    private Registry _rmireg = null;
    private ClientGUI _gui = null;
    private IServer _server = null;
    private LinkedList<Player> _playerlist = null;
    private Player _me = null;

    public static void main(String[] args) {
        new ClientHost();
    }

    public ClientHost() {
        Util.readProps();
        _gui = new ClientGUI(this);
        _gui.setVisible(true);

        _me = new Player(null, 0, null, 0, false);
        _me.setPort(Util.getClientRMIPort());
        _serverport = Util.getServerRMIPort();

        // Check which of the servers is reachable
        for (String s_addr : Util.getServerAddress()) {
            try {
                Socket sock = new Socket();
                SocketAddress sock_addr = new InetSocketAddress(s_addr, _serverport);
                sock.connect(sock_addr, 500);
                _serveraddr = s_addr;
                System.out.println("Reached server " + _serveraddr);
                /*
                 * remember own address with which the server was reached
                 */
                _me.setAddress(sock.getLocalAddress().getHostAddress());
                sock.getLocalAddress().isReachable(_serverport);
                break;
            } catch (Exception e) {
                System.err.println("Could not reach server at " + s_addr + " on " + _serverport);
                System.err.println(e.toString());
            }
        }
        /*
         * http://docs.oracle.com/javase/1.4.2/docs/guide/rmi/javarmiproperties.html
         * use own address to be associated with remote stubs for locally
         * created remote objects
         */
        System.setProperty("java.rmi.server.hostname", _me.getAddress());

        /*
         * Register own Client-Services
         */

        System.out.println("Set up own registry...");
        try {
            _rmireg = LocateRegistry.createRegistry(_me.getPort());
        } catch (RemoteException e) {
            System.err.println("Not able to create registry on port: " + _me.getPort());
            System.err.println(e.getMessage());
            System.err.println("try to search for active registry");
        }
        try {
            _rmireg = LocateRegistry.getRegistry(_me.getPort());
            System.out.println("Got registry on port " + _me.getPort());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        System.out.println("Alcatraz Client running.");

    }

    public void registerPlayer(String p_name) {

        /*
         * Binding own services
         */
        _me.setName(p_name);
        System.out.println("Binding own services...");
        try {
            IClient client = new ClientImpl(this);
            //rmireg.rebind("rmi://localhost:1099/Alcatraz/ClientImpl/ + name", server);
            Naming.rebind("rmi://localhost:" + _me.getPort() + "/Alcatraz/ClientImpl/" + _me.getName(), client);
            Util.printRMIReg(_rmireg);
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

            String rmi_adr = new String("rmi://" + _serveraddr + ":" + _serverport + "/Alcatraz/ServerImpl");
            System.out.println("Looking up:" + rmi_adr);
            _server = (IServer) Naming.lookup(rmi_adr);


            //InetAddress local_addr = InetAddress.getLocalHost();
            _server.register(_me.getName(), _me.getAddress(), _me.getPort());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterPlayer() {
        try {
            _server.deregister(_me.getName());
            _gui.updatePlayerList(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setReady(boolean state) {
        try {
            _server.setStatus(_me.getName(), state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processPlayerList(LinkedList<Player> pl) {
        int ctr = 0;

        for (Player p : pl) {
            /*
             * find own entry in list to get ID given by server
             */
            if (p.getName().equals(_me.getName())) {
                _me = p;
            }
            /*
             * check how many players are ready
             */
            if (p.isReady()) {
                ctr++;
            }
        }

        /*
         * reverse and rotate list until I'm last in place for easier
         * distribution of moves
         */
        Collections.reverse(pl);
        Collections.rotate(pl, _me.getId());
        _playerlist = pl;

        _gui.updatePlayerList(_playerlist);

        /*
         * in case all players are ready start the game
         */
        if (ctr == _playerlist.size()) {
            // los gehts mit alcazraz 
            System.out.println("Game ready!");

            Alcatraz a = new Alcatraz();
            a.init(_playerlist.size(), _me.getId());

            for (Player p : _playerlist) {
                a.getPlayer(p.getId()).setName(p.getName());
            }

            _gui.setVisible(false);
            a.showWindow();

            a.addMoveListener(this);

            a.start();
        }
    }

    @Override
    public void moveDone(at.falb.games.alcatraz.api.Player player, Prisoner prsnr, int i, int i1, int i2) {
        System.out.println("moveDone");
        
        Move move = new Move(player, prsnr, i, i1, i2);
        IClient clientproxy = null;
        for (Player p : _playerlist) {
            if (p.equals(_me)) {
                break;
            }
            try {
                String rmi_adr = new String("rmi://" + p.getAddress() + ":" + p.getPort() + "/Alcatraz/ClientImpl/" + p.getName());
                System.out.println("Sending move to " + rmi_adr);
                clientproxy = (IClient) Naming.lookup(rmi_adr);
                clientproxy.doMove(p.getName(), move);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void gameWon(at.falb.games.alcatraz.api.Player player) {
        System.out.println("gameWon");
    }
}
