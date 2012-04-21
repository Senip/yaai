/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.common;

import java.io.Serializable;

/**
 *
 * @author Rudolf Galler <ic10b039@technikum-wien.at> [1010258039]
 */
public class Player extends at.falb.games.alcatraz.api.Player implements Serializable {
    private String address;
    private int port;
    private boolean ready;
    private IClient proxy;

    public Player(String name, int id, String address, int port, boolean ready) {
        super(id);
        super.setName(name);        
        this.address = address;
        this.port = port;
        this.ready = ready;
        proxy = null;
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
    
    @Override
    public String toString() {
        return "Player{" + "name=" + super.getName() + ", id=" + super.getId() 
                + ", address=" + address + ", port=" + port 
                + ", ready=" + ready + '}';
    }
        
}
