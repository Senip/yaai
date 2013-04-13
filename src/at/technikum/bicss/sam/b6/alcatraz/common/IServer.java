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
public interface IServer extends Remote 
{
    public LinkedList<Player> getPlayerList()                                   throws RemoteException;
    public void               register  (String name, String address, int port) throws RemoteException, AlcatrazServerException;
    public void               deregister(String name)                           throws RemoteException, AlcatrazServerException;
    public void               setStatus (String name, boolean ready)            throws RemoteException, AlcatrazServerException;
    public String             getMasterServer()                                 throws RemoteException;
}
