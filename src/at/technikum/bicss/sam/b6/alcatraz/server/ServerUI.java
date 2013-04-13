/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.server;

import at.technikum.bicss.sam.b6.alcatraz.common.Player;
import at.technikum.bicss.sam.b6.alcatraz.common.Util;
import at.technikum.bicss.sam.b6.alcatraz.server.spread.Spread;
import java.util.Formatter;
import java.util.Scanner;

/**
 *
 * @author Peter
 */
public class ServerUI 
{
    private ServerUI() {     
    //prohibit instances of class ServerUI
    }
 
    public static void banner()
    {
        System.out.println("*******************************************************************************");
        System.out.println("*                                                                             *");
        System.out.println("*                  yaai - Yet Another Alcatraz Implementation                 *");
        System.out.println("*              Alcatraz Registration Server powered by RMI/Spread             *");
        System.out.println("*                                                                             *");
        System.out.println("*******************************************************************************");
        System.out.println();
    }
    
    public static void run()
    {
        String str;
        Scanner in = new Scanner(System.in); 
        
        do
        {
            do
            {
                System.out.print("> ");

                str = in.nextLine();
                str = str.trim();

            } while(str.isEmpty());
                        
            switch(str.toLowerCase())
            {
                case "show user":   System.out.println(user());
                break;
                case "show master": System.out.println(Spread.server().getMasterServerAddress());
                break;
                case "show server": System.out.println(Spread.server().getMemberServer().toString());
                break;
                case "show info":   System.out.println("yaai - Yet Another Alcatraz Implementation");
                                    System.out.println("Alcatraz Registration Server powered by RMI/Spread ");
                                    System.out.println("Thanks to all supporters and contributors!");
                break;                    
            }
            
        } while(str.compareTo("exit") != 0);
    }
    
    private static String user()
    {
        Formatter fmt = new Formatter();
        fmt.format("\n");
        fmt.format("%2s | %-40s | %-7s | %15s:%-5s\n", "#", "Name", "Status", "Address", "Port");
        fmt.format("%s","-------------------------------------------------------------------------------\n");
        
        int i = 0;
        for (Player p : Spread.server().getPlayerList().getLinkedList()) 
        {
            fmt.format("%2d | %-40s | %-7s | %15s:%-5d\n", p.getId(),
                    p.getName(), (p.isReady() ? "ready" : "waiting"),
                    p.getAddress(), p.getPort());;
            i++;
        }
        
        fmt.format("\n %s: %d", "Total", i);
        fmt.format("\n");
        return fmt.toString();
    }
}
