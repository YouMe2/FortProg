package set03;

/**
 * A registration for a chat client is represented by a <code>ChatClient</code>
 * object and name. It is being used by the {@link ChatServerImpl chat server} to
 * manage all valid client registrations.
 */
public class ChatClientRegistration {

	private final String name;
	private final ChatClient chatClient;

	public ChatClientRegistration(String name, ChatClient chatClient) {
		this.name = name;
		this.chatClient = chatClient;
	}

	public String getName() {
		return name;
	}

	public ChatClient getChatClient() {
		return chatClient;
	}

}
