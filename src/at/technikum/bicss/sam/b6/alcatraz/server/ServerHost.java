/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.server;

import at.technikum.bicss.sam.b6.alcatraz.common.IServer;
import at.technikum.bicss.sam.b6.alcatraz.common.Util;
import at.technikum.bicss.sam.b6.alcatraz.server.spread.PlayerList;
import at.technikum.bicss.sam.b6.alcatraz.server.spread.SpreadServer;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author 
 */
public class ServerHost 
{

    static private Logger l = null;

    public static void main(String args[]) 
    {
        PropertyConfigurator.configure(Util.readProps());
        l = Logger.getLogger(Util.getClientRMIPath());
        int port = Util.getServerRMIPort();
        System.setProperty("java.rmi.server.hostname", Util.getMyServerAddress());

        /*
         *
         * Register Server-Services
         */
        Registry rmireg = null;
        
        l.debug("SERVER: Set up own registry...");
        
        try {
            rmireg = LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            l.warn("SERVER: Not able to create registry on port " + port
                    + "\n" + e.getMessage()
                    + "try to search for active registry", e);
        }
        
        try {
            rmireg = LocateRegistry.getRegistry(port);
            l.info("SERVER: Got registry on port " + port);
        } catch (RemoteException e) {
            l.error(e.getMessage(), e);
        }

        l.info("SERVER: Bind...");
        
        try {
            IServer server = new ServerRMI();
            String rmi_uri = Util.buildRMIString(Util.getMyServerAddress(),
                    Util.getServerRMIPort(), Util.getServerRMIPath());
            Naming.rebind(rmi_uri, server);
            Util.logRMIReg(rmireg);
            l.info("SERVER: Alcatraz running...");
        } catch (Exception e) {
            l.error(e.getMessage(), e);
        }
    }
}
