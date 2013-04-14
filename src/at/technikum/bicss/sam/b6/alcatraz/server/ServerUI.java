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
 * The server is a multi-threaded application.
 * The UI is not synchronized! It might display invalid information!
 * 
 * @author 
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
        String str ="";
        Scanner in = new Scanner(System.in); 
        
        do
        {
            do
            {
                System.out.print("> ");

                try{ str = in.nextLine(); } catch(Exception e) { /*Ctrl+C*/ }
                str = str.trim();
                
            } while(str.isEmpty());
                        
            switch(str.toLowerCase())
            {
                case "player":   System.out.println(user());
                break;
                case "master": System.out.println(master());
                break;
                case "member": System.out.println(member());
                break;
                case "server": System.out.println(server());
                break;
                case "whoami": System.out.println(whoami());
                break;
                case "info":   System.out.println(info());
                break;  
                case "help":   System.out.println(help());    
                break;
                default:       System.out.println(notFound(str));    
                break;
            }
            
        } while(str.compareTo("exit") != 0);
    }
    
    private static String user()
    {
        Formatter fmt = new Formatter();
        fmt.format("\n");
        fmt.format("%2s | %-40s | %-7s | %15s:%-5s\n", "id", "Name", "Status", "Address", "Port");
        fmt.format("%s","-------------------------------------------------------------------------------\n");
        
        for (Player p : Spread.server().getPlayerList().getLinkedList()) 
        {
            fmt.format("%2d | %-40s | %-7s | %15s:%-5d\n", p.getId(),
                    p.getName(), (p.isReady() ? "ready" : "waiting"),
                    p.getAddress(), p.getPort());;
        }
        
        fmt.format("\n %s: %d", "Total", Spread.server().getPlayerList().getLinkedList().size());
        fmt.format("\n");
        return fmt.toString();
    }
    
    private static String master()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(Spread.server().getMasterServerName()).append(" ");
        sb.append(Spread.server().getMasterServerAddress());
        sb.append(" ").append(Spread.server().i_am_MasterServer() ? "(me)" : "(not me)");
        sb.append("\n");
        return sb.toString();
    }
    
    private static String member()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        
        sb.append("Group: ");
        sb.append(Spread.server().getGroupName()).append("\n");
        sb.append("-------------------------------------------------------------------------------\n");
        
        
        for(Object member : Spread.server().getMemberServer())
        {
            String name = (String) member;
            
            sb.append(name);
            if(name.equalsIgnoreCase(Spread.server().getMasterServerName()))
            {
                sb.append(" ").append(Spread.server().getMasterServerAddress());
                sb.append(" (master)");
            }
            if(name.equalsIgnoreCase(Spread.server().getPrivateGroup().toString()))
            {
                sb.append(" (me)");
            }
            sb.append("\n");
        }
        sb.append("\n Total: ").append(Spread.server().getMemberServer().size());
        sb.append("\n");
        
        return sb.toString();
    }
        
    private static String server()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        
        sb.append("Server:\n");
        sb.append("-------------------------------------------------------------------------------\n");
        
        
        for(String name : Util.getServerAddressList())
        {            
            sb.append(name);
            if(name.equalsIgnoreCase(Spread.server().getMasterServerAddress()))
            {
                sb.append(" (master)");
            }
            if(name.equalsIgnoreCase(Util.getMyServerAddress()))
            {
                sb.append(" (me)");
            }
            sb.append("\n");
        }
        
        sb.append("\n Total: ").append(Util.getServerAddressList().length);
        sb.append("\n");
        
        return sb.toString();
    }
    
    private static String whoami()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append(Spread.server().getPrivateGroup().toString());
        sb.append(" ").append(Util.getMyServerAddress());
        sb.append(" ").append(Spread.server().i_am_MasterServer() ? "(master)" : "(slave)");
        sb.append("\n");
                
        return sb.toString();
    }
    
    private static String info()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("yaai - Yet Another Alcatraz Implementation").append("\n");
        sb.append("Alcatraz Registration Server powered by RMI/Spread").append("\n");
        sb.append("Thanks to all supporters and contributors!").append("\n");        
        
        return sb.toString();
    }
    
    private static String help()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("player     Show connected player").append("\n");
        sb.append("master     Show master server").append("\n");
        sb.append("member     Show member server").append("\n");
        sb.append("server     Show the server list").append("\n");
        sb.append("whoami     Show info about this server").append("\n");
        sb.append("info       Display credits").append("\n");
        sb.append("help       Display this text").append("\n");
        
        return sb.toString();
    }
    
    private static String notFound(String str)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(str).append(": command not found").append("\n");
        sb.append("If you want a list of all supported commands type 'help'").append("\n");
        
        return sb.toString();
        
    }
}
