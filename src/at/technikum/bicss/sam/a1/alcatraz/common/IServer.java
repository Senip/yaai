/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Rudolf Galler <ic10b039@technikum-wien.at> [1010258039]
 */
public interface IServer extends Remote {

    public void register(String name, String address, int port) throws RemoteException;
    public void deregister(String name) throws RemoteException;
    public void setStatus(String name, boolean ready) throws RemoteException;
    public String getMasterServer() throws RemoteException;
}
