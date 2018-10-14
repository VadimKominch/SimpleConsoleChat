package by.kominch.firstwebsocketapp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.glassfish.tyrus.server.Server;
//import by.touchsoft.clientagentconsolechat.Storage.Storage;

import by.kominch.firstwebsocketapp.SocketStorage.Storage;

/**
 * @author Вадим
 *	Server class. It gets socket connection from client and take it to the ClientHandler in the infinite loop.
 *	
 *	@param serverocket   serversocket for starting listener on one port.
 *	@param clientsocket  socket for client connection
 *	@param clients		 synchronized list of the clients
 *	@param agents		 synchronized list of the agents
 *	@param id			 id of current connection 
 */
public class MyServer {
	private ServerSocket serversocket = null;
	private Socket clientsocket = null;
	private AtomicInteger id = new AtomicInteger(0);
	private Scanner keyboard = new Scanner(System.in);
	private Storage storage;
	
	public synchronized int getId() {
		return id.get();
	}
		
	public Storage getStorage() {
		return storage;
	}

	public synchronized void setScanner(Scanner scanner) {
		this.keyboard = scanner;
	}
	
	public synchronized boolean isNullConnect() {
		return serversocket==null?true:false;
	}
	public synchronized boolean isNullConnect1() {
		return clientsocket==null?true:false;
	}
	
	/**
	 * Server class constructor. Create listener on one port. If exception occurs close serversocket and exit app.
	 */
	public MyServer() {
		try {
			serversocket = new ServerSocket(8082);
			storage = new Storage();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				serversocket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				System.exit(1);
			}
			System.exit(1);
		}
	}


	/**
	 * Method which receive client connections and send it to ClientHandler. After that client thread starts.
	 * if exception occurs, all lists clear, sockets closed.
	 */
	public synchronized void start(){
		Thread working = new Thread(new Runnable() {
			public void run() {
				try {
					while (true) {
						clientsocket = serversocket.accept();
						ClientHandler ch = new ClientHandler(clientsocket, id.getAndIncrement(),storage);
						Thread t = new Thread(ch);
						t.start();
					}
				} catch (IOException e) {
					storage.deleteAgentList();
					storage.deleteCLientList();
						try {
							if(clientsocket!=null)
							clientsocket.close();
							serversocket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
							System.exit(1);
						}
					}
			}});
		
		
		Thread sendMessage = new Thread(new Runnable() {
			public void run() {
				while (true) {
					String output = keyboard.nextLine();
					if (output.equals("/exit")) {
						keyboard.close();
						break;
					}
				}
				try {
					storage.closeConnections();
					storage.deleteAgentList();
					storage.deleteCLientList();
					if (serversocket != null)
						serversocket.close();
					System.exit(1);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
				System.exit(1);
			}
		});
			working.start();
			sendMessage.start();
	}
}
