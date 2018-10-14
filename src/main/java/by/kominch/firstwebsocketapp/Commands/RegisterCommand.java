package by.kominch.firstwebsocketapp.Commands;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import by.kominch.firstwebsocketapp.ClientHandler;
import by.kominch.firstwebsocketapp.SocketStorage.Storage;

public class RegisterCommand implements AbstractCommand {
	private ClientHandler client;
	private Storage storage;
	private static final Logger log = Logger.getLogger(ClientHandler.class.getSimpleName());

	static {
		PropertyConfigurator.configure("src/main/java/by/kominch/firstwebsocketapp/log4j.properties");
	}
	
	public RegisterCommand(ClientHandler client,Storage storage) {
		this.storage = storage;
		this.client = client;
	}

	public boolean execute() {
		String appRole = client.getScanner().nextLine();
		while (true) {
			if (client.getScanner().hasNextLine()) {
				String[] buf = client.getScanner().nextLine().trim().split("\\s+");
				if (buf.length != 3) {
					if(buf.length==1) {
						if(buf[0].equals("/exit")) {
						return false;
						}
					}
					client.getPrintWriter().println("Wrong command");
				} else {
					if (buf[0].equals(Commands.REGISTER.getCommand())) {
						if ((buf[1]).equals(appRole)) {
							client.setRole(buf[1]);
							client.setName(buf[2]);
							if (client.getStatus().equals("client")) {
								storage.addClient(client);
							} else {
								storage.addAgent(client);
							}
							log.info(client.getStatus() + " " + client.getName() + " joined to the system");
							return true;
						} else {
							client.getPrintWriter().println("Access denied. Please re-enter your role.");
						}
					} else {
						client.getPrintWriter().println("Wrong register command. Enter /register");
					}
				}
				client.getPrintWriter().println("You are not registered. Write /register [role] [name]");
			}
		}
		
	}

}