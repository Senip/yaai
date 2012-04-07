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
public class ClientImpl extends UnicastRemoteObject implements IClient {
    ClientHost hosthandle = null;
    
    public ClientImpl(ClientHost host) throws RemoteException {
        super();
        hosthandle = host;
    }

    @Override
    public void updatePlayerList(LinkedList<Player> pl) throws RemoteException {
        System.out.println("\nUpdate of Playerlist");
        System.out.println(pl.toString());
        hosthandle.processPlayerList(pl);
    }

    @Override
    public void doMove(Move m) throws RemoteException {
        System.out.println("\nreceived Move");
        hosthandle.processMove(m);
        
    }
}
