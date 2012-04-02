package at.technikum.bicss.sam.a1.alcatraz.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Properties;
import java.util.logging.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public final class Util {

    static final private String PROP_FILE = "alcatraz.props";
    static final public String SERVER_ADDRESS = "server_address";
    static final public String SERVER_RMIREG_PORT = "server_registry_port";
    static final public String CLIENT_RMIREG_PORT = "client_registry_port";
    static final public String GROUP_NAME = "group_name";
    static final public String MY_SERVER_ADDRESS = "my_server_host_address";
    private static Properties props = null;
    /**
     * Name of property-file for event logger
     */
    public static final String log_propfile = "log.props";
    private static Logger l = null;
    private static LogManager logMgr = null;
    private static FileHandler file_handler = null;
    private static MemoryHandler mem_handler = null;

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
    // master server ip

    public static String getMyServerAddress() {
        return props.getProperty(MY_SERVER_ADDRESS);
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

    public static void handleDebugMessage(String prefix, String message){
        System.out.print(prefix + ": " + message + "\n");       
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

    /**
     * Returns the current Util-{@link Logger} instance of this class. This may
     * be
     * <code>null</code> if Logging was not started before by calling method {@link #startLog}
     * or start was not successfull.
     *
     * @return returns the current {@link Logger} instance of this class.
     */
    public static Logger getLog() {
        return l;
    }

    /**
     * Starts the Util-{@link Logger} and writes all events to the file given by
     * <code>filename</code> if they exceed the {@link Level} specified by
     * <code>level</code>. The configuration of the {@link Logger} is read from
     * file {@link #log_propfile} if existing. If not the {@link Logger} is set
     * to standard configuration. This method throws no exceptions if there are
     * problems while opening the log-file the {@link Logger} since this should
     * be transparent to the user. In case of errors the {@link Logger} becomes
     * deactivated again by calling method {@link #stopLog()}.
     *
     * @param filename name of file that should be used for logging
     * @param level {@link Level} at which events should be logged
     */
    public static void startLog(String filename, Level level) {
        if (filename.isEmpty()) {
            throw new IllegalArgumentException("Filename must not be empty");
        }
        logMgr = LogManager.getLogManager();
        l = Logger.getLogger("global");

        FileInputStream f_propfile = null;
        try {
            f_propfile = new FileInputStream(log_propfile);
            logMgr.readConfiguration(f_propfile);
        } catch (Exception e) {
            /*
             * in case of SecurityException, FileNotFoundException or
             * IOException logMgr is used with standard configuration
             */
            logMgr.reset();
        } finally {
            if (f_propfile != null) {
                try {
                    f_propfile.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }

        try {
            file_handler = new FileHandler(filename, false);
            mem_handler = new MemoryHandler(file_handler, 100, level);
            l.addHandler(mem_handler);
            l.info("Logger set up properly");
        } catch (Exception e) {
            //in caser there is any problem stop the logger
            l.severe("Caught exception while setting up logger");
            l.severe(e.getStackTrace().toString());
            stopLog();
        }
    }

    /**
     * Stops the Util-{@link Logger}
     */
    public static void stopLog() {

        l.info("stopping logger...");
        l.removeHandler(mem_handler);

        if (mem_handler != null) {
            mem_handler.close();
            mem_handler = null;
        }

        if (file_handler != null) {
            file_handler.close();
            file_handler = null;
        }

        l = null;
        logMgr = null;
    }
}
