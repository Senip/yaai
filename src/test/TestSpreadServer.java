/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import at.technikum.bicss.sam.a1.alcatraz.server.spread.PlayerList;
import at.technikum.bicss.sam.a1.alcatraz.server.spread.SpreadServer;
import at.technikum.bicss.sam.a1.alcatraz.common.Player;
/**
 *
 * @author Gabriel Pendl | ic10b026@technikum-wean.at
 */
public class TestSpreadServer {

        public static void main(String[] args) {
            SpreadServer spread_server = SpreadServer.getInstance();
            PlayerList player_list = spread_server.getPlayerList();
            
            player_list.add(new Player("gabriel", 1, "192.168.1.106", 1099, true));
            
            
        
        }
}
