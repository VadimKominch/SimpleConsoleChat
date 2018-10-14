package by.kominch.firstwebsocketapp;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.glassfish.tyrus.server.Server;


public class WebServer {

   public static void main(String[] args) {
	   MyServer sserver = new MyServer();
	   sserver.start();
       Server server = new Server("localhost", 8081, "/websockets", ChatServerEndPoint.class);
       try {
    	   server.start();
   			
           BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
           System.out.print("Please press a key to stop the server.");
           reader.readLine();
       } catch (Exception e) {
           throw new RuntimeException(e);
       } finally {
           server.stop();
       }
   }
}
