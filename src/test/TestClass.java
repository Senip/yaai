/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author Ru
 */
public class TestClass {

    public static void main(String[] args) {
        try {
        Socket s = new Socket("www.w3c.org", 80);
        InetAddress ip = s.getLocalAddress();
        System.out.println("Internet IP = " + ip.toString());
        s.close();    
        } catch (Exception e) {
            
        }
        
    }
}
