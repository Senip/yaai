/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.common;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 *
 * @author 
 */
public class Player extends at.falb.games.alcatraz.api.Player implements Serializable 
{
    private String  address;
    private int     port;
    private boolean ready;
    private IClient proxy;
    private String  rmiURI;

    public Player(String name, int id, String address, int port, boolean ready) 
    {
        super(id);
        super.setName(name);        
        this.address = address;
        this.port    = port;
        this.ready   = ready;
        this.proxy   = null;
    }       

    public String getAddress() 
    {
        return address;
    }

    public void setAddress(String address) 
    {
        this.address = address;
        this.rmiURI  = null;
        this.proxy   = null;
    }

    public int getPort() 
    {
        return port;
    }

    public void setPort(int port) 
    {
        this.port    = port;
        this.rmiURI  = null;
        this.proxy   = null;
    }
    
    public void setProxy(IClient proxy) 
    {
        this.proxy = proxy;
    }
    
    public IClient getProxy() 
    {
        return proxy;
    }
     
    public String getRmiURI()
    {
        if(this.rmiURI == null)
        {
            this.rmiURI = Util.buildRMIString(this.address, this.port,
                          Util.getClientRMIPath(), super.getName());
        }
        
        return this.rmiURI;
    }
    
    public String getRmiURI(String name)
    {
        return this.rmiURI = Util.buildRMIString(this.address, this.port,
                             Util.getClientRMIPath(), name);
    }
              
    public boolean isReady() 
    {
        return ready;
    }

    public void setReady(boolean ready) 
    {
        this.ready = ready;
    }
    
    @Override
    public String toString() 
    {
        return "Player{" + "name=" + super.getName() + ", id=" + super.getId() 
                + ", address=" + address + ", port=" + port 
                + ", ready=" + ready + '}';
    }
        
}
