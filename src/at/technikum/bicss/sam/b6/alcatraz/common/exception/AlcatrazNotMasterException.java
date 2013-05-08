/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.common.exception;

/**
 *
 * @author 
 */
public class AlcatrazNotMasterException extends AlcatrazServerException {

    /**
     * Alcatraz Server Exception
     */
    public AlcatrazNotMasterException() {
    }

    /**     
     * Alcatraz Server Exception
     * 
     * @param msg the detail message.
     */
    public AlcatrazNotMasterException(String msg) {
        super(msg);
    }
}
