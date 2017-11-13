package set03.xmpls;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** This is an interface for a simple RMI talk client */
public interface TalkClient extends Remote {
	
    /** Connect to the talk client */
    public void connect(TalkClient other) throws RemoteException;

    /** Hang up a connection */
    public void bye() throws RemoteException;

    /** Send a message to the talk client */
    public void send(String msg) throws RemoteException;

}
