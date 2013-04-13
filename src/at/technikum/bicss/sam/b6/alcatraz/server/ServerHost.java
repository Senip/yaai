/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.server;

import at.technikum.bicss.sam.b6.alcatraz.common.Util;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author 
 */
public class ServerHost 
{
    private static Logger l = null;

    public static void main(String args[]) 
    {
        ServerRMI server = null;
        Registry  rmireg = null;
        String    rmiURI = "";
        
        ServerUI.banner();
        
        PropertyConfigurator.configure(Util.readProps());
        l = Util.setLogger(Util.getServerRMIPath());
                
        int port = Util.getServerRMIPort();
        System.setProperty("java.rmi.server.hostname", Util.getMyServerAddress());

        // Register Server-Services        
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
            System.exit(1);
        }

        l.info("SERVER: Bind...");
        
        try 
        {
            server = new ServerRMI();
            rmiURI = Util.buildRMIString(Util.getMyServerAddress(),
                     Util.getServerRMIPort(), Util.getServerRMIPath());
            
            Naming.rebind(rmiURI, server);
            Util.logRMIReg(rmireg);
        } 
        catch (RemoteException | MalformedURLException e) 
        {
            l.fatal("SERVER: Unable to bind services\n" + e.getMessage(), e);
            System.exit(1);
        }
        
        l.info("SERVER: Alcatraz running...");
        
        // Run the ServerUI until the server crashes or is stopped by the user
        ServerUI.run();
        
        // Clean up
        //try { UnicastRemoteObject.unexportObject(server, false); } catch (Exception e) { }
        //try { Naming.unbind(rmiURI);                             } catch (Exception e) { }
        //try { UnicastRemoteObject.unexportObject(server, true);  } catch (Exception e) { }
        //try { server.close();                                    } catch (Exception e) { }
               
        l.info("SERVER: Terminated.");
        System.exit(0);
    }
}
