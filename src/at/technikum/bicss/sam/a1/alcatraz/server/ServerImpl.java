/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.server;

import at.technikum.bicss.sam.a1.alcatraz.common.IClient;
import at.technikum.bicss.sam.a1.alcatraz.common.IServer;
import at.technikum.bicss.sam.a1.alcatraz.common.Player;
import at.technikum.bicss.sam.a1.alcatraz.server.spread.PlayerList;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Rudolf Galler <ic10b039@technikum-wien.at> [1010258039]
 */
public class ServerImpl extends UnicastRemoteObject implements IServer {

    private PlayerList PlayerList;

    public ServerImpl(PlayerList pl) throws RemoteException {
        super();
        // get playerlist with already instanced spread server
        PlayerList = pl;
    }

    private void broadcastPlayerList() {
        String rmi_adr = null;
        for (Player p : PlayerList) {
            rmi_adr = new String("rmi://" + p.getAddress() + ":" + 1099 + "/Alcatraz/ClientImpl/" + p.getName());
            try {
                IClient c  = (IClient) Naming.lookup(rmi_adr);
                c.updatePlayerList(PlayerList.getLinkedList());
            }
            catch (Exception e) {
                e.printStackTrace();
            }            
        }
    }

    @Override
    public void register(String name, String address) throws RemoteException {
        Player newPlayer = new Player(name, 0, address, false);
        PlayerList.add(newPlayer);
        System.out.println("\nSERVER: Registered new Player:\n" + newPlayer.toString());
        broadcastPlayerList();
    }
}
