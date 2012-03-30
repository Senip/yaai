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
public class AlcatrazMessage implements Serializable{
    // header if upadte on playerlist or a new masterserver
    private MessageHeader header;
    // new masterserver id or playerlist
    private Object body = null;

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public MessageHeader getHeader() {
        return header;
    }

    public void setHeader(MessageHeader header) {
        this.header = header;
    }
    
    // guess there comes mr. header and his bride mr. body
    public AlcatrazMessage(MessageHeader h, Object o) {
        if(o != null){
            header = h;
            body = o;
        }
    }
    
    
    
}
