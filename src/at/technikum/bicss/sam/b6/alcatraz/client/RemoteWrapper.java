/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.client;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.RemoteException;

/**
 *
 * @author 
 */
public abstract class RemoteWrapper
{    
    // Hint: This can be done in a more elegant way using reflection 
    
    protected abstract class Wrapper<T, E extends Throwable >
    {
        public T execute() throws E, RemoteException
        {
            do
            {
                try                       { return command(); } 
                catch (RemoteException e) { }
                
            } while(connect());
            
            throw new RemoteException("Permanent Failure");
        }
        
        protected abstract T    command()  throws E, RemoteException;
    }
    
    protected abstract boolean connect();
}
    
