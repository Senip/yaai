/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.common;

/**
 *
 * @author 
 */
public class AlcatrazClientInitGameException extends Exception {

    /**
     * Alcatraz Server Exception
     */
    public AlcatrazClientInitGameException() {
    }

    /**     
     * Alcatraz Server Exception
     * 
     * @param msg the detail message.
     */
    public AlcatrazClientInitGameException(String msg) {
        super(msg);
    }
}
