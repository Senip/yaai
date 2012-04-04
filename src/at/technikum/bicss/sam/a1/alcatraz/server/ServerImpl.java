/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.server;

import at.technikum.bicss.sam.a1.alcatraz.common.IClient;
import at.technikum.bicss.sam.a1.alcatraz.common.IServer;
import at.technikum.bicss.sam.a1.alcatraz.common.Player;
import at.technikum.bicss.sam.a1.alcatraz.server.spread.PlayerList;
import at.technikum.bicss.sam.a1.alcatraz.server.spread.SpreadServer;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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
        String rmi_adr = null;
        renumberIDs(player_list);
        for (Player p : player_list) {
            rmi_adr = new String("rmi://" + p.getAddress() + ":" + p.getPort() + "/Alcatraz/ClientImpl/" + p.getName());
            try {
                IClient c = (IClient) Naming.lookup(rmi_adr);
                c.updatePlayerList(player_list.getLinkedList());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void renumberIDs(PlayerList pl) {
        //renumber IDs if there was a change in playerlist
        for (Player p : player_list) {
            p.setId(player_list.getLinkedList().indexOf(p));
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
