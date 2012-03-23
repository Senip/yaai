/*
 * nur mal checken ob das mit der spread-lib funktioniert
 * http://www.spread.org/docs/javadocs/java.html
 */
package test;

import java.net.InetAddress;
import spread.*;

/**
 *
 * @author Rudolf Galler <ic10b039@technikum-wien.at> [1010258039]
 */
public class TestSpread {

    public static void main(String[] args) {

        try {
            SpreadConnection connection = new SpreadConnection();
            connection.connect(InetAddress.getByName("daemon.address.com"), 0, "privatename", false, false);

            SpreadGroup group = new SpreadGroup();
            group.join(connection, "group");


            group.leave();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
