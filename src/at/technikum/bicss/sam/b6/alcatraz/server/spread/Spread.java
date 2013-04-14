/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.server.spread;

import at.technikum.bicss.sam.b6.alcatraz.common.Util;
import java.net.ConnectException;
import org.apache.log4j.Logger;
import spread.SpreadException;

/**
 * SpreadServer singleton instance Wrapper
 * 
 * @author 
 */
public class Spread 
{   
    // Singleton Instance
    private static SpreadServer instance = null;
    
    // Logger    
    private static Logger l = Util.getLogger();
    
    public static SpreadServer open()
    {
        try 
        {
            instance = new SpreadServer(SpreadServer.getAddr(Util.getSpreadServerAddr()),
                                        SpreadServer.getPort(Util.getSpreadServerPort()),
                                        Util.getUniqueName(Util.getSpreadGroupName()),
                                        Util.getSpreadGroupName());
        } 
        catch (SpreadException e) 
        {
            l.fatal("SPREAD: Unable to connect to Server " + 
                    Util.getSpreadServerAddr() + ":" + Util.getSpreadServerPort());
            System.exit(1);
        }    
        
        return instance;
    }
    
    public static void sync()
    {
        instance.sync();
    }
    
    public static SpreadServer server()
    {        
        // create singelton if there is no instance yet
        if (instance == null) 
        {
            instance = open();
        }
        
        if(!instance.isSynced()) 
        {
            sync();
        }
        
        return instance;
    }
    
    public static SpreadServer close()
    {
        instance.close();
        instance = null;
        return null;
    }
}
