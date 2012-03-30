package at.technikum.bicss.sam.a1.alcatraz.common;

import java.io.FileInputStream;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Properties;

public final class Util {

    static final private String PROP_FILE = "alcatraz.props";
    static final public String SERVER_ADDRESS = "server_address";
    static final public String SERVER_RMIREG_PORT = "server_registry_port";
    static final public String CLIENT_RMIREG_PORT = "client_registry_port";
    static final public String GROUP_NAME = "group_name";
    private static Properties props = null;

    //prohibit instances of class Util
    private Util() {
    }

    public static void readProps() {
        FileInputStream fileIn = null;
        props = new Properties();

        try {
            fileIn = new FileInputStream(PROP_FILE);
            props.load(fileIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] getServerAddress() {
        return props.getProperty(SERVER_ADDRESS).split(",");
    }

    public static int getServerRMIPort() {
        return Integer.valueOf(props.getProperty(SERVER_RMIREG_PORT));
    }
    
    public static int getClientRMIPort() {
        return Integer.valueOf(props.getProperty(CLIENT_RMIREG_PORT));
    }
    
    
    // spread group name
    public static String getGroupName() {
        return props.getProperty(GROUP_NAME);
    }
        
    public static Properties getProps() {
        FileInputStream fileIn = null;
        Properties props = new Properties();

        try {
            fileIn = new FileInputStream(PROP_FILE);
            props.load(fileIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props;
    }

    public static void printRMIReg(Registry rmireg)
            throws RemoteException, AccessException {
        System.out.println("Objects available on " + rmireg.toString());
        for (String s : rmireg.list()) {
            System.out.println(s);
        }
    }

    /**
     * Checks whether a string is empty an returns {@code true} if it is,
     * otherwise {@code false}
     *
     * @param value
     * @return true if string is empty, otherwise false
     */
    public static boolean isEmpty(String value) {
        if (value == null || "".equals(value)) {
            return true;
        }
        return false;
    }
}
