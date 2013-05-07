/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.server.spread;

import at.technikum.bicss.sam.b6.alcatraz.common.Player;
import at.technikum.bicss.sam.b6.alcatraz.common.Util;
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
    private int numPlayerReady            = -1;
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
    public void add(Player player) 
    {
        playerList.add(player);
        numPlayerReady = -1;
        // fire changed event
        triggerObjectChangedEvent();
    }

    // wrapper for remove on linkedlist
    public void remove(Player player) 
    {
        playerList.remove(player);
        numPlayerReady = -1;
        // fire changed event
        triggerObjectChangedEvent();
    }

    public LinkedList<Player> getLinkedList() 
    {
        return playerList;
    }

    public void setLinkedList(LinkedList<Player> playerList) 
    {
        this.playerList = playerList;
        numPlayerReady  = -1;
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

    public boolean gameReady() 
    {
        return ((count() == numPlayerReady()) &&
                (count() >= Util.NUM_MIN_PLAYER));
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
    
    public static int numPlayerReady(LinkedList<Player> playerList)
    {
        int numPlayerReady = 0; 
        
        for (Player p : playerList) if(p.isReady()) numPlayerReady++; 
        
        return numPlayerReady;     
    }
    
    public int numPlayerReady()
    {
        if(numPlayerReady < 0)
        {
            numPlayerReady = numPlayerReady(playerList);
        }
        
        return numPlayerReady;       
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        for(Player player : playerList)
        {
            sb.append(player.toString()).append("\n");
        }
        
        return sb.toString();
    }
}
