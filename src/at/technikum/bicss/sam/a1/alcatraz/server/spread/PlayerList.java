/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.server.spread;

import at.technikum.bicss.sam.a1.alcatraz.common.Player;
import java.io.Serializable;
import java.util.Iterator;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 *
 * @author Gabriel Pendl
 * LinkedList with Spread support
 */
public class PlayerList implements Serializable, Iterable<Player>{
    
    private LinkedList<Player> player_list = new LinkedList();
    private SpreadServer spread_server;
   
    public PlayerList(SpreadServer sp) {
        spread_server = sp;
    }
    
    @Override
    public Iterator iterator() {
        return player_list.iterator();
    }

    public void add(Player p){
        player_list.add(p);
        spread_server.multicastPlayerListToGroup(player_list);
   
    }
    
    
    public LinkedList<Player> getLinkedList(){
        return player_list;
    }




}
