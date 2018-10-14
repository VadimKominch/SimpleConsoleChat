package by.kominch.firstwebsocketapp.Commands;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import by.kominch.firstwebsocketapp.ClientHandler;
import by.kominch.firstwebsocketapp.SocketStorage.Storage;

public class ExitCommand implements AbstractCommand {

	private ClientHandler client;
	private Storage storage;
	private static final Logger log = Logger.getLogger(ClientHandler.class.getSimpleName());

	static {
		PropertyConfigurator.configure("src/main/java/by/kominch/firstwebsocketapp/log4j.properties");
	}

	public ExitCommand(ClientHandler client, Storage storage) {
		this.storage = storage;
		this.client = client;
	}

	public boolean execute() {
		log.info(client.getStatus() + " " + client.getName() + " left the system");
		log.info(client.getConnectedPerson().getStatus() + " " + client.getConnectedPerson().getName()
				+ " left the channel");
		ClientHandler oldagent = client.getConnectedPerson();
		client.setConnectedPerson(null);
		if (oldagent != null) {
			oldagent.setConnectedPerson(null);
		}
		if (client.getStatus().equals("client")) {
			storage.addAgent(oldagent);
			storage.deletePerson(client);
		}
		return true;
	}
}
