/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.common;

/**
 *
 * @author Rudolf Galler <ic10b039@technikum-wien.at> [1010258039]
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
