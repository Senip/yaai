/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.server;

import at.technikum.bicss.sam.b6.alcatraz.common.IServer;
import at.technikum.bicss.sam.b6.alcatraz.common.Util;
import at.technikum.bicss.sam.b6.alcatraz.server.spread.Spread;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *  Alcatraz Registration Server
 * 
 * The server is a multi-threaded application, therefore synchronization is necessary.
 * 
 *                                 Main
 *                                  |
 *          -------------------------------------------------
 *          |                                               |
 *         RMI                                            Spread
 *          |                                               |
 *   ----------------                           ------------------------
 *   |      |       |                           |                       |
 *   Clients...                            PlayerList            MessageListener
 *                                    ObjectChangedListner
 * 
 * The stateful RMI methods are synchronized. Those are all, except getMasterServer(),
 * which is not; thus it can be accessed by many clients simultaneously.
 * 
 * Clients only connect to the master server for registration. Therefore information 
 * from RMI and Spread can't interfere.
 * 
 * Clients might only connect to a slave server to ask for the master. Therefore
 * Spread and RMI needs to be synchronized here.
 * 
 * Furthermore the server must know the master server before it offers its services.
 * Therefore Spread.Sync() is called before Naming.rebind().
 * 
 * @author 
 */
public class ServerHost 
{
    private static Logger l = null;

    public static void main(String args[]) 
    {
        IServer      server       = null;
        Registry     rmireg       = null;
        String       rmiURI       = "";
        
        PropertyConfigurator.configure(Util.readProps());
        l = Util.setLogger(Util.getServerRMIPath());
        
        int port = Util.getServerRMIPort();
        System.setProperty("java.rmi.server.hostname", Util.getMyServerAddress());  
                
        ServerUI.banner();   
        
        // 1. Connect to Spread
        Spread.open();
                
        // 2. Register Server-Services        
        l.debug("SERVER: Set up own registry...");
        
        try 
        {
            rmireg = LocateRegistry.createRegistry(port);
        } 
        catch (RemoteException e) 
        {
            l.warn("SERVER: Not able to create registry on port " + port
                    + "\n" + e.getMessage());
            l.debug("SERVER: try to search for active registry");
        }
        
        try 
        {
            rmireg = LocateRegistry.getRegistry(port);
            l.info("SERVER: Got registry on port " + port);
        } 
        catch (RemoteException e) 
        {
            l.fatal("SERVER: Unable to set up RMI registry\n" + e.getMessage());
            Spread.close();
            System.exit(1);
        }

        // 2. Bind
        l.info("SERVER: Bind...");
        
        rmiURI = Util.buildRMIString(Util.getMyServerAddress(),
                 Util.getServerRMIPort(), Util.getServerRMIPath());
                    
        // 2.1 Check for running Alcatraz Server        
        boolean success = true;
        try 
        {  
            server = (IServer) Naming.lookup(rmiURI);
        } 
        catch (Exception e) 
        {
            success = false;
        }
        
        if(success)
        {
            l.warn("SERVER: An instance is allready bound to this machine.");
            l.info("SERVER: This instance terminates now!");
            System.exit(0);
        }
        
        // 2.2 Synchronize with Spread
        Spread.sync();
        
        // 2.3 Bind (now really)
        try 
        {
            server = new ServerRMI(Spread.server());
            
            Naming.rebind(rmiURI, server);
            Util.logRMIReg(rmireg);
        } 
        catch (RemoteException | MalformedURLException e) 
        {
            l.fatal("SERVER: Unable to bind services\n" + e.getMessage(), e);
            Spread.close();
            System.exit(1);
        }
        
        l.info("SERVER: Alcatraz running...");
        
        // 3. Run the ServerUI until the server crashes or is stopped by the user
        ServerUI.run();
        
        // 4. Clean up
        try { UnicastRemoteObject.unexportObject(server, false); } catch (Exception e) { }
        try { Naming.unbind(rmiURI);                             } catch (Exception e) { }
        try { UnicastRemoteObject.unexportObject(server, true);  } catch (Exception e) { }
        Spread.close();
               
        l.info("SERVER: Terminated.");
        System.exit(0);
    }
}
