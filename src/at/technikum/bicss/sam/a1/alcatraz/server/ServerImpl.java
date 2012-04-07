/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.server;

import at.technikum.bicss.sam.a1.alcatraz.common.IClient;
import at.technikum.bicss.sam.a1.alcatraz.common.IServer;
import at.technikum.bicss.sam.a1.alcatraz.common.Player;
import at.technikum.bicss.sam.a1.alcatraz.common.Util;
import at.technikum.bicss.sam.a1.alcatraz.server.spread.PlayerList;
import at.technikum.bicss.sam.a1.alcatraz.server.spread.SpreadServer;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

/**
 *
 * @author Rudolf Galler <ic10b039@technikum-wien.at> [1010258039]
 */
public class ServerImpl extends UnicastRemoteObject implements IServer {

    private PlayerList player_list;
    private SpreadServer spread_server;

    public ServerImpl() throws RemoteException {
        super();
        // create initial spread server instance
        spread_server = SpreadServer.getInstance();
        player_list = spread_server.getPlayerList();

    }

    private void broadcastPlayerList() {
        player_list.renumberIDs();
        for (Player p : player_list) {
            String rmi_uri = Util.buildRMIString(p.getAddress(), p.getPort(), 
                    Util.getClientRMIPath(), p.getName());
            try {
                IClient c = (IClient) Naming.lookup(rmi_uri);
                c.updatePlayerList(player_list.getLinkedList());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void register(String name, String address, int port) throws RemoteException {
        Player newPlayer = new Player(name, 0, address, port, false);
        player_list.add(newPlayer);

        System.out.println("\nSERVER: Registered new Player:\n" + newPlayer.toString());
        broadcastPlayerList();
    }

    public void deregister(String name) throws RemoteException {
        Player p_remove = null;
        for (Player p : player_list) {
            if (p.getName().equals(name)) {
                p_remove = p;
            }
        }
        player_list.remove(p_remove);
        broadcastPlayerList();
    }

    public void setStatus(String name, boolean ready) throws RemoteException {
        for (Player p : player_list) {
            if (p.getName().equals(name)) {
                p.setReady(ready);
            }
        }
        broadcastPlayerList();
    }

    @Override
    public String getMasterServer() throws RemoteException {
        return spread_server.getMasterServerAddress();
    }
}
