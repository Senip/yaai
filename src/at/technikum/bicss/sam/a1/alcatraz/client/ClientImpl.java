/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.client;

import at.falb.games.alcatraz.impl.Move;
import at.technikum.bicss.sam.a1.alcatraz.common.IClient;
import at.technikum.bicss.sam.a1.alcatraz.common.Player;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

/**
 *
 * @author Rudolf Galler <ic10b039@technikum-wien.at> [1010258039]
 */
public class ClientImpl extends UnicastRemoteObject implements IClient {

    public ClientImpl() throws RemoteException {
        super();
    }

    @Override
    public void updatePlayerList(LinkedList<Player> pl) throws RemoteException {
        System.out.println("\nUpdate of Playerlist");
        System.out.println(pl.toString());
    }

    @Override
    public void sendMove(String name, Move m) throws RemoteException {
    }
}
