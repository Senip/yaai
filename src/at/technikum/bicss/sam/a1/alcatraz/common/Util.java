package at.technikum.bicss.sam.a1.alcatraz.common;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Properties;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public final class Util {

    static final private String PROP_FILE = "alcatraz.props";
    /**
     * General Properties
     */
    static final private String CONNECTION_TIMEOUT = "connection_timeout_ms";
    static final public int NAME_MAX_LENGTH = 30;
    /**
     * Client Properties
     */
    static final public String SERVER_ADDRESS_LIST = "server_address_list";
    static final public String CLIENT_RMIREG_PORT = "client_rmireg_port";
    static final public String CLIENT_RMIREG_PATH = "client_rmireg_path";
    static final private int CLIENT_RMIREG_PORT_MIN = 49152;
    static final private int CLIENT_RMIREG_PORT_MAX = 65535;
    /**
     * Server Properties
     */
    static final public String MY_SERVER_ADDRESS = "my_server_host_address";
    static final public String SERVER_RMIREG_PORT = "server_rmireg_port";
    static final public String SERVER_RMIREG_PATH = "server_rmireg_path";
    static final public String GROUP_NAME = "spread_group_name";
    private static Properties props = null;
    private static Logger l = Logger.getRootLogger();

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
        PropertyConfigurator.configure(props);
    }

    public static int getConTimeOut() {
        return Integer.valueOf(props.getProperty(CONNECTION_TIMEOUT));
    }

    public static String[] getServerAddressList() {
        return props.getProperty(SERVER_ADDRESS_LIST).split(",");
    }

    public static int getClientRMIPort() {
        return Integer.valueOf(props.getProperty(CLIENT_RMIREG_PORT));
    }

    public static String getClientRMIPath() {
        return props.getProperty(CLIENT_RMIREG_PATH);
    }

    // master server ip
    public static String getMyServerAddress() {
        return props.getProperty(MY_SERVER_ADDRESS);
    }

    public static int getServerRMIPort() {
        return Integer.valueOf(props.getProperty(SERVER_RMIREG_PORT));
    }

    public static String getServerRMIPath() {
        return props.getProperty(SERVER_RMIREG_PATH);
    }

    // spread group name
    public static String getGroupName() {
        return props.getProperty(GROUP_NAME);
    }

    /**
     * Builds a String in RMU-URI format: rmi://host:port/path/
     *
     * @param host name or address
     * @param port port number
     * @param path namespace to object
     * @return
     */
    public static String buildRMIString(String host, int port, String path) {
        StringBuilder sb = new StringBuilder("rmi://");
        sb.append(host).append(":").append(port);
        sb.append("/").append(path).append("/");
        return sb.toString();
    }

    public static String buildRMIString(String host, int port, String path, String player_name) {
        return buildRMIString(host, port, path + "/" + player_name);
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

    public static void logRMIReg(Registry rmireg)
            throws RemoteException, AccessException {
        StringBuilder sb = new StringBuilder();
        sb.append("Objects available on ");
        sb.append(rmireg.toString()).append("\n");
        for (String s : rmireg.list()) {
            sb.append(s).append("\n");
        }
        l.debug(sb.toString());
    }

    public static void centerFrame(JFrame frm) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // Center position of frame
        int top = (screenSize.height - frm.getHeight()) / 2;
        int left = (screenSize.width - frm.getWidth()) / 2;
        frm.setLocation(left, top);
    }

    public static int getRandomPort() {
        int port_min = 49152;
        int port_max = 65535;

        Random random = new Random();
        int randomPort =
                random.nextInt(CLIENT_RMIREG_PORT_MAX - CLIENT_RMIREG_PORT_MIN)
                + CLIENT_RMIREG_PORT_MIN;
        l.debug("Generated random port number: " + randomPort);
        return randomPort;
    }

    public static void handleDebugMessage(String prefix, String message) {
        System.out.print(prefix + ": " + message + "\n");
    }

    /**
     * Checks whether a string is empty and returns {@code true} if it is,
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

    /**
     * Displays an error to the user inside a dialog-window.
     *
     * @param frm current frame.
     * @param message text to be displayed
     */
    public static void errorUser(JFrame frm, String message) {
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
    public static void warnUser(JFrame frm, String message) {
        //l.finer("Display warning message to user");
        JOptionPane.showMessageDialog(frm, message,
                "Warning", JOptionPane.WARNING_MESSAGE);
    }
}
