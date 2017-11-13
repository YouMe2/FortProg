package set03;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

public class ChatClientImpl extends UnicastRemoteObject implements ChatClient, Runnable {
	private static final long serialVersionUID = 806027190262448605L;

	private final Scanner in = new Scanner(System.in);

	private boolean running;
	private Thread thread;

	private ChatServer server;
	private boolean connected = false;

	private String username;

	protected ChatClientImpl() throws RemoteException {
		super();
	}

	@Override
	public void send(String msg) throws RemoteException {
		System.out.println(msg);
	}

	public void onStart() {
		if (!running) {
			thread = new Thread(this);
			running = true;
			thread.start();
		}
	}

	public void onStop() {
		if (running) {
			running = false;
			thread.interrupt();
			thread = null;
		}
		System.exit(0);
	}

	@Override
	public void run() {
		try {
			while (!connected)
				connect();
			printUsers();
			String msg;
			while (connected) {
				msg = in.nextLine();
				if (msg.equals(":q")) {
					disconnect();
					connected = false;
				} else if (msg.equals(":u"))
					printUsers();
				else
					try {
						server.send(username + ": " + msg);
					} catch (RemoteException e) {
						connected = false;
					}
			}
			System.out.println("Goodbye!");
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			onStop();
		}
	}

	private void printUsers() {
		try {
			List<String> users = server.getUsers();
			System.out.println("User list:");
			users.forEach(System.out::println);
		} catch (RemoteException e) {
			System.err.println("Couldn't retrieve users: " + e.getMessage());
		}
	}

	private void connect() {
		String hostname;
		String username;
		boolean success = false;
		do {
			do {
				System.out.println("Please enter a hostname!");
				hostname = in.nextLine();
			} while (hostname == null || hostname.isEmpty());
			do {
				System.out.println("Please enter a username!");
				username = in.nextLine();
			} while (username == null || username.isEmpty());
			try {
				Registry reg = LocateRegistry.getRegistry(hostname);
				server = (ChatServer) reg.lookup(ChatServer.RMI_NAME);
				success = server.register(this, username);
				if (!success)
					System.out.println("Username already taken!");
			} catch (RemoteException | NotBoundException e) {
				System.err.println("Failed: " + e.getMessage());
			}
		} while (!success);
		this.username = username;
		connected = true;
	}

	private void disconnect() {
		try {
			server.logout(this);
		} catch (RemoteException e) {
			System.out.println("Failed to logout: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		try {
			new ChatClientImpl().onStart();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
