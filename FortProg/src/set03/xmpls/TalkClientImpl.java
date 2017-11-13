package set03.xmpls;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/** This implements a simple Chat Client via RMI */
public class TalkClientImpl extends UnicastRemoteObject implements TalkClient {

	private TalkClient other = null;
	private boolean connected = false;

	protected TalkClientImpl() throws RemoteException {
		super();
	}

	/**
	 *
	 */
	private static final long serialVersionUID = -2683353750553564871L;

	/** Create a new registry or connect to the existing one */
	private static Registry getOrCreateRegistry() {
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

	/** Receive a call */
	@Override
	public synchronized void connect(TalkClient other) {
		this.other = other;
		notify();
	}

	/** Receive a good bye message */
	@Override
	public synchronized void bye() {
		System.out.println("Other client quit");
		connected = false; // Alternativ: System.exit(0);
	}

	/** Receive a message from the server and print it to the output */
	@Override
	public void send(String msg) {
		System.out.println(msg);
	}

	/** Establish a connection */
	private synchronized void waitForOther() {
		while (other == null)
			try {
				wait();
			} catch (InterruptedException e) {
			}
	}

	/** A simple read-print-loop which reads messages from the input */
	public void talk() {
		waitForOther();
		connected = true;
		System.out.println("Welcome to talk, type ':q' to quit.");

		try (Scanner in = new Scanner(System.in)) {
			String msg;
			while (connected) {
				msg = in.nextLine();
				if (msg.equals(":q"))
					try {
						other.bye();
					} catch (RemoteException e) {
					} finally {
						connected = false;
					}
				else
					try {
						other.send(msg);
					} catch (RemoteException e) {
						connected = false;
					}
			}
		}

		System.out.println("Goodbye!");
	}

	/** Start a new talk client */
	public static void main(String[] args) throws Exception {

		Registry reg;

		switch (args.length) {
			case 1:
				reg = getOrCreateRegistry();
				if (reg != null) {
					TalkClientImpl me = new TalkClientImpl();
					reg.rebind(args[0], me);
					me.talk();
					reg.unbind(args[0]);
				}
				break;
			case 2:
				TalkClientImpl me = new TalkClientImpl();
				reg = LocateRegistry.getRegistry(args[1]);
				TalkClient other = (TalkClient) reg.lookup(args[0]);
				me.other = other;
				other.connect(me);
				me.talk();
				break;
			default:
				System.err.println("Usage:");
				System.err.println("To receive a call: java TalkClientImpl <user>");
				System.err.println("To call someone  : java TalkClientImpl <user> <host>");
				System.exit(1);
		}
		System.exit(0);
	}

}
