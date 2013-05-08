/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.common;

import at.technikum.bicss.sam.b6.alcatraz.common.exception.AlcatrazNotMasterException;
import at.technikum.bicss.sam.b6.alcatraz.common.exception.AlcatrazServerException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 *
 * @author
 */
public interface IServer extends Remote 
{
    public LinkedList<Player> getPlayerList()                        throws RemoteException;
    public Player             register  (String name, int port)      throws RemoteException, AlcatrazServerException, AlcatrazNotMasterException;
    public void               deregister(String name)                throws RemoteException, AlcatrazServerException, AlcatrazNotMasterException;
    public void               setStatus (String name, boolean ready) throws RemoteException, AlcatrazServerException, AlcatrazNotMasterException;
    public String             getMasterServer()                      throws RemoteException;
}
