/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.common.exception;

/**
 *
 * @author 
 */
public class AlcatrazInitGameException extends Exception {

    /**
     * Alcatraz Server Exception
     */
    public AlcatrazInitGameException() {
    }

    /**     
     * Alcatraz Server Exception
     * 
     * @param msg the detail message.
     */
    public AlcatrazInitGameException(String msg) {
        super(msg);
    }
}
