/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.client;

import at.technikum.bicss.sam.a1.alcatraz.common.IClient;
import at.technikum.bicss.sam.a1.alcatraz.common.Move;
import at.technikum.bicss.sam.a1.alcatraz.common.Player;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

/**
 *
 * @author Rudolf Galler <ic10b039@technikum-wien.at> [1010258039]
 */
public class ClientRMI extends UnicastRemoteObject implements IClient {
    ClientHost hosthandle = null;
    
    public ClientRMI(ClientHost host) throws RemoteException {
        super();
        hosthandle = host;
    }

    @Override
    public synchronized void updatePlayerList(LinkedList<Player> pl) throws RemoteException {
        hosthandle.processPlayerList(pl);
    }

    @Override
    public synchronized void doMove(Move m) throws RemoteException {
        hosthandle.processMove(m);        
    }
}
