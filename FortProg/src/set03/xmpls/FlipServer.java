package set03.xmpls;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** A server with a flippable state */
public interface FlipServer extends Remote {

	public final String NAME = "FlipServer";

	/**
	 * Flip the current state
	 *
	 * @throws RemoteException
	 */
	public void flip() throws RemoteException;

	/**
	 * Retrieve the current state
	 *
	 * @return current state
	 * @throws RemoteException
	 */
	public boolean getState() throws RemoteException;
}