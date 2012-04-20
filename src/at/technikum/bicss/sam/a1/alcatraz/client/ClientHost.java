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
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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
    private IClient _client = null;
    private LinkedList<Player> _playerlist = null;
    private Player _me = null;
    private Alcatraz _actrz = null;
    static private Logger l = null;

    public static void main(String[] args) {
        Util.readProps();
        PropertyConfigurator.configure(Util.getProps());
        l = Logger.getLogger(Util.getClientRMIPath());
        new ClientHost();
    }

    public ClientHost() {
        _gui = new ClientGUI(this);
        _gui.setVisible(true);

        _me = new Player(null, 0, null, 0, false);

        try {
            _client = new ClientImpl(this);
        } catch (RemoteException e) {
            l.error(e);
        }

        contactServer();
        setupClientRMI();
        l.info("Alcatraz Client running on " + _me.getAddress() + ":" + _me.getPort());
    }

    /**
     * Tries to find a running Alcatraz server process
     *
     * This is done by parsing the server addresses given in the property-file.
     * 1. Try to establish a connetion to a server on the RMI-port with a socket
     *
     * 2. if successfull, try to ask for the master server address
     *
     * 3. if successfull and master server is on different address, check again
     * if master-server is reachable on RMI-port with a socket
     *
     * if there is a problem in any of these steps, continue with next address
     * of property file
     *
     * on successfull completion {@code _serveraddr} is set to the Alcatraz
     * master server address and {@code _server) holds a reference to the remote {@link IServer}
     * object on failure both will be set to {@code null}
     */
    private void contactServer() {
        l.info("Try to find server for registration process...");
        _serveraddr = null;
        _serverport = Util.getServerRMIPort();
        Socket sock = null;
        SocketAddress sock_addr = null;

        _gui.lockRegisterBtn(true);

        String rmi_uri = null;
        for (String s_addr : Util.getServerAddressList()) {
            try {
                _serveraddr = s_addr;

                // 1. Establish a connetion to a server on the RMI-port with a socket                
                sock = new Socket();
                sock_addr = new InetSocketAddress(_serveraddr, _serverport);
                sock.connect(sock_addr, Util.getConTimeOut());
                // to determine own address of used network interface
                _me.setAddress(sock.getLocalAddress().getHostAddress());
                l.debug("Reached server:" + _serveraddr + ":" + _serverport);
                sock.close();

                // 2. Ask for the master server address
                rmi_uri = Util.buildRMIString(_serveraddr, _serverport, Util.getServerRMIPath());
                l.debug("Lookup " + rmi_uri);
                _server = (IServer) Naming.lookup(rmi_uri);
                l.debug("Asking for master registration server ");
                _serveraddr = _server.getMasterServer();
                l.debug("Master registration server at " + _serveraddr + ":" + _serverport);


                // 3. if master server is on different address:
                if (!_serveraddr.equals(s_addr)) {
                    // check again if master-server is reachable on RMI-port with a socket
                    sock = new Socket();
                    sock_addr = new InetSocketAddress(_serveraddr, _serverport);
                    sock.connect(sock_addr, Util.getConTimeOut());
                    // to determine own address of used network interface
                    _me.setAddress(sock.getLocalAddress().getHostAddress());
                    sock.close();

                    // Obtain reference to remote object
                    rmi_uri = Util.buildRMIString(_serveraddr, _serverport, Util.getServerRMIPath());
                    l.debug("Lookup " + rmi_uri);
                    _server = (IServer) Naming.lookup(rmi_uri);
                }
                l.info("Reached master server at " + _serveraddr + ":" + _serverport);
                _gui.lockRegisterBtn(false);
                break;
            } catch (NotBoundException e) { //thrown by Naming.lookup()
                l.warn(Util.getServerRMIPath() + " seems to be not bound"
                        + _serveraddr + ":" + _serverport, e);
                _serveraddr = null;
                _server = null;
            } catch (RemoteException e) { //thrown by Naming.lookup()
                l.warn(e.getMessage(), e);
                _serveraddr = null;
                _server = null;
            } catch (MalformedURLException e) { //thrown by Naming.lookup()
                l.error(e);
                _serveraddr = null;
                _server = null;
            } catch (IOException e) { //thrown by sock.connect()
                l.warn("registration server not reachable at "
                        + _serveraddr + ":" + _serverport, e);
                _serveraddr = null;
                _server = null;
            }
        }

        if (Util.isEmpty(_serveraddr)) {
            StringBuilder sb = new StringBuilder();
            sb.append("It was not possible to connect to a server at any of the adresses below:\n\n");
            for (String s : Util.getServerAddressList()) {
                sb.append(s).append("\n");
            }
            sb.append("\n Check your config file. ");
            sb.append("Ensure that at least one registration server is online");
            l.fatal(sb.toString());
        }
    }

    private void setupClientRMI() {
        /*
         * http://docs.oracle.com/javase/1.4.2/docs/guide/rmi/javarmiproperties.html
         * use own address to be associated with remote stubs for locally
         * created remote objects
         */
        _gui.lockRegisterBtn(true);
        System.setProperty("java.rmi.server.hostname", _me.getAddress());

        // get default client RMI port from prop-file
        int port = Util.getClientRMIPort();

        // Register own Client-Services
        l.info("Set up RMI registry for own client services...");
        do {
            try {
                _rmireg = LocateRegistry.createRegistry(port);
            } catch (RemoteException e) {
                l.debug("Not able to create registry on port: " + _me.getPort(), e);
                port++;
                l.debug("trying different port: " + port);
            }
            break;
        } while (true);

        _me.setPort(port);
        _gui.lockRegisterBtn(false);
    }

    public void registerPlayer(String p_name) {
        _me.setName(null);

        // Binding own services
        l.info("Binding own services...");
        try {
            String rmi_uri = Util.buildRMIString(_me.getAddress(), _me.getPort(),
                    Util.getClientRMIPath(), p_name);
            Naming.rebind(rmi_uri, _client);
            Util.logRMIReg(_rmireg);
            _me.setName(p_name);
        } catch (RemoteException e) {
            l.error(e);
        } catch (MalformedURLException e) {
            l.error(e);
        }

        // Register Player at Server
        if (!_me.getName().isEmpty()) {
            l.info("Register " + _me.getName() + " at server " + _serveraddr);
            try {
                String rmi_uri = Util.buildRMIString(_serveraddr, _serverport, Util.getServerRMIPath());
                l.debug("Looking up:" + rmi_uri);
                _server = (IServer) Naming.lookup(rmi_uri);
                _server.register(_me.getName(), _me.getAddress(), _me.getPort());
            } catch (Exception e) {
                l.error(e);
            }
        }
    }

    public void unregisterPlayer() {
        try {
            _server.deregister(_me.getName());
            _gui.updatePlayerList(null);
        } catch (Exception e) {
            l.error(e);
        }
    }

    public void setReady(boolean state) {
        try {
            _server.setStatus(_me.getName(), state);
        } catch (RemoteException e) {
            l.error(e);
        }
    }

    public void processPlayerList(LinkedList<Player> pl) {
        int ctr = 0;

        for (Player p : pl) {
            //find own entry in list to get ID given by server
            if (p.getName().equals(_me.getName())) {
                _me = p;
            }
            // count how many players are ready
            if (p.isReady()) {
                ctr++;
            }
        }

        //reverse and rotate list until I'm last in place 
        // for easier distribution of moves
        Collections.reverse(pl);
        Collections.rotate(pl, _me.getId());
        _playerlist = pl;

        _gui.updatePlayerList(_playerlist);


        // if all players are ready start the game 
        // AND there are at least 2 players
        if ((ctr == _playerlist.size()) && (ctr >= 2)) {
            l.info("Game ready!");
            initGame();
        }
    }

    private void initGame() {
        _actrz = new Alcatraz();
        _actrz.init(_playerlist.size(), _me.getId());

        for (Player p : _playerlist) {
            _actrz.getPlayer(p.getId()).setName(p.getName());
        }

        _gui.setVisible(false);
        /*
         * set playername as title to distinguish several windows
         */
        _actrz.getWindow().setTitle(
                _actrz.getWindow().getTitle() + " - " + _me.getName());
        _actrz.showWindow();

        _actrz.addMoveListener(this);

        _actrz.start();
    }

    public void processMove(Move m) {
        l.debug("received a move from " + m.getPlayer().getName());
        l.trace(m.toString());
        _actrz.doMove(m.getPlayer(), m.getPrisoner(), m.getRowOrCol(), m.getRow(), m.getCol());
    }

    @Override
    public void moveDone(at.falb.games.alcatraz.api.Player player, Prisoner prsnr, int i, int i1, int i2) {
        l.debug(player.getName() + " did a move");
        Move move = new Move(player, prsnr, i, i1, i2);
        l.trace(move.toString());

        IClient clientproxy = null;
        for (Player p : _playerlist) {
            if (p.equals(_me)) {
                break;
            }

            while (true) {
                try {
                    String rmi_uri = Util.buildRMIString(p.getAddress(), p.getPort(), Util.getClientRMIPath(), p.getName());
                    l.info("Sending move to " + rmi_uri);
                    clientproxy = (IClient) Naming.lookup(rmi_uri);
                    clientproxy.doMove(move);
                    break;
                } catch (NotBoundException e) {
                    l.warn(Util.getClientRMIPath() + p.getName()
                            + " seems to be not bound on "
                            + p.getAddress() + ":" + p.getPort(), e);
                } catch (RemoteException e) {
                    l.warn(p.getAddress() + ":" + p.getPort(), e);
                } catch (MalformedURLException e) {
                    l.error(e);
                }
            }

        }
    }

    @Override
    public void gameWon(at.falb.games.alcatraz.api.Player player) {
        l.info("gameWon");
        Util.warnUser(_gui, "gameWon");

        _actrz.disposeWindow();
        _actrz.closeWindow();
        _gui.setVisible(true);

    }
}
