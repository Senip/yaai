/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.client;

import at.technikum.bicss.sam.b6.alcatraz.common.IClient;
import at.technikum.bicss.sam.b6.alcatraz.common.Move;
import at.technikum.bicss.sam.b6.alcatraz.common.Player;
import at.technikum.bicss.sam.b6.alcatraz.common.Util;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import org.apache.log4j.Logger;

/**
 *
 * @author
 */
public class ClientRMI extends UnicastRemoteObject implements IClient 
{
    private ClientHost hosthandle = null;
    private static Logger l       = Util.getLogger();
    
    public ClientRMI(ClientHost host) throws RemoteException 
    {
        super();
        hosthandle = host;
    }

    public ClientHost getHosthandle() {
        return hosthandle;
    }

    public void setHosthandle(ClientHost hosthandle) {
        this.hosthandle = hosthandle;
    }

    @Override
    public synchronized void updatePlayerList(LinkedList<Player> pl) throws RemoteException 
    {            
        if(!hosthandle.isInGame()) // Ignore if inGame
        {
            hosthandle.processPlayerList(pl);
        }
        else
        {
            l.warn("Received playerlist although already in game!");
        }
    }

    @Override
    public synchronized void doMove(Move m) throws RemoteException 
    {        
        if(hosthandle.isInGame()) // Ignore if not inGame
        {
            hosthandle.processMove(m);        
        }
        else
        {
            l.warn("Received move although not in game!");
        }
    }
}
