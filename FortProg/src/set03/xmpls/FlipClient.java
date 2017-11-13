package set03.xmpls;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class FlipClient {

	public static void main(String[] args) {
		try {
			FlipServer s = (FlipServer) LocateRegistry.getRegistry().lookup(FlipServer.NAME);

			s.flip();
			System.out.println("State: " + s.getState());

		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}
}