/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.client;

import at.technikum.bicss.sam.b6.alcatraz.common.exception.AlcatrazNotMasterException;
import at.technikum.bicss.sam.b6.alcatraz.common.exception.AlcatrazServerException;
import at.technikum.bicss.sam.b6.alcatraz.common.IServer;
import at.technikum.bicss.sam.b6.alcatraz.common.Player;
import at.technikum.bicss.sam.b6.alcatraz.common.Util;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author 
 */
public abstract class ServerWrapper extends RemoteWrapper implements IServer
{
    private IServer server   = null;
    private String  addr     = null;
    private int     port     = Util.getServerRMIPort();
    private static  Logger l = Util.getLogger();
    
    // Hint: This can be done in a more elegant way using reflection 
    protected abstract boolean fatal();            
        
    @Override
    public LinkedList<Player> getPlayerList() throws RemoteException
    {
        boolean retry;
        
        do
        {            
            try                       
            { 
                return server.getPlayerList();
            } 
            catch (RemoteException e) 
            {
                retry = connect();
            }
            
            if(!retry)
            {
                retry = fatal();
            }

        } while(retry);
        
        throw new RemoteException("Permanent Error");
    }

    @Override
    public Player register(String name, int port) throws RemoteException, AlcatrazServerException 
    {
        boolean retry;
        
        do
        {            
            try                       
            { 
                return server.register(name, port);
            } 
            catch (RemoteException e) 
            {
                retry = connect();
            }
            catch (AlcatrazNotMasterException e)
            {
                retry = connect(getMasterServer());
            }
            
            if(!retry)
            {
                retry = fatal();
            }

        } while(retry);
        
        throw new RemoteException("Permanent Error");
    }

    @Override
    public void deregister(String name) throws RemoteException, AlcatrazServerException 
    {
        boolean retry;
        
        do
        {            
            try                       
            { 
                server.deregister(name);
                return;
            } 
            catch (RemoteException e) 
            {
                retry = connect();
            }
            catch (AlcatrazNotMasterException e)
            {
                retry = connect(getMasterServer());
            }
            
            if(!retry)
            {
                retry = fatal();
            }

        } while(retry);
        
        throw new RemoteException("Permanent Error");
    }

    @Override
    public void setStatus(String name, boolean ready) throws RemoteException, AlcatrazServerException 
    {
        boolean retry;
        
        do
        {            
            try                       
            { 
                server.setStatus(name, ready);
                return;
            } 
            catch (RemoteException e) 
            {
                retry = connect();
            }
            catch (AlcatrazNotMasterException e)
            {
                retry = connect(getMasterServer());
            }
            
            if(!retry)
            {
                retry = fatal();
            }

        } while(retry);
        
        throw new RemoteException("Permanent Error");
    }

    @Override
    public String getMasterServer() throws RemoteException 
    {
        boolean retry;
        
        do
        {            
            try                       
            { 
                return server.getMasterServer();
            } 
            catch (RemoteException e) 
            {
                retry = connect();
            }
            
            if(!retry)
            {
                retry = fatal();
            }

        } while(retry);
        
        throw new RemoteException("Permanent Error");
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
     * on successful completion {@code serveraddr} is set to the Alcatraz
     * master server address and {@code server) holds a reference to the remote {@link IServer}
     * object on failure both will be set to {@code null} and the application is terminated
     * 
     */
    @Override
    protected boolean connect()
    {
        return connect(null);
    }
    
    private boolean connect(String firstAddr)
    {
        l.info("Try to find server for registration process...");
        
        Socket sock;
        String rmiURI;
        SocketAddress sock_addr;
        
        addr  = null;
        
        do
        {
            boolean success       = false;
            boolean reachedServer = false;
            
            for (int i = 0; i < Util.getServerAddressList().length; ) 
            {
                // Contact first Address first (if set)...
                if(i == 0 && firstAddr != null)
                {
                    addr    = firstAddr;
                }
                else
                {
                    // ...then contact all others...
                    addr    = Util.getServerAddressList()[i++]; 
                    
                    // ...except first Address
                    if(addr.equals(firstAddr))
                    {
                        continue;
                    }
                }
                
                try 
                {                    
                    do
                    {
                        // 1. Establish a connetion to a server on the RMI-port with a socket                
                        sock      = new Socket();
                        sock_addr = new InetSocketAddress(addr, port);

                        sock.connect(sock_addr, Util.getConTimeOut());
                        l.debug("Reached server:" + addr + ":" + port);
                        sock.close();

                        // 2. Obtain reference to remote object
                        rmiURI     = Util.buildRMIString(addr, port, Util.getServerRMIPath());
                        l.debug("Lookup " + rmiURI);
                        server     = (IServer) Naming.lookup(rmiURI);

                        // 3. Ask for the master server address
                        l.debug("Asking for master registration server");
                        String masterAddr = server.getMasterServer();
                        l.debug("Master registration server at " + addr + ":" + port);

                        // At least one alcatraz server is online and responsive: 
                        reachedServer = true;

                        // 4. Retry if master server is on different address:
                        if(!addr.equals(masterAddr))
                        {
                            addr = masterAddr;
                            continue;
                        }

                        break;

                    } while(true);

                    l.info("Reached master server");
                    break;
                } 
                catch(NotBoundException | IOException e)  
                { 
                    l.warn("Server not reachable " + addr + ":" + port);
                    l.debug(e.getMessage());
                    addr   = null;
                    server = null;
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

        if (Util.isEmpty(addr)) 
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
}
    
