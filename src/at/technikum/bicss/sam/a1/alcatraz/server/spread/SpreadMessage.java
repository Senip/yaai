/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.server.spread;

import java.io.Serializable;

/**
 *
 * @author Gabriel Pendl
 * SpreadMessage send to GroupMemebers
 * Header: Type of Message (master-server, player-list)
 * Body: (id of master server, PlayerList)
 */
public class SpreadMessage implements Serializable{
    // header if upadte on playerlist or a new masterserver
    private String header = "";
    // new masterserver id or playerlist
    private Object body = null;

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
    
    
    
}
