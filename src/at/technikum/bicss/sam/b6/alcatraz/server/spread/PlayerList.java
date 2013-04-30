/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.server.spread;

import at.technikum.bicss.sam.b6.alcatraz.common.Player;
import at.technikum.bicss.sam.b6.alcatraz.server.spread.events.ObjectChangedEvent;
import at.technikum.bicss.sam.b6.alcatraz.server.spread.events.ObjectChangedListner;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author
 */
public class PlayerList implements Serializable, Iterable<Player> 
{

    private LinkedList<Player> playerList = new LinkedList();
    
    // add event listners to avoid nested instance mess
    private List event_listeners = new ArrayList();

    // event methods
    public synchronized void addObjectChangedListner(ObjectChangedListner l) 
    {
        event_listeners.add(l);
    }
    
    public synchronized void removeObjectChangedListner(ObjectChangedListner l) 
    {
        try { event_listeners.remove(l); } catch(Exception e) { }
    }

    public synchronized void triggerObjectChangedEvent() 
    {
        ObjectChangedEvent event = new ObjectChangedEvent(this);
        Iterator listeners = event_listeners.iterator();
        
        while (listeners.hasNext()) 
        {
            ((ObjectChangedListner) listeners.next()).updateObject(event);
        }
    }

    @Override
    public Iterator iterator() 
    {
        return playerList.iterator();
    }

    // wrapper for add on linkedlist
    public void add(Player p) 
    {
        playerList.add(p);
        // fire changed event
        triggerObjectChangedEvent();
    }

    // wrapper for remove on linkedlist
    public void remove(Player p) 
    {
        playerList.remove(p);
        // fire changed event
        triggerObjectChangedEvent();
    }

    public LinkedList<Player> getLinkedList() 
    {
        return playerList;
    }

    public void setLinkedList(LinkedList<Player> ll) 
    {
        playerList = ll;
    }

    public void renumberIDs() 
    {
        Iterator<Player> it = playerList.iterator();
        
        int ctr = 0;
        for (ctr = 0; it.hasNext(); ctr++) 
        {
            it.next().setId(ctr);
        }
    }

    public boolean allReady() 
    {
        boolean ready = false;
        int ctr = 0;
        
        for (Player p : playerList) 
        {
            // count how many players are ready
            if (p.isReady()) 
            {
                ctr++;
            }
        }
        
        if ((ctr == playerList.size())) 
        {
            ready = true;
        }
        return ready;
    }
    
    public int count()
    {
        return playerList.size();
    }
    
    public Player getPlayerByName(String name)
    {
        for (Player p : playerList) 
        {
            if (p.getName().equals(name)) 
            {
                return p;
            }
        }
        
        return null;
    }
}
