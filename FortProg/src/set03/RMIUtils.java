package set03;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIUtils {

	/** Create a new registry or connect to the existing one */
	public static Registry getOrCreateRegistry() {
		try {
			return LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		} catch (RemoteException e0) {
			try {
				return LocateRegistry.getRegistry();
			} catch (RemoteException e1) {
				return null;
			}
		}
	}

}
