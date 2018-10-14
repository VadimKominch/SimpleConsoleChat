package by.kominch.firstwebsocketapp.Commands;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import by.kominch.firstwebsocketapp.ClientHandler;
import by.kominch.firstwebsocketapp.SocketStorage.Storage;

public class LeaveCommand implements AbstractCommand {
	private ClientHandler client;
	private Storage storage;
	private static final Logger log = Logger.getLogger(ClientHandler.class.getSimpleName());

	static {
		PropertyConfigurator.configure("src/main/java/by/kominch/firstwebsocketapp/log4j.properties");
	}

	public LeaveCommand(ClientHandler client, Storage storage) {
		this.storage = storage;
		this.client = client;
	}

	public boolean execute() {
		if (client.getStatus().equals("client")) {
			log.info(client.getStatus() + " " + client.getName() + " left the channel");
			log.info(client.getConnectedPerson().getStatus() + " " + client.getConnectedPerson().getName()
					+ " left the channel");
			disconnect();
			return true;
		}
		return false;
	}

	public synchronized void disconnect() {
		if (client.getStatus().equals("client")) {
			ClientHandler oldAgent = client.getConnectedPerson();
			client.setConnectedPerson(null);
			if (oldAgent != null) {
				oldAgent.setConnectedPerson(null);
				storage.addAgent(oldAgent);
			}
		}
	}
}