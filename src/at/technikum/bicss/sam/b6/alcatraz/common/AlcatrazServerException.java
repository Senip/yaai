/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.common;

/**
 *
 * @author 
 */
public class AlcatrazServerException extends Exception {

    /**
     * Creates a new instance of
     * <code>ServerException</code> without detail message.
     */
    public AlcatrazServerException() {
    }

    /**
     * Constructs an instance of
     * <code>ServerException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public AlcatrazServerException(String msg) {
        super(msg);
    }
}
