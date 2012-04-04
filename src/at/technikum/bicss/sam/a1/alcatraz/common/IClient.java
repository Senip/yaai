/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

/**
 *
 * @author Rudolf Galler <ic10b039@technikum-wien.at> [1010258039]
 */
public interface IClient extends Remote{
    public void updatePlayerList(LinkedList<Player> pl) throws RemoteException;
    public void doMove (String name, Move m) throws RemoteException;
    
}
