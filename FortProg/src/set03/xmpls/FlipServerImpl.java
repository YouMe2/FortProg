package set03.xmpls;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/** Implementation of an FlipServer */
public class FlipServerImpl extends UnicastRemoteObject implements FlipServer {

	private static final long serialVersionUID = -2767063908667994453L;

	private boolean state;

	public FlipServerImpl() throws RemoteException {
		state = false;
	}

	@Override
	public void flip() {
		state = !state;
	}

	@Override
	public boolean getState() {
		return state;
	}

	public static void main(String[] args) {
		try {
			FlipServer server = new FlipServerImpl();
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT).rebind(FlipServer.NAME, server);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}