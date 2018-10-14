package by.kominch.firstwebsocketapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.websocket.Session;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import by.kominch.firstwebsocketapp.Commands.AbstractCommand;
import by.kominch.firstwebsocketapp.Commands.Commands;
import by.kominch.firstwebsocketapp.Commands.ExitCommand;
import by.kominch.firstwebsocketapp.Commands.LeaveCommand;
import by.kominch.firstwebsocketapp.Commands.RegisterCommand;
import by.kominch.firstwebsocketapp.SocketStorage.Storage;
import by.kominch.firstwebsocketapp.storage.SessionStorage;

/**
 * @author Вадим
 * 
 *         Class which create client thread on the server side and handle io
 *         streams.
 * 
 * @param incoming
 *            Client socket which is created after connection attempt
 * @param scanner
 *            Input stream from socket
 * @param pr
 *            Output stream to the socket
 * @param name
 *            name of the client or agent
 * @param id
 *            id of the client or agent
 * @param role
 *            role of the connected person(agent or client)
 * @param connectedPerson
 *            object of connected client or agent
 * @param log
 *            logger for logging important events in the file or on the console
 */
public class ClientHandler implements Runnable {

	private Storage storage;
	private Socket incoming;
	private Session session;
	private Scanner scanner;
	private PrintWriter pr;
	private String name;
	private int id;
	private String role;
	private ClientHandler connectedPerson;
	private AbstractCommand registercommand;
	private AbstractCommand leavecommand;
	private AbstractCommand exitcommand;
	private boolean enable;
	private static final Logger log = Logger.getLogger(ClientHandler.class.getSimpleName());

	static {
		PropertyConfigurator.configure("src/main/java/by/kominch/firstwebsocketapp/log4j.properties");
	}

	public synchronized Scanner getScanner() {
		return scanner;
	}

	public synchronized Storage getStorage() {
		return storage;
	}

	public synchronized PrintWriter getPrintWriter() {
		return pr;
	}

	public synchronized String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Socket getSocket() {
		return incoming;
	}

	public String getStatus() {
		return this.role;
	}

	public ClientHandler getConnectedPerson() {
		return connectedPerson;
	}

	public void setConnectedPerson(ClientHandler connectedPerson) {
		this.connectedPerson = connectedPerson;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	/**
	 * ClientHandler constructor.It gets two parameters socket and id. After that
	 * two streams are get and included in more convenient classes. If attempt was
	 * failed socket is closed.
	 * 
	 * @param i
	 *            socket of the client/agent
	 * @param id
	 *            id of the client/agent
	 * @see PrintWriter output stream writer
	 * @see Scanner input stream reader
	 */
	public ClientHandler(Socket i, int id, Storage storage) {
		registercommand = new RegisterCommand(this, storage);
		leavecommand = new LeaveCommand(this, storage);
		exitcommand = new ExitCommand(this, storage);
		incoming = i;
		this.id = id;
		this.storage = storage;
		if (incoming != null) {
			try {
				scanner = new Scanner(incoming.getInputStream());
				pr = new PrintWriter(incoming.getOutputStream(), true);
			} catch (IOException e) {
				try {
					if (incoming != null)
						incoming.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	public ClientHandler(Session session, String name, String role) {
		this.session = session;
		this.name = name;
		this.role = role;
	}

	/**
	 * Method for sending messages.It puts message to the output stream and flushes
	 * it. Method is synchronized.
	 * 
	 * @param Msg
	 *            message which send
	 */
	public synchronized void sendMsg(String Msg, ClientHandler receiver) {
		if (receiver.getSocket() != null) {
			if (receiver.pr != null) {
				receiver.pr.println(receiver.connectedPerson.role + " " + receiver.connectedPerson.name + ": " + Msg);
				receiver.pr.flush();
			}
		}
		if (receiver.getSession() != null) {
			receiver.getSession().getAsyncRemote()
					.sendText("{\"user\":\"" + this.role + " " + this.name + "\",\"message\":" + "\"" + Msg + "\"}");
		}
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * Method runs client thread. Client registers and then is waiting for agent.
	 * Agent don't wait and go the loop. After client connected to the client chat
	 * starts. Client receive message and check it for specific command. If one of
	 * them is found, thread logs it into file and on the console. If command
	 * "/leave" met we only disconnect from this agent/client and waiting for other
	 * agent. If command "/exit" met client/agent disconnect and exit from two
	 * loops. Second person if it is client wait for other agent,if it is agent, he
	 * returns to the agent's list. For exiting person all streams and socket are
	 * closed. Otherwise if client/agent connected, message send.
	 * 
	 * @see java.lang.Runnable#run()
	 * @see Commands
	 */
	public void run() {
		if (register()) {
			boolean finish = false;
			do {
				if (connect()) {//scanner.hasNextLine() &&
					while ( connectedPerson != null) {
						String line = scanner.nextLine();
						if (leavechat(line)) {
							break;
						}
						if (exitFromSystem(line)) {
							finish = true;
							break;
						}
						if (connectedPerson != null) {
							sendMsg(line, connectedPerson);
						}
					}
				} else {
					finish = true;
				}
			} while (!finish);
		}
		try

		{
			if (incoming != null)
				incoming.close();
			scanner.close();
			pr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method which register person in the system. It receive string with command
	 * "/register client/agent [name]" and split it into three words checking every
	 * of them for their correctness. If string is not correct user get message. If
	 * registration was correct, thread log this event to the file and on the
	 * console.
	 * 
	 * @see Commands
	 */
	public synchronized boolean register() {
		return registercommand.execute();
	}

	public synchronized boolean leavechat(String msg) {
		if (msg.trim().equals(Commands.LEAVE.getCommand())) {
			return leavecommand.execute();
		}
		return false;
	}

	public synchronized boolean exitFromSystem(String msg) {
		if (msg.trim().equals(Commands.EXIT.getCommand())) {
			return exitcommand.execute();
		}
		return false;
	}

	/**
	 * Method which connect client with agent ,if free agents are available.
	 * Connection created after client enters first word. Thread log this event into
	 * file and on the console.
	 * 
	 * @return
	 */
	public synchronized boolean connect() {
		if (this.role.equals("client")) {
			ClientHandler e = storage.getFreeAgent();
			String str = "";
			if (scanner.hasNextLine())
				str = scanner.nextLine();
			if (!str.equals("/exit")) {
				while (e == null) {
					e = storage.getFreeAgent();
				}
				log.info(this.role + " " + this.name + " started the chat");
				this.pr.println("You are connected to agent " + e.getName());
				this.connectedPerson = e;
				e.connectedPerson = this;
				log.info(e.role + " " + e.name + " joined to the chat");
				sendMsg(str, this.connectedPerson);
			} else {
				log.info(this.getStatus() + " " + this.getName() + " left the system");
				return false;
			}
		}
		return true;
	}

	/**
	 * Method which disconnect client form agent in the case of "/leave" command.
	 */
}
