/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.client;

import at.falb.games.alcatraz.api.Alcatraz;
import at.falb.games.alcatraz.api.MoveListener;
import at.falb.games.alcatraz.api.Prisoner;
import at.technikum.bicss.sam.b6.alcatraz.common.exception.AlcatrazInitGameException;
import at.technikum.bicss.sam.b6.alcatraz.common.exception.AlcatrazClientStateException;
import at.technikum.bicss.sam.b6.alcatraz.common.exception.AlcatrazServerException;
import at.technikum.bicss.sam.b6.alcatraz.common.IClient;
import at.technikum.bicss.sam.b6.alcatraz.common.IServer;
import at.technikum.bicss.sam.b6.alcatraz.common.Move;
import at.technikum.bicss.sam.b6.alcatraz.common.Player;
import at.technikum.bicss.sam.b6.alcatraz.common.Util;
import at.technikum.bicss.sam.b6.alcatraz.server.spread.PlayerList;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author 
 */
public class ClientHost implements MoveListener 
{

    private String              serverAddr = null;
    private int                 serverPort = 0;
    private Registry            rmiReg     = null;
    private ClientGUI           gui        = null;
    private IServer             server     = null;
    private String              rmiURI     = null;
    private IClient             client     = null;
    private LinkedList<Player>  playerList = null;
    private Player              me         = null;
    private Alcatraz            actrz      = null;
    private static Logger       l            = Util.getLogger();
    private boolean             inGame       = false;
    private WindowAdapter       exitListener = new WindowAdapter()
    {
        @Override
        public void windowClosing(WindowEvent e) 
        {
            gameExit();
        }
    };

    public static void main(String[] args) 
    {
        ClientHost client;       
        
        PropertyConfigurator.configure(Util.readProps());
        l      = Util.setLogger(Util.getClientRMIPath());
        client = new ClientHost();
                
        l.info("Connecting...");
        if(client.open())
        {
            // Wait until the User closes the Application
            client.waitForExit();       
        }
        
        // Clean Up
        client.close();
        l.info("Terminated.");
        System.exit(0);
        
    }

    public ClientHost() 
    {       
        me  = new Player(null, 0, null, 0, false);        
        gui = new ClientGUI(this);
        
        gui.lock(true);
        gui.setVisible(true);    
    }
    
    public boolean open()
    {
        // Contact Server and Setup RMI
        if(contactServer())
        {
            setupClientRMIReg();
            l.info("Alcatraz Client running on port " + me.getPort());
            gui.lock(false);
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void waitForExit()
    {        
        gui.waitForExit();
    }
    
    public void close()
    {
        try { UnicastRemoteObject.unexportObject(client, false); } catch (Exception e) { }
        try { Naming.unbind(rmiURI);                             } catch (Exception e) { }
        try { UnicastRemoteObject.unexportObject(client, true);  } catch (Exception e) { }
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
        
        serverAddr  = null;
        serverPort  = Util.getServerRMIPort();
        Socket sock;
        String rmiURI;
        SocketAddress sock_addr;

        do
        {
            boolean success       = false;
            boolean reachedServer = false;
            
            for (String thisAddr : Util.getServerAddressList()) 
            {
                try 
                {
                    serverAddr        = thisAddr;
                    
                    do
                    {
                        
                        // 1. Establish a connetion to a server on the RMI-port with a socket                
                        sock      = new Socket();
                        sock_addr = new InetSocketAddress(serverAddr, serverPort);
                        sock.connect(sock_addr, Util.getConTimeOut());
                        
                        // to determine own address of used network interface
                        me.setAddress("0.0.0.0"); //sock.getLocalAddress().getHostAddress());
                        l.debug("Reached server:" + serverAddr + ":" + serverPort);
                        sock.close();

                        // 2. Obtain reference to remote object
                        rmiURI     = Util.buildRMIString(serverAddr, serverPort, Util.getServerRMIPath());
                        l.debug("Lookup " + rmiURI);
                        server     = (IServer) Naming.lookup(rmiURI);

                        // 3. Ask for the master server address
                        l.debug("Asking for master registration server");
                        String masterAddr = server.getMasterServer();
                        l.debug("Master registration server at " + serverAddr + ":" + serverPort);

                        // At least one server is online: 
                        reachedServer = true;
                        
                        // 4. Retry if master server is on different address:
                        if(!serverAddr.equals(masterAddr))
                        {
                            serverAddr = masterAddr;
                            continue;
                        }
                        
                        break;
                        
                    } while(true);

                    l.info("Reached master server");
                    success = true;
                    
                    //server.getPlayerList();
                    updatePlayerList();         
                    break;
                } 
                catch(NotBoundException | IOException e)  
                { 
                    l.warn("Server not reachable " + serverAddr + ":" + serverPort);
                    l.debug(e.getMessage());
                    serverAddr = null;
                    server     = null;
                }
            }
            
            if(!success && reachedServer)  
            {
                // Reached Server, but can not connect to master
                // New master is elected --> retry 
                continue;
            }
            
            break;
                        
        } while(true);

        if (Util.isEmpty(serverAddr)) 
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
            l.debug("Client Handle: " + client);
        } 
        catch (RemoteException e) 
        {
            l.fatal("Failed to creates and exports a new UnicastRemoteObject object " + 
                     "using an anonymous port" + e.getMessage(), e);
            System.exit(1);
        }
        
        // Liste on all IP Adresses
        System.setProperty("java.rmi.server.hostname", "0.0.0.0");

        // get default client RMI port from prop-file
        int port = Util.getClientRMIPort();

        // Setup Registry
        l.info("Set up RMI registry for own client services...");
        
        boolean success = false;
        for(int i = 1; !(success) && (i <= Util.CLIENT_RMIREG_RETRY_MAX); i++)
        {            
            try 
            {
                rmiReg  = LocateRegistry.createRegistry(port);
                success = true;
                break;
            } 
            catch (RemoteException e) 
            {
                l.debug("Not able to create registry on port: " + port);
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
    }

    /**
     * Check if the Player is registered
     * 
     * If the client's state is waiting but the Backbone was reset, this will 
     * cause unjustified errors.
     * 
     * @return if p is registered
     */
    public boolean isPlayerRegistered() throws RemoteException
    {         
        boolean found = false;
        for(Player p : updatePlayerList())
        {
            found = found || p.getName().equals(me.getName());
        } 
        
        return found;
    }
    
    private boolean bind(String name)
    {
        l.debug("Try to bind own services...");
     
        try 
        {   
            rmiURI = me.getRmiURI(name);
            l.debug(me.getRmiURI(name) + "\n" + client);
            Naming.rebind(me.getRmiURI(name), client);
            l.info("Bound client methods at port " + rmiURI);
            Util.logRMIReg(rmiReg);
            me.setName(name);
        } 
        catch (RemoteException | MalformedURLException e) 
        {
            l.error("Unable to bind methods to registry.\n" + e.getMessage(), e);
            rmiURI = null;
        } 
        
        return (rmiURI != null); 
    }
    
    private void unbind()
    {
        l.debug("Unbind services...");
        try { Naming.unbind(rmiURI); } catch (Exception e) { }
                
        rmiURI = null;        
    }
    
    /**
     * Register Player at Server
     * 
     * @param name Player Name
     * @return success
     */
    public boolean registerPlayer(String name) 
    {
        boolean retry;
        int     i       = 1;
        boolean success = false;
        
        me.setName(null);
        
        // Register Player at Server
        if (bind(name)) 
        { 
            l.debug("Try to register " + me.getName());
            do
            { 
                if(i > 1) { l.info("#" + i + " Attempt..."); }
                retry = false;
                
                try 
                {
                    me = server.register(me.getName(), me.getPort());
                    l.info("Registered " + me.getName() + " at server " + serverAddr + ":" + serverPort);
                    success = true;
                } 
                catch (AlcatrazServerException e) 
                {
                    l.error("Server refused registration:\n" + e.getMessage());
                } 
                catch (RemoteException e) 
                {
                    l.info("Relocating server...");
                    retry = contactServer();
                }
            } while(retry);
        }
        
        if(!success)
        {
            unbind();
        }
        
        return success;
    }  
   
    /**
     * Unregister Player from Server
     * 
     * @return success
     */
    public boolean unregisterPlayer() 
    {
        boolean retry;
        int     i       = 1;
        boolean success = false;

        l.info("Try to remove player methods from registry");
        unbind();

        // Unregister Player at Server
        l.debug("Try to unregister " + me.getName());
        
        do
        {
            retry = false;
            if(i > 1) { l.info("#" + i + " Attempt..."); }
            
            try 
            {
                try
                {
                    if(!success)
                    {
                        server.deregister(me.getName());
                        success = true;
                    }
                    updatePlayerList();
                }   
                catch (AlcatrazServerException e) 
                {               
                    if(isPlayerRegistered())
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
                l.info("Relocating server...");
                retry = contactServer();
            }
        } while(retry);
            
        if(success)
        {
            l.info("Unregistered " + me.getName() + " from server " + serverAddr + ":" + serverPort);
            me.setName(null);
        }
        
        return success;
    }

    /**
     * Set Player State
     * 
     * @param state Ready State
     * @return success
     */
    public boolean setReady(boolean state) 
    {
        boolean retry;
        int     i       = 1;
        boolean success = false;
        l.debug("Try to update ready state of " + me.getName() + " to " + state);
        
        do
        {
            retry = false;
            if(i > 1) { l.info("#" + i + " Attempt..."); }
            
            try
            {
                try
                {
                    server.setStatus(me.getName(), state);
                    success = true;
                } 
                catch (AlcatrazServerException e) 
                {    
                    if(!isPlayerRegistered())
                    {
                        l.debug("Client statemachine fixed");
                        l.error("Session expired. Please re-register!");
                        success = true;
                    }

                    if(!success)
                    {
                        l.error("Server refused to set ready status:\n" + e.getMessage());  
                    }
                } 
            }
            catch (RemoteException e) 
            {
                l.info("Relocating server...");
                retry = contactServer();
            }
        } while(retry);
        
        return success;
    }
    
    /**
     * Get PlayerList from Server
     * 
     * This function is used if the Player is not registered yet.
     * 
     * @return PlayerList
     * @throws RemoteException 
     */
    private LinkedList<Player> updatePlayerList() throws RemoteException
    {
        updatePlayerList(server.getPlayerList());  
        return this.playerList;
    }
    
    private void updatePlayerList(LinkedList<Player> playerList)
    {
        StringBuilder sb = new StringBuilder();
        int numPlayerReady = PlayerList.numPlayerReady(playerList);            
        
        sb.append(playerList.size()).append(" Player");
        sb.append(playerList.size() >  1 ? "s"            : ""      ).append(" joined (");
        sb.append(numPlayerReady    >  0 ? numPlayerReady : "Nobody").append(" ");
        sb.append(numPlayerReady    <= 1 ? "is"           : "are"   ).append(" ready)");
        
        l.info(sb.toString());
        
        this.playerList = playerList;
        gui.updatePlayerList(this.playerList);  
    }

    /**
     * Process PlayerList received from Server
     * 
     * This function is used if the Player is registered.
     * The server will send updates to registered players whenever they occure.
     * 
     * The gameStarted Parameter is needed for a coordinated start of the game
     * 
     * @param playerList
     * @param gameStarted
     * @throws AlcatrazInitGameException 
     */
    public void processPlayerList(LinkedList<Player> playerList, boolean gameStarted) throws AlcatrazInitGameException
    {
        boolean onTheList  = false;
        int numPlayerReady = 0;
        
        gui.lock(true);
        l.info("Processing Playerlist...");
        
        for (Player p : playerList) 
        {  
            //find own entry in list to get ID given by server
            if (p.getName().equals(me.getName())) 
            {
                onTheList = true;
                me = p;
            }
            
            // count how many players are ready
            if (p.isReady()) 
            {
                numPlayerReady++;
            }
        }
                
        // Store & Display new player list
        updatePlayerList(playerList);

        if(!onTheList)
        {
            assert(false);
        }
        
        // if all players are ready 
        // AND there are at least 2 players
        // start the game 
        if ((numPlayerReady == playerList.size()) && (numPlayerReady >= Util.NUM_MIN_PLAYER)) 
        {   
            int i  = 1;
            l.info("Starting the game...");
            
            do
            {
                if(i > 1) { l.info("#" + i + " Attempt..."); }
            
                if(initGame(numPlayerReady))
                {
                    l.info("Game ready!");
                    startGame();    
                    break;
                }
                else
                {
                    if(!gameStarted)
                    {
                        AlcatrazInitGameException e = new AlcatrazInitGameException("Game start failed!");
                        l.warn(e.getMessage());
                        gui.lock(false);
                        throw e;
                    }
                }
                
                Thread.yield();
                
            } while(true);
        }
        
        gui.lock(false);
    }

    public boolean isInGame() {
        return inGame;
    }
    
    private boolean initGame(int numPlayerReady)
    {
        PlayerList lostPlayer = retreivePlayerProxys();
        int        numPlayer  = numPlayerReady - lostPlayer.count();
        
        return (numPlayer == numPlayerReady);
    }
    
    private void startGame()
    {
        inGame = true;  // Lock RMI interface
        
        actrz  = new Alcatraz();
        actrz.init(playerList.size(), me.getId());
                
        for (Player p : playerList) 
        {
            actrz.getPlayer(p.getId()).setName(p.getName());
        }

        gui.setVisible(false);
        
        actrz.getWindow().setTitle(actrz.getWindow().getTitle() + " - " + me.getName());
        actrz.getWindow().setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        actrz.getWindow().addWindowListener(exitListener);

        Util.centerFrame(actrz.getWindow());
        actrz.showWindow();

        actrz.addMoveListener(this);

        actrz.start();
    }

    private PlayerList retreivePlayerProxys() 
    {
        int numPlayerReady = 0;
        PlayerList lostPlayer = new PlayerList();
               
        for(Iterator<Player> i = playerList.iterator(); i.hasNext(); )
        {
            Player player   = i.next();
                        
            if (player.equals(me)) 
            {
                numPlayerReady++;
                continue;
            }
            
            try 
            {
                String rmi_uri = Util.buildRMIString(player.getAddress(), player.getPort(), Util.getClientRMIPath(), player.getName());
                l.info("Obtaining proxy from Player '" + player.getName() + "':\n" + rmi_uri);
                player.setProxy((IClient) Naming.lookup(rmi_uri));
                player.getProxy().isAlive();
                numPlayerReady++;   
            } 
            catch (NotBoundException | MalformedURLException | RemoteException e) 
            {
                l.debug("Contacting player '" + player.getName() + "' failed:\n" + e.getMessage());
                lostPlayer.add(player);
                i.remove();
            }
        }
        
        l.debug("Number of ready player: " + numPlayerReady);
                
        return lostPlayer;
    }

    public void processMove(Move m) 
    {
        l.debug("Received a move from player '" + m.getPlayer().getName() + "'");
        l.trace(m.toString());
        actrz.doMove(m.getPlayer(), m.getPrisoner(), m.getRowOrCol(), m.getRow(), m.getCol());
    }

    @Override
    public void moveDone(at.falb.games.alcatraz.api.Player player, Prisoner prisoner, int rowOrCol, int row, int col) 
    {  
        l.debug("Player '" + player.getName() + "' did a move");
        Move move = new Move(player, prisoner, rowOrCol, row, col);
        l.trace(move.toString());

        for (Player p : playerList) 
        {
            if (p.equals(me)) 
            {
                continue;
            }

            int i = 1;
            l.debug("Sending move to Player '" + p.getName() + "'");
            
            do // Send move 
            {
                if(i > 1) { l.info("#" + i + " Attempt..."); }
                
                try
                {
                    do // Check target player state machine
                    {
                        try 
                        {
                            p.getProxy().doMove(move);
                            break;
                        } 
                        catch (AlcatrazClientStateException e) 
                        {
                            try 
                            {
                                l.debug("Fix statemachine from player '" + p.getName() + "'");
                                p.getProxy().updatePlayerList(playerList, true); // inGame == true
                            } 
                            catch (AlcatrazInitGameException ex) 
                            {
                                assert false : "PlayerList was sent with Parameter inGame=true";
                            }
                            catch (AlcatrazClientStateException ex) 
                            {
                                assert false : "Player statemachine must be inGame or not";
                            }
                        }
                        
                    } while(true);
                    break;
                }
                catch(RemoteException e) 
                {
                    l.warn(p.getAddress() + ":" + p.getPort(), e);
                }
                
                Thread.yield();
            } while (true);
        }
    }

    @Override
    public void gameWon(at.falb.games.alcatraz.api.Player player) 
    { 
        inGame = false;
        l.info(player.getName() + " has won the game");        

        actrz.closeWindow();
        actrz.disposeWindow();
        
        gui.setVisible(true);
        Util.warnUser(gui, (player.getName() + " has won the game"));
        
        playerList = new LinkedList();
        gui.updatePlayerList(playerList);
        gui.reset();
    }
    
    public void gameExit()
    {
        if(Util.yesnocancel(actrz.getWindow(), "Do you really want to quit the game?") == JOptionPane.YES_OPTION)
        {
            System.exit(0);
        }
    }
}
