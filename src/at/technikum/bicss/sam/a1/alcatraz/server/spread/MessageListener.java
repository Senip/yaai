/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.server.spread;

/**
 *
 * @author Gabriel Pendl
 */

import spread.AdvancedMessageListener;
import spread.MembershipInfo;
import spread.SpreadException;
import spread.SpreadMessage;


public class MessageListener implements AdvancedMessageListener {


    /**
     * Constructor
     */
    public MessageListener() {
        super();
    }

    /**
     * Overridden Method for receiving regular Messages
     * @param message
     */
    @Override
    public void regularMessageReceived(SpreadMessage message) {
        try {
            Object obj = message.getObject();
            System.out.print("got messege " + obj);
        } catch (SpreadException spreadException) {
            System.out.print(spreadException);
        }
    }

    @Override
    public void membershipMessageReceived(SpreadMessage message) {
        MembershipInfo msi = message.getMembershipInfo();

        // there is a join
        if (msi.isCausedByJoin()) {
            System.out.print("join");
        }

        // someone leaves
        if (msi.isCausedByLeave()) {
            System.out.print("leave");
        }

        // disconnect
        if (msi.isCausedByDisconnect()) {
            System.out.print("disconnect");
        }
        // network error
        if (msi.isCausedByNetwork()) {
           
            System.out.print("network error");
        }
    }


}

