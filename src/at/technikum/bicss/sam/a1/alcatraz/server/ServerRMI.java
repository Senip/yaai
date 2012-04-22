/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.server;

import at.technikum.bicss.sam.a1.alcatraz.common.*;
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
public class ServerRMI extends UnicastRemoteObject implements IServer {

    private PlayerList player_list;
    private SpreadServer spread_server;

    public ServerRMI() throws RemoteException {
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
    public synchronized void register(String name, String address, int port) throws RemoteException, AlcatrazServerException {
        for (Player p : player_list) {
            if (p.getName().equals(name)) {
                throw new AlcatrazServerException("Player with name " + name
                        + " already registered.\nName must be unique, "
                        + "please us a different name.");
            }
        }
        if (player_list.getLinkedList().size() >= 4) {
            throw new AlcatrazServerException("This game is already full! "
                    + "(max. 4 Players)\nPlease try some time later.");
        }

        Player newPlayer = new Player(name, 0, address, port, false);
        player_list.add(newPlayer);

        System.out.println("\nSERVER: Registered new Player:\n" + newPlayer.toString());
        broadcastPlayerList();
    }

    @Override
    public synchronized void deregister(String name) throws RemoteException, AlcatrazServerException {
        Player p_remove = null;
        for (Player p : player_list) {
            if (p.getName().equals(name)) {
                p_remove = p;
            }
        }

        if (p_remove == null) {
            throw new AlcatrazServerException("Playername " + name
                    + " not found!");
        } else {
            player_list.remove(p_remove);
            broadcastPlayerList();
        }
    }

    @Override
    public synchronized void setStatus(String name, boolean ready) throws RemoteException, AlcatrazServerException {
        Player p_status = null;
        for (Player p : player_list) {
            if (p.getName().equals(name)) {
                p_status = p;
            }
        }

        if (p_status == null) {
            throw new AlcatrazServerException("Playername " + name
                    + " not found!");
        } else {
            p_status.setReady(ready);
            broadcastPlayerList();
            if (player_list.allReady()) {
                spread_server.setPlayerList(new LinkedList());
                player_list = spread_server.getPlayerList();
                player_list.triggeraddObjectChangedEvent();
            }
            player_list.triggeraddObjectChangedEvent();

        }
    }

    @Override
    public String getMasterServer() throws RemoteException {
        return spread_server.getMasterServerAddress();
    }
}
