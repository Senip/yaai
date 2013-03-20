/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.client;

import at.technikum.bicss.sam.b6.alcatraz.common.IClient;
import at.technikum.bicss.sam.b6.alcatraz.common.Move;
import at.technikum.bicss.sam.b6.alcatraz.common.Player;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

/**
 *
 * @author
 */
public class ClientRMI extends UnicastRemoteObject implements IClient 
{
    ClientHost hosthandle = null;
    
    public ClientRMI(ClientHost host) throws RemoteException 
    {
        super();
        hosthandle = host;
    }

    @Override
    public synchronized void updatePlayerList(LinkedList<Player> pl) throws RemoteException 
    {
        hosthandle.processPlayerList(pl);
    }

    @Override
    public synchronized void doMove(Move m) throws RemoteException 
    {
        hosthandle.processMove(m);        
    }
}
