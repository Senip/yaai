/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.common;

import java.io.Serializable;

/**
 *
 * @author 
 */
public class Player extends at.falb.games.alcatraz.api.Player implements Serializable 
{
    private String  UUID;
    private String  address;
    private int     port;
    private boolean ready;
    private IClient proxy;

    public Player(String name, int id, String address, int port, boolean ready) 
    {
        super(id);
        super.setName(name);        
        this.address = address;
        this.port    = port;
        this.ready   = ready;
        this.proxy   = null;
    }       

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public IClient getProxy() {
        return proxy;
    }

    public void setProxy(IClient proxy) {
        this.proxy = proxy;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }
       
    @Override
    public String toString() 
    {
        return "Player{" + "name=" + super.getName() + ", id=" + super.getId() 
                + ", address=" + address + ", port=" + port 
                + ", ready=" + ready + '}';
    }
        
}
