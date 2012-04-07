/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import at.technikum.bicss.sam.a1.alcatraz.common.Player;
import at.technikum.bicss.sam.a1.alcatraz.common.Util;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Rudolf Galler <ic10b039@technikum-wien.at> [1010258039]
 */
public class log4j_test {
    static Logger l = null;

    public static void main(String args[]) {
        
        //BasicConfigurator.configure();
        Util.readProps();  
        l = Logger.getLogger(Util.getClientRMIPath());
        PropertyConfigurator.configure(Util.getProps());
        
        Player p1 = new Player("Player1", 0, "0.0.0.0", 0, false);
        Player p2 = new Player("Player2", 1, "0.0.0.1", 0, false);
        Player p3 = new Player("Player3", 1, "0.0.0.3", 0, false);
        p2.setId(12);
        
        l.info(p1.toString());
        l.info(p2.toString());
    }
}
