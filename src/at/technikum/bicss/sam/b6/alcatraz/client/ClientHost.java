/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.client;

import at.technikum.bicss.sam.b6.alcatraz.common.Player;
import at.technikum.bicss.sam.b6.alcatraz.common.Move;
import at.technikum.bicss.sam.b6.alcatraz.common.AlcatrazServerException;
import at.technikum.bicss.sam.b6.alcatraz.common.IClient;
import at.technikum.bicss.sam.b6.alcatraz.common.IServer;
import at.technikum.bicss.sam.b6.alcatraz.common.Util;
import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.Prisoner;
import java.awt.Dimension;
import java.awt.Toolkit;
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
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author 
 */
public class ClientHost implements MoveListener 
{

    private String              serveraddr = null;
    private int                 serverport = 0;
    private Registry            rmireg     = null;
    private ClientGUI           gui        = null;
    private IServer             server     = null;
    private IClient             client     = null;
    private LinkedList<Player>  playerlist = null;
    private Player              me         = null;
    private Alcatraz            actrz      = null;
    private static Logger       l          = null;

    public static void main(String[] args) 
    {
        PropertyConfigurator.configure(Util.readProps());
        l = Util.setLogger(Util.getClientRMIPath());
        new ClientHost();
    }

    public ClientHost() 
    {
        me  = new Player(null, 0, null, 0, false);
        gui = new ClientGUI(this);
        gui.setVisible(true);

        if(contactServer())
        {
            setupClientRMIReg();
            l.info("Alcatraz Client running on " + me.getAddress() + ":" + me.getPort());
        }
    }

    /**
     * Tries to find a running Alcatraz server process
     *
     * This is done by parsing the server addresses given in the property-file.
     * 1. Try to establish a connection to a server on the RMI-port with a socket
     *
     * 2. if successful, try to ask for the master server address
     *
     * 3. if successful and master server is on different address, check again
     * if master-server is reachable on RMI-port with a socket
     *
     * if there is a problem in any of these steps, continue with next address
     * of property file
     *
     * on successful completion {@code _serveraddr} is set to the Alcatraz
     * master server address and {@code _server) holds a reference to the remote {@link IServer}
     * object on failure both will be set to {@code null} and the application is terminated
     * 
     */
    private boolean contactServer()
    {
        l.info("Try to find server for registration process...");
        serveraddr  = null;
        serverport  = Util.getServerRMIPort();
        Socket sock = null;
        SocketAddress sock_addr = null;

        gui.lockRegisterBtn(true);

        String rmi_uri = null;
        for (String s_addr : Util.getServerAddressList()) 
        {
            try 
            {
                serveraddr = s_addr;

                // 1. Establish a connetion to a server on the RMI-port with a socket                
                sock = new Socket();
                sock_addr = new InetSocketAddress(serveraddr, serverport);
                sock.connect(sock_addr, Util.getConTimeOut());
                
                // to determine own address of used network interface
                me.setAddress(sock.getLocalAddress().getHostAddress());
                l.debug("Reached server:" + serveraddr + ":" + serverport);
                sock.close();

                // 2. Ask for the master server address
                rmi_uri    = Util.buildRMIString(serveraddr, serverport, 
                                                 Util.getServerRMIPath());
                l.debug("Lookup " + rmi_uri);
                server     = (IServer) Naming.lookup(rmi_uri);
                l.debug("Asking for master registration server ");
                serveraddr = server.getMasterServer();
                l.debug("Master registration server at " + serveraddr + ":" + serverport);

                // 3. if master server is on different address:
                if (!serveraddr.equals(s_addr)) 
                {
                    // check again if master-server is reachable on RMI-port with a socket
                    sock = new Socket();
                    sock_addr = new InetSocketAddress(serveraddr, serverport);
                    sock.connect(sock_addr, Util.getConTimeOut());
                    
                    // to determine own address of used network interface
                    me.setAddress(sock.getLocalAddress().getHostAddress());
                    sock.close();

                    // Obtain reference to remote object
                    rmi_uri = Util.buildRMIString(serveraddr, serverport, Util.getServerRMIPath());
                    l.debug("Lookup " + rmi_uri);
                    server  = (IServer) Naming.lookup(rmi_uri);
                }
                
                l.info("Reached master server at " + serveraddr + ":" + serverport);
               
                gui.updatePlayerList(server.getPlayerList());                
                gui.lockRegisterBtn(false);
                              
                break;
            } 
            catch (NotBoundException e)         //thrown by Naming.lookup()
            { 
                l.warn(Util.getServerRMIPath() + " seems to be not bound "
                        + serveraddr + ":" + serverport, e);
                serveraddr = null;
                server     = null;
            } 
            catch (RemoteException e)           //thrown by Naming.lookup()
            {                                   //or server.getPlayerList()
                l.warn(e.getMessage(), e);
                serveraddr = null;
                server     = null;
            } 
            catch (MalformedURLException e)     //thrown by Naming.lookup()
            { 
                l.error(e.getMessage(), e);
                serveraddr = null;
                server     = null;
            } 
            catch (IOException e)               //thrown by sock.connect()
            { 
                l.warn("Server not reachable "
                        + serveraddr + ":" + serverport, e);
                serveraddr = null;
                server     = null;
            }
        }

        if (Util.isEmpty(serveraddr)) 
        {
            StringBuilder sb = new StringBuilder();
            sb.append("It was not possible to connect to a server at any of the adresses below:\n\n");
            
            for (String s : Util.getServerAddressList()) 
            {
                sb.append(s).append("\n");
            }
            
            sb.append("\n Check your config file. ");
            sb.append("Ensure that at least one registration server is online");
            l.fatal(sb.toString());
            
            return false;
        }
        
        return true;
    }
   
    private void setupClientRMIReg() 
    {
        /*
         * http://docs.oracle.com/javase/1.4.2/docs/guide/rmi/javarmiproperties.html
         * use own address to be associated with remote stubs for locally
         * created remote objects
         */
        
        try 
        {
            client = new ClientRMI(this);
        } 
        catch (RemoteException e) 
        {
            l.fatal("Failed to creates and exports a new UnicastRemoteObject object " + 
                     "using an anonymous port" + e.getMessage(), e);
            System.exit(1);
        }
        
        gui.lockRegisterBtn(true);
        System.setProperty("java.rmi.server.hostname", me.getAddress());

        // get default client RMI port from prop-file
        int port = Util.getClientRMIPort();

        // Setup Registry
        l.info("Set up RMI registry for own client services...");
        
        boolean success = false;
        for(int i = 1; !(success) && (i <= Util.CLIENT_RMIREG_RETRY_MAX); i++)
        {
            l.info("#" + i + " Attempt");
            
            try 
            {
                rmireg  = LocateRegistry.createRegistry(port);
                success = true;
                break;
            } 
            catch (RemoteException e) 
            {
                l.debug("Not able to create registry on port: " + me.getPort(), e);
                port = Util.getRandomPort();
                l.debug("trying different port: " + port);
            }            
        }
        
        if(!success)
        {
            l.fatal("Unable to set up RMI registry for client services ");
            System.exit(1);
        }

        me.setPort(port);
        gui.lockRegisterBtn(false);
    }

    /**
     * Check if the Player is registered
     * 
     * If the client's state is waiting but the Backbone was reset, this will 
     * cause unjustified errors.
     * 
     * @return if player is registered
     */
    public boolean isPlayerRegistered(String name) throws RemoteException
    {         
        boolean found = false;
        for(Player p : server.getPlayerList())
        {
            found = found || name.equalsIgnoreCase(p.getName());
        } 
        
        return found;
    }
    
    public boolean registerPlayer(String name) 
    {
        me.setName(null);
        boolean retry;
        boolean success = false;
        
        l.debug("Try to bind own services...");
     
        try 
        {
            String rmi_uri = Util.buildRMIString(me.getAddress(), me.getPort(),
                    Util.getClientRMIPath(), name);
            Naming.rebind(rmi_uri, client);
            l.info("Bound client methods to " + rmi_uri);
            Util.logRMIReg(rmireg);
            me.setName(name);
        } 
        catch (RemoteException e) 
        {
            l.error("Unable to bind methods to registry.\n" + e.getMessage(), e);
        } 
        catch (MalformedURLException e) 
        {
            l.warn(e.getMessage(), e);
        }
                  
        // Register Player at Server
        if (!me.getName().isEmpty()) 
        { 
            l.debug("Try to register " + me.getName());
            do
            { 
                retry = false;
                
                try 
                {
                    server.register(me.getName(), me.getAddress(), me.getPort());
                    l.info("Registered " + me.getName() + " at server " + serveraddr + ":" + serverport);
                    success = true;
                } 
                catch (AlcatrazServerException e) 
                {
                    l.error("Server refused registration:\n" + e.getMessage(), e);
                } 
                catch (RemoteException e) 
                {
                    l.info("Relocation server...");
                    retry = contactServer();
                }
            } while(retry);
        }
        return success;
    }  
   
    public boolean unregisterPlayer() 
    {
        boolean retry;
        boolean success = false;

        l.info("Try to remove player methods from registry");
        try 
        {
            String rmi_uri = Util.buildRMIString(me.getAddress(), me.getPort(),
                             Util.getClientRMIPath(), me.getName());
            Naming.unbind(rmi_uri);
            Util.logRMIReg(rmireg);
        } 
        catch (NotBoundException e) 
        {
            l.warn("Methods not bound to registry.\n" + e.getMessage(), e);
        } 
        catch (RemoteException e) 
        {
            l.warn("Unable to remove methods from registry.\n" + e.getMessage(), e);
        } 
        catch (MalformedURLException e) 
        {
            l.warn(e.getMessage(), e);
        }

        // Unregister Player at Server
        l.debug("Try to unregister " + me.getName());
        
        do
        {
            retry = false;
            
            try 
            {
                try
                {
                    server.deregister(me.getName());
                    success = true;
                }   
                catch (AlcatrazServerException e) 
                {               
                    if(isPlayerRegistered(me.getName()))
                    {
                        l.error("Server refused to unregister:\n" + e.getMessage(), e);
                    }
                    else
                    {
                        l.debug("Client statemachine fixed");
                        success = true;  
                    }
                } 
            } 
            catch (RemoteException e) 
            {
                l.info("Relocation server...");
                retry = contactServer();
            }
        } while(retry);
            
        if(success)
        {
            gui.updatePlayerList(null);                
            l.info("Unregistered " + me.getName() + " from server " + serveraddr + ":" + serverport);
            me.setName(null);
        }
        
        return success;
    }

    public boolean setReady(boolean state) 
    {
        boolean retry;
        boolean success = false;
        l.debug("Try to update ready state of " + me.getName() + " to " + state);
        
        do
        {
            retry = false;
            
            try
            {
                try
                {
                    server.setStatus(me.getName(), state);
                    success = true;
                } 
                catch (AlcatrazServerException e) 
                {    
                    if(!isPlayerRegistered(me.getName()))
                    {
                        l.debug("Client statemachine fixed");
                        if(registerPlayer(me.getName()))
                        {
                            success = true;
                        }
                        else
                        {
                            unregisterPlayer();
                        }
                    }

                    if(!success)
                    {
                        l.error("Server refused to set ready status:\n" + e.getMessage(), e);  
                    }
                } 
            }
            catch (RemoteException e) 
            {
                l.info("Relocation server...");
                retry = contactServer();
            }
        } while(retry);
        
        return success;
    }
    
    private void updatePlayerList()
    {
        boolean retry;
        l.debug("Try to update the Player List");
        
        do
        {
            retry = false;
            
            try 
            {  
                gui.updatePlayerList(server.getPlayerList());
            } 
            catch (RemoteException e) 
            {
                l.info("Relocation server...");
                retry = contactServer();
            }
        } while(retry);        
    }

    public void processPlayerList(LinkedList<Player> pl) 
    {
        int ctr = 0;

        for (Player p : pl) 
        {
            //find own entry in list to get ID given by server
            if (p.getName().equals(me.getName())) 
            {
                me = p;
            }
            
            // count how many players are ready
            if (p.isReady()) 
            {
                ctr++;
            }
        }

        //reverse and rotate list until I'm last in place 
        // for easier distribution of moves
        Collections.reverse(pl);
        Collections.rotate(pl, me.getId());
        playerlist = pl;

        gui.updatePlayerList(playerlist);

        // if all players are ready 
        // AND there are at least 2 players
        // start the game 
        if ((ctr == playerlist.size()) && (ctr >= 2)) 
        {
            l.info("Game ready!");
            initGame();
        }
    }

    private void initGame() 
    {
        actrz = new Alcatraz();
        actrz.init(playerlist.size(), me.getId());

        if(retreivePlayerProxys())
        {
            for (Player p : playerlist) 
            {
                actrz.getPlayer(p.getId()).setName(p.getName());
            }

            gui.setVisible(false);
            /*
             * set playername as title to distinguish several windows
             */
            actrz.getWindow().setTitle(
                    actrz.getWindow().getTitle() + " - " + me.getName());

            Util.centerFrame(actrz.getWindow());
            actrz.showWindow();

            actrz.addMoveListener(this);

            actrz.start();
        }
        else
        {
            updatePlayerList();
        }
    }

    private boolean retreivePlayerProxys() 
    {
        boolean success = true;
        
        for (Player p : playerlist) 
        {
            if (p.getName().equals(me.getName())) 
            {
                break;
            }
            
            try 
            {
                String rmi_uri = Util.buildRMIString(p.getAddress(), p.getPort(), Util.getClientRMIPath(), p.getName());
                l.info("Obtaining proxy: " + rmi_uri);
                IClient clientproxy = null;
                clientproxy = (IClient) Naming.lookup(rmi_uri);
                p.setProxy(clientproxy);
                continue;
            } 
            catch (NotBoundException e) 
            {
                l.warn(Util.getClientRMIPath() + p.getName()
                        + " seems to be not bound on "
                        + p.getAddress() + ":" + p.getPort(), e);
            } 
            catch (RemoteException e) 
            {
                l.warn(p.getAddress() + ":" + p.getPort(), e);
            } 
            catch (MalformedURLException e) 
            {
                l.warn(e.getMessage(), e);
            }
            
            success = false;
            break;
        }
        
        if(!success)
        {
            l.error("Starting the Game failed\nSome Player seem not be ready");
        }
        
        return success;
    }

    public void processMove(Move m) 
    {
        l.debug("received a move from " + m.getPlayer().getName());
        l.trace(m.toString());
        actrz.doMove(m.getPlayer(), m.getPrisoner(), m.getRowOrCol(), m.getRow(), m.getCol());
    }

    @Override
    public void moveDone(at.falb.games.alcatraz.api.Player player, Prisoner prsnr, int i, int i1, int i2) 
    {  
        l.debug(player.getName() + " did a move");
        Move move = new Move(player, prsnr, i, i1, i2);
        l.trace(move.toString());

        for (Player p : playerlist) 
        {
            if (p.getName().equals(me.getName())) 
            {
                break;
            }

            while (true) 
            {
                try 
                {
                    l.debug("Sending move to " + p.toString());
                    p.getProxy().doMove(move);
                    break;
                } 
                catch (RemoteException e) 
                {
                    l.warn(p.getAddress() + ":" + p.getPort(), e);
                }
            }
        }
    }

    @Override
    public void gameWon(at.falb.games.alcatraz.api.Player player) 
    { 
        l.info(player.getName() + " has won the game");        

        actrz.closeWindow();
        actrz.disposeWindow();
        
        gui.setVisible(true);
        Util.warnUser(gui, (player.getName() + " has won the game"));
        
        playerlist = new LinkedList();
        gui.updatePlayerList(playerlist);
        gui.reset();
    }
}
