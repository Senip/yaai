/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
 package at.technikum.bicss.sam.b6.alcatraz.common;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public final class Util 
{
    private  final static String PROP_FILE = "alcatraz.props";
    
    //General Properties
    private final static String CONNECTION_TIMEOUT = "connection_timeout_ms";
    public  final static int    NAME_MAX_LENGTH    = 13;
    public  final static int    NUM_MIN_PLAYER     = 2;
    public  final static int    NUM_MAX_PLAYER     = 4;
    
    // Client Properties
    public  final static String SERVER_ADDRESS_LIST     = "server_address_list";
    public  final static String CLIENT_RMIREG_PORT      = "client_rmireg_port";
    public  final static String CLIENT_RMIREG_PATH      = "client_rmireg_path";
    private final static int    CLIENT_RMIREG_PORT_MIN  = 49152;
    private final static int    CLIENT_RMIREG_PORT_MAX  = 65535;
    public  final static int    CLIENT_RMIREG_RETRY_MAX = (CLIENT_RMIREG_PORT_MAX -
                                                           CLIENT_RMIREG_PORT_MIN);
    public  final static int    SERVER_BUSY_TIMEOUT     = 500; //ms

    // Server Properties
    public  final static String MY_SERVER_ADDRESS  = "my_server_host_address";
    public  final static String SERVER_RMIREG_PORT = "server_rmireg_port";
    public  final static String SERVER_RMIREG_PATH = "server_rmireg_path";    
    public  final static String SPREAD_SERVER_ADDR = "spread_server_address";
    public  final static String SPREAD_SERVER_PORT = "spread_server_port";
    public  final static String SPREAD_GROUP_NAME  = "spread_group_name";
            
    private static   Properties props              = null;
    private static   Logger     l                  = Logger.getRootLogger();

    private Util() {    
    //prohibit instances of class Util
    }
    
    /**
     * Setup a Logger
     * 
     * @param name Name of the Logger
     * @return Logger
     */
    public static Logger setLogger(String name)
    {
        return (l =  Logger.getLogger(name));
    }
    
    /**
     * Get Logger
     * @return Logger
     */
    public static Logger getLogger()
    {
        return l;
    }
      
    /**
     * Read Properties from Properties File
     * 
     * @return Properties
     */
    public static Properties readProps() 
    {
        FileInputStream fileIn = null;
        props = new Properties();

        try 
        {
            fileIn = new FileInputStream(PROP_FILE);
            props.load(fileIn);
        } 
        catch (Exception e) 
        {
            l.fatal("Property file " + PROP_FILE + " not found!\n" 
                    + e.getMessage(), e);
            System.exit(1);
        }
        PropertyConfigurator.configure(props);
        return props;
    }

    public static int getConTimeOut() {
        return Integer.valueOf(getProp(CONNECTION_TIMEOUT));
    }

    public static String[] getServerAddressList() {
        return getProp(SERVER_ADDRESS_LIST).split("\\s*,\\s*"); //Split and trim
    }

    public static int getClientRMIPort() 
    {
        int port = 0;
        
        try
        {
            port = Integer.valueOf(getProp(CLIENT_RMIREG_PORT)).intValue();
        }
        catch(NumberFormatException e)
        {
            l.fatal("Can't parse Server RMI Registry Port " + 
                    " (CLIENT_RMIREG_PORT):" + getProp(CLIENT_RMIREG_PORT), e);
            System.exit(1);
        }
                
        return port;
    }

    public static String getClientRMIPath() {
        return getProp(CLIENT_RMIREG_PATH).trim();
    }

    // master server ip
    public static String getMyServerAddress() {
        return getProp(MY_SERVER_ADDRESS).trim();
    }

    public static int getServerRMIPort() 
    {
        int port = 0;
        
        try
        {
            port = Integer.valueOf(getProp(SERVER_RMIREG_PORT)).intValue();
        }
        catch(NumberFormatException e)
        {
            l.fatal("Can't parse Server RMI Registry Port " + 
                    " (SERVER_RMIREG_PORT):" + getProp(SERVER_RMIREG_PORT), e);
            System.exit(1);
        }
                
        return port;
    }

    public static String getServerRMIPath() {
        return getProp(SERVER_RMIREG_PATH).trim();
    }

    // spread server address
    public static String getSpreadServerAddr() {
        return getProp(SPREAD_SERVER_ADDR).trim();
    }
    
    // spread server port
    public static String getSpreadServerPort() {
        return getProp(SPREAD_SERVER_PORT).trim();
    }
    
    // spread group name
    public static String getSpreadGroupName() {
        return getProp(SPREAD_GROUP_NAME).trim();
    }

    /**
     * Get Property
     * 
     * @param prop_name Name of the Property
     * @return Property
     */
    public static String getProp(String prop_name) 
    {
        String prop_val = props.getProperty(prop_name);
    
        if (prop_val == null) 
        {
            l.fatal("Property " + prop_name + 
                    " was not found in file " + PROP_FILE);
            System.exit(1);
        }
        return prop_val;
    }

    /**
     * Builds a String in RMU-URI format: rmi://host:port/path/
     *
     * @param host name or address
     * @param port port number
     * @param path namespace to object
     * @return
     */
    public static String buildRMIString(String host, int port, String path) 
    {
        StringBuilder sb = new StringBuilder("rmi://");
        sb.append(host).append(":").append(port);
        sb.append("/").append(path).append("/");
        return sb.toString();
    }

    /**
     * Construct RMI String
     * 
     * @param host  Host
     * @param port  Port
     * @param path  Path
     * @param player_name   Player Name
     * @return RMI String
     */
    public static String buildRMIString(String host, int port, String path, String player_name) 
    {
        return buildRMIString(host, port, path + "/" + player_name);
    }

    /**
     * Add RMIReg to the Debug log
     * 
     * @param rmireg
     * @throws RemoteException
     * @throws AccessException 
     */
    public static void logRMIReg(Registry rmireg)
            throws RemoteException, AccessException 
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Objects available on\n");
        sb.append(rmireg.toString()).append("\n");
        
        for (String s : rmireg.list()) 
        {
            sb.append(s).append("\n");
        }
        
        l.debug(sb.toString());
    }
    
    /**
     * Center frame on scree
     * @param frm Frame
     */
    public static void centerFrame(JFrame frm) 
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // Center position of frame
        int top = (screenSize.height - frm.getHeight()) / 2;
        int left = (screenSize.width - frm.getWidth()) / 2;
        frm.setLocation(left, top);
    }
    
    /**
     * 
     * @return Random Port
     */
    public static int getRandomPort() 
    {
        Random random = new Random();
        int randomPort =
                random.nextInt(CLIENT_RMIREG_PORT_MAX - CLIENT_RMIREG_PORT_MIN)
                + CLIENT_RMIREG_PORT_MIN;
        l.debug("Generated random port number: " + randomPort);
        return randomPort;
    }
    
    /**
     * Create a unique Name
     * 
     * @param prefix Prefix 
     * @return Unique name in format prefix_uuid
     */
    public static String getUniqueName(String prefix)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix).append("_");
        sb.append(UUID.randomUUID().toString());
        
        return sb.toString();
    }
    
    /**
     * Checks whether a string is empty and returns {@code true} if it is,
     * otherwise {@code false}
     *
     * @param value
     * @return true if string is empty, otherwise false
     */
    public static boolean isEmpty(String value) 
    {
        return (value == null || "".equals(value));
    }

    /**
     * Displays an error to the user inside a dialog-window.
     *
     * @param frm current frame.
     * @param message text to be displayed
     */
    public static void errorUser(JFrame frm, String message) 
    {
        //l.finer("Display error message to user");
        JOptionPane.showMessageDialog(frm, message,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays a warning to the user inside a dialog-window.
     *
     * @param frm current frame.
     * @param message text to be displayed
     */
    public static void warnUser(JFrame frm, String message) 
    {
        //l.finer("Display warning message to user");
        JOptionPane.showMessageDialog(frm, message,
                "Warning", JOptionPane.WARNING_MESSAGE);
    }
        
    public static int yesnocancel(JFrame frm, String message) 
    {
        return JOptionPane.showConfirmDialog(frm, message,"Question", JOptionPane.YES_NO_CANCEL_OPTION);
    }
    
    public static int retryExit(JFrame frm, String message) 
    {
        Object[] options = {"Retry", "Exit"};
        return JOptionPane.showOptionDialog(frm,
                message,
                "Question",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
    }
}
