/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.server.spread.events;

import java.util.EventObject;

/**
 *
 * @author 
 */
public class ObjectChangedEvent extends EventObject 
{    
    public ObjectChangedEvent(Object source) 
    {
        super(source);
    }
}
