/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 *
 * @author 
 */
public interface IClient extends Remote
{   
    
    //public void announce(String address, int port)      throws RemoteException;

    public void updatePlayerList(LinkedList<Player> pl) throws RemoteException;

    //public void myTurn()                                throws RemoteException;
    public void doMove(Move m)                          throws RemoteException;

    //public void isOffline()                             throws RemoteException;
    //public void isAlive()                               throws RemoteException;

    
}
