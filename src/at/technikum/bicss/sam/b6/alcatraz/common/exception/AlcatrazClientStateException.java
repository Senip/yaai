/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.common.exception;

/**
 *
 * @author 
 */
public class AlcatrazClientStateException extends Exception {

    /**
     * Alcatraz Server Exception
     */
    public AlcatrazClientStateException() {
    }

    /**     
     * Alcatraz Server Exception
     * 
     * @param msg the detail message.
     */
    public AlcatrazClientStateException(String msg) {
        super(msg);
    }
}
