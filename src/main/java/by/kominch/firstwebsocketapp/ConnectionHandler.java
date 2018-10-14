package by.kominch.firstwebsocketapp;

import javax.websocket.Session;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import by.kominch.firstwebsocketapp.Commands.Commands;
import by.kominch.firstwebsocketapp.SocketStorage.Storage;
import by.kominch.firstwebsocketapp.storage.SessionStorage;

public class ConnectionHandler {
	private static final Logger log = Logger.getLogger(ConnectionHandler.class.getSimpleName());

	static {
		PropertyConfigurator.configure("src/main/java/by/kominch/firstwebsocketapp/log4j.properties");
	}

	static void manageConnection(Session session, String msg) {
		msg = msg.replaceAll("\\{", "").replaceAll("\\}", "");
		if (isRegistered(session) || isRegistered2(session)) {
			if (getPersonBySession(session)!=null &&getPersonBySession(session).getConnectedPerson()==null) {
				connect(session);
			}
			leaveChat(session, msg);
			exitSystem(session, msg);
			if (getPersonBySession(session).getConnectedPerson() != null) {
				sendMsg(session,msg);
			}
		} else {
			registerSession(session, msg);
		}
	}
	
	static synchronized void sendMsg(Session from,String msg) {
		if(getPersonBySession(from).getConnectedPerson().getSocket()!=null) {
			System.out.println("Socket");
			getPersonBySession(from).getSession().getAsyncRemote().sendText("{\"user\":\"" + getPersonBySession(from).getStatus() + " "
					+ getPersonBySession(from).getName() + "\",\"message\":" + "\"" + msg + "\"}");
			getPersonBySession(from).sendMsg(msg,getPersonBySession(from).getConnectedPerson());
		}
		if(getPersonBySession(from).getConnectedPerson().getSession()!=null) {
			getPersonBySession(from).getConnectedPerson().getSession().getAsyncRemote().sendText("{\"user\":\"" + getPersonBySession(from).getStatus() + " "
					+ getPersonBySession(from).getName() + "\",\"message\":" + "\"" + msg + "\"}");
			getPersonBySession(from).getSession().getAsyncRemote()
					.sendText("{\"user\":\"" + getPersonBySession(from).getStatus() + " "
							+ getPersonBySession(from).getName() + "\",\"message\":" + "\"" + msg + "\"}");
		}
	} 

	static void registerSession(Session session, String msg) {
		if (msg.startsWith(Commands.REGISTER.getCommand())) {
			if (msg.split(" ").length == 3) {
				if (msg.split(" ")[1].equals("client")) {
					ClientHandler obj = new ClientHandler(session, msg.split(" ")[2], msg.split(" ")[1]);
					obj.setConnectedPerson(null);
					Storage.getSessionclients().add(obj);
					log.info(Storage.getSessionclients().get(Storage.getSessionclients().size() - 1).getStatus() + " "
							+ Storage.getSessionclients().get(Storage.getSessionclients().size() - 1).getName()
							+ " joined to the system");
				}
				if (msg.split(" ")[1].equals("agent")) {
					ClientHandler obj = new ClientHandler(session, msg.split(" ")[2], msg.split(" ")[1]);
					obj.setEnable(true);
					obj.setConnectedPerson(null);
					Storage.getSessionAgents().add(obj);
					log.info(Storage.getSessionAgents().get(Storage.getSessionAgents().size() - 1).getStatus() + " "
							+ Storage.getSessionAgents().get(Storage.getSessionAgents().size() - 1).getName()
							+ " joined to the system");
				}
			}
		} // check on /exit command
	}

	static void leaveChat(Session session, String msg) {
		if (msg.equals(Commands.LEAVE.getCommand())) {
			ClientHandler person = getPersonBySession(session);
			log.info(person.getStatus() + " " + person.getName() + " left the channel");
			log.info(person.getConnectedPerson().getStatus() + " " + person.getConnectedPerson().getName()
					+ " left the channel");
			if (person != null) {
				person.getConnectedPerson().setConnectedPerson(null);
				if (person.getStatus().equals("client")) {
					person.getConnectedPerson().setEnable(true);
					Storage.getSessionAgents().add(person.getConnectedPerson());
					person.setConnectedPerson(null);
				}
				if (person.getStatus().equals("agent")) {
					person.setConnectedPerson(null);
					person.setEnable(true);
					Storage.getSessionAgents().add(person);
				}
			}
		}
	}

	static void exitSystem(Session session, String msg) {
		if (msg.equals(Commands.EXIT.getCommand())) {
			ClientHandler person = getPersonBySession(session);
			log.info(person.getStatus() + " " + person.getName() + " left the system");
			log.info(person.getConnectedPerson().getStatus() + " " + person.getConnectedPerson().getName()
					+ " left the channel");
			if (person != null) {
				person.getConnectedPerson().setConnectedPerson(null);
				if (person.getStatus().equals("client")) {
					person.getConnectedPerson().setEnable(true);
					Storage.getSessionAgents().add(person.getConnectedPerson());
					person.setConnectedPerson(null);
					Storage.getSessionclients().remove(person);
				}
				if (person.getStatus().equals("agent")) {
					person.setConnectedPerson(null);
					Storage.getSessionAgents().remove(person);
				}
			}
		}
	}

	static void connect(Session usersession) {
		ClientHandler person = getPersonBySession(usersession);
		if (person.getStatus().equals("client")) {
			int index = Storage.getSessionclients().indexOf(person);
			ClientHandler object = Storage.getFreeAgent();
			person.setConnectedPerson(object);
			person.getConnectedPerson().setConnectedPerson(person);
			Storage.getSessionclients().set(index, person);
			log.info(person.getStatus() + " " + person.getName() + " joined to the chat");
			log.info(person.getConnectedPerson().getStatus() + " " + person.getConnectedPerson().getName()
					+ " joined to the chat");
		}
	}

	static boolean isRegistered(Session userSession) {
		for (ClientHandler st : Storage.getSessionclients()) {
			if (st.getSession() == userSession)
				return true;
		}
		return false;
	}

	static boolean isRegistered2(Session userSession) {
		for (ClientHandler st : Storage.getSessionAgents()) {
			if (st.getSession() == userSession)
				return true;
		}
		return false;
	}

	static ClientHandler getPersonBySession(Session user) {
		for (ClientHandler st : Storage.getSessionAgents()) {
			if (st.getSession() == user)
				return st;
		}
		for (ClientHandler st : Storage.getSessionclients()) {
			if (st.getSession() == user)
				return st;
		}
		return null;
	}
}
