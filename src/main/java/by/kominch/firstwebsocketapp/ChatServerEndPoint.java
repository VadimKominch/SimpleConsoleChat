package by.kominch.firstwebsocketapp;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import by.kominch.firstwebsocketapp.SocketStorage.Storage;
import by.kominch.firstwebsocketapp.storage.SessionStorage;

/**
* ChatServer
* @author Vadim
*/
@ServerEndpoint(value="/chat")
@Singleton
public class ChatServerEndPoint {
   
   static Set<Session> userSessions = Collections.synchronizedSet(new HashSet<Session>());
   static Set<SessionStorage> agents = Collections.synchronizedSet(new HashSet<SessionStorage>());
   static Set<SessionStorage> clients = Collections.synchronizedSet(new HashSet<SessionStorage>());
	private static final Logger log = Logger.getLogger(ClientHandler.class.getSimpleName());

	static {
		PropertyConfigurator.configure("src/main/java/by/kominch/firstwebsocketapp/log4j.properties");
	}
   /**
    * Callback hook for Connection open events. 
    * 
    * This method will be invoked when a client requests for a 
    * WebSocket connection.
    * 
    * @param userSession the userSession which is opened.
    */
   @OnOpen
   public void onOpen(Session userSession) {
       userSessions.add(userSession);
   }
    
   /**
    * Callback hook for Connection close events.
    * 
    * This method will be invoked when a client closes a WebSocket
    * connection.
    * 
    * @param userSession the userSession which is opened.
    */
   @OnClose
   public void onClose(Session userSession) {
	   userSessions.remove(userSession);
       ClientHandler person = ConnectionHandler.getPersonBySession(userSession);
		if (person != null) {
			log.info(ConnectionHandler.getPersonBySession(userSession).getStatus()+" "+ConnectionHandler.getPersonBySession(userSession).getName()+" left the system");
			if(person.getConnectedPerson()!=null) {
				log.info(ConnectionHandler.getPersonBySession(userSession).getConnectedPerson().getStatus()+" "+ConnectionHandler.getPersonBySession(userSession).getConnectedPerson().getName()+" left the channel");
				person.getConnectedPerson().setConnectedPerson(null);
			}
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
    
   /**
    * Callback hook for Message Events.
    * 
    * This method will be invoked when a client send a message.
    * 
    * @param message The text message
    * @param userSession The session of the client
    */
   @OnMessage
   public synchronized void onMessage(String message, Session userSession) {
	   	//System.out.println("Message recieved! "+message+" "+userSession.getId());
	       ConnectionHandler.manageConnection(userSession, message.split(",")[1].split(":")[1].replaceAll("\"", ""));
   }
}
