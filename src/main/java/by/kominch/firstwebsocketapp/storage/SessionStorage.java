package by.kominch.firstwebsocketapp.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.websocket.Session;

import by.kominch.firstwebsocketapp.ClientHandler;

public class SessionStorage {
	/**
	 * @param session
	 * @param name
	 * @param role
	 */
	static List<SessionStorage> agents = Collections.synchronizedList(new ArrayList<SessionStorage>());
	static List<SessionStorage> clients = Collections.synchronizedList(new ArrayList<SessionStorage>());
	
	public SessionStorage(Session session) {
		this.session = session;
	}

	private Session session;
	private String name;
	private String role;
	private SessionStorage connectedPerson;
	private ClientHandler socketclient;
	private boolean enable;
	/**
	 * @param session
	 * @param name
	 * @param role
	 */
	public Session getSession() {
		return session;
	}

	public ClientHandler getSocketclient() {
		return socketclient;
	}

	public void setSocketclient(ClientHandler socketclient) {
		this.socketclient = socketclient;
	}

	public SessionStorage getConnectedPerson() {
		return connectedPerson;
	}

	public void setConnectedPerson(SessionStorage connectedPerson) {
		this.connectedPerson = connectedPerson;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	
}
