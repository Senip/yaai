/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.common;

/**
 *
 * @author 
 */
public class AlcatrazClientException extends Exception {

    /**
     * Alcatraz Server Exception
     */
    public AlcatrazClientException() {
    }

    /**     
     * Alcatraz Server Exception
     * 
     * @param msg the detail message.
     */
    public AlcatrazClientException(String msg) {
        super(msg);
    }
}
