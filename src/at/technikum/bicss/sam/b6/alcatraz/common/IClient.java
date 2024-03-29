/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.common;

import at.technikum.bicss.sam.b6.alcatraz.common.exception.AlcatrazInitGameException;
import at.technikum.bicss.sam.b6.alcatraz.common.exception.AlcatrazClientStateException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 *
 * @author 
 */
public interface IClient extends Remote
{
    public void updatePlayerList(LinkedList<Player> playerList, boolean inGame) 
            throws RemoteException, AlcatrazInitGameException, AlcatrazClientStateException;

    public void doMove(Move m)                          
            throws RemoteException, AlcatrazClientStateException;
    
    public void isAlive()
            throws RemoteException;
    
}
