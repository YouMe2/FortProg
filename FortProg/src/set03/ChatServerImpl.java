package set03;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class ChatServerImpl extends UnicastRemoteObject implements ChatServer {
	private static final long serialVersionUID = 5107453690560461667L;

	private Set<ChatClientRegistration> clients = new HashSet<>();

	protected ChatServerImpl() throws RemoteException {
		super();
	}

	@Override
	public synchronized boolean register(ChatClient c, String name) throws RemoteException {
		ChatClientRegistration registration = new ChatClientRegistration(name, c);
		boolean unique = !clients.stream()
				.map(r -> r.getName())
				.filter(n -> n.equalsIgnoreCase(name))
				.findAny()
				.isPresent();
		if (unique) {
			clients.add(registration);
			send("[" + name + " joined the chat]");
		}
		return unique;
	}

	@Override
	public synchronized List<String> getUsers() throws RemoteException {
		return clients.stream().map(r -> r.getName()).collect(Collectors.toList());
	}

	@Override
	public synchronized void logout(ChatClient c) throws RemoteException {
		Optional<ChatClientRegistration> o = clients.stream().filter(r -> r.getChatClient().equals(c)).findAny();
		if (o.isPresent()) {
			ChatClientRegistration remove = o.get();
			clients.remove(remove);
			send("[" + remove.getName() + " left the chat]");
		}
	}

	@Override
	public synchronized void send(String msg) throws RemoteException {
		ArrayList<ChatClientRegistration> failures = new ArrayList<>();
		for (ChatClientRegistration r : clients)
			try {
				r.getChatClient().send(msg);
			} catch (RemoteException e) {
				failures.add(r);
			}
		for (ChatClientRegistration r : failures)
			logout(r.getChatClient());
	}

	public static void main(String[] args) {
		Registry reg = RMIUtils.getOrCreateRegistry();
		if (reg == null) {
			System.err.println("Couldn't get or create Registry");
			System.exit(1);
		}
		try {
			ChatServerImpl server = new ChatServerImpl();
			reg.rebind(ChatServer.RMI_NAME, server);
			System.out.println(Arrays.toString(reg.list()));
			Scanner exitCommandScanner = new Scanner(System.in);
			System.out.println("Server running. Type exit to shut down server.");
			while (!exitCommandScanner.nextLine().equals("exit"))
				System.out.println("Type exit to shut down server");
			exitCommandScanner.close();
			reg.unbind(ChatServer.RMI_NAME);
		} catch (RemoteException | NotBoundException e) {
			System.out.println("Failed: " + e.getMessage());
		}
		System.exit(0);
	}

}
