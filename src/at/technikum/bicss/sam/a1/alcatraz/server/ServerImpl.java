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
        for (Player p : player_list) {
            rmi_adr = new String("rmi://" + p.getAddress() + ":" + 1099 + "/Alcatraz/ClientImpl/" + p.getName());
            try {
                IClient c  = (IClient) Naming.lookup(rmi_adr);
                c.updatePlayerList(player_list.getLinkedList());
            }
            catch (Exception e) {
                e.printStackTrace();
            }            
        }
    }

    @Override
    public void register(String name, String address) throws RemoteException {
        Player newPlayer = new Player(name, 0, address, false);
        player_list.add(newPlayer);
        System.out.println("\nSERVER: Registered new Player:\n" + newPlayer.toString());
        broadcastPlayerList();
    }

    @Override
    public String getMasterServer() throws RemoteException {
        return spread_server.getMasterServerAddress();
    }
}
