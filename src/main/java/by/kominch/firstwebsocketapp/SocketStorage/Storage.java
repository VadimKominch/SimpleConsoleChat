package by.kominch.firstwebsocketapp.SocketStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import by.kominch.firstwebsocketapp.ClientHandler;
import by.kominch.firstwebsocketapp.storage.SessionStorage;

public class Storage {
	private static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<ClientHandler>());;
	private static List<ClientHandler> agents = Collections.synchronizedList(new ArrayList<ClientHandler>());;
	static List<ClientHandler> sessionagents = Collections.synchronizedList(new ArrayList<ClientHandler>());
	static List<ClientHandler> sessionclients = Collections.synchronizedList(new ArrayList<ClientHandler>());

	public Storage() {
	}

	public synchronized void addClient(ClientHandler object) {
		clients.add(object);
	}

	public synchronized void addAgent(ClientHandler object) {
		agents.add(object);
	}

	public synchronized void deleteAgentList() {
		agents.clear();
	}

	public synchronized void deleteCLientList() {
		clients.clear();
	}
	
	public synchronized void deleteSessionAgents() {
		sessionagents.clear();
	}
	
	public synchronized void deleteSessionClients() {
		sessionclients.clear();
	}
	
	public static synchronized ClientHandler getFreeAgent() {
		if (agents.size() != 0) {
			ClientHandler fa = agents.get(0);
			agents.remove(fa);
			return fa;
		} else {
			if (sessionagents.size() != 0) {
				for (ClientHandler e : sessionagents) {
					if (e.isEnable()) {
						e.setEnable(false);
						return e;
					}
				}
			}
		}
		return null;
	}

	public synchronized boolean isEmptyAgentList() {
		return agents.isEmpty() ? true : false;
	}

	public synchronized void deletePerson(ClientHandler object) {
		if (object.getStatus().equals("agent")) {
			agents.remove(object);
		}
		if (object.getStatus().equals("client")) {
			clients.remove(object);
		}
	}

	public synchronized void closeConnections() {
		try {
			for (ClientHandler i : agents) {
				i.getSocket().close();
			}
			for (ClientHandler i : clients) {
				i.getSocket().close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized int getAgents() {
		return agents.size();
	}
	
	public static synchronized List<ClientHandler> getSessionAgents(){
		return sessionagents;
	}

	public static synchronized List<ClientHandler> getSessionclients() {
		return sessionclients;
	}

}