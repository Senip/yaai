/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.server.spread;

import at.technikum.bicss.sam.a1.alcatraz.common.Player;
import at.technikum.bicss.sam.a1.alcatraz.server.spread.events.ObjectChangedListner;
import at.technikum.bicss.sam.a1.alcatraz.server.spread.events.ObjectChangedEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Gabriel Pendl
 * LinkedList with Spread support
 */
public class PlayerList implements Serializable, Iterable<Player>{
        
    private LinkedList<Player> player_list = new LinkedList();
    
    // add event listners to avoid nested instance mess
    private List event_listeners = new ArrayList();
   
    // event methods
    public synchronized void addObjectChangedListner(ObjectChangedListner l){
        event_listeners.add(l); 
    } 
    
    public synchronized void triggeraddObjectChangedEvent(){
        ObjectChangedEvent event = new ObjectChangedEvent(this);
        Iterator listeners = event_listeners.iterator();
        while(listeners.hasNext()){
            ((ObjectChangedListner) listeners.next()).updateObject(event);
        }
    }
       
    
    @Override
    public Iterator iterator() {
        return player_list.iterator();
    }

    // wrapper for add linkedlist add method
    public void add(Player p){
        player_list.add(p);
        System.out.print("player add");
        // fire changed event
        triggeraddObjectChangedEvent();   
    }
    
    public LinkedList<Player> getLinkedList(){
        return player_list;
    }

    public void setLinkedList(LinkedList<Player> ll) {
        player_list = ll;
    }

}
