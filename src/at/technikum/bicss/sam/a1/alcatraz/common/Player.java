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
public class Player implements Serializable {
    private String name;
    private Integer id;
    private String address;
    private int port;
    private boolean ready;

    public Player(String name, Integer id, String address, int port, boolean ready) {
        super();
        this.name = name;
        this.id = id;
        this.address = address;
        this.port = port;
        this.ready = ready;
    }        

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Player{" + "name=" + name + ", id=" + id + ", address=" + address + ", port=" + port + ", ready=" + ready + '}';
    }
        
}
