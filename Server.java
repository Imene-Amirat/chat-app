package chat_app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class  Server {
	
	private int port = 7777;
	private ServerSocket ss;
	private List<ClientInfo> clientList = new ArrayList<ClientInfo>();
	
	public void startServer() {
		try {
			ss = new ServerSocket(port);
			System.out.println("Server run on port : "+ port);
			
			while(true) {
				Socket socketClient = ss.accept();
				System.out.println("new client connected ");
				
				BufferedReader fromClient = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
				PrintWriter toClient = new PrintWriter(socketClient.getOutputStream(), true);
				
				String username = fromClient.readLine();
				System.out.println(username + " joined");
				
				ClientInfo client = new ClientInfo(socketClient, username);
				clientList.add(client);
				
				for(ClientInfo c: clientList) {
					if(!c.socket.equals(socketClient)) {
						PrintWriter p = new PrintWriter(c.socket.getOutputStream(), true);
						p.println(username + " joined");
					}

				}
				
				
				Thread clientThread = new Thread(()->{
					try {
						String message;
						
						while((message = fromClient.readLine()) != null) {
							if(message.equalsIgnoreCase("quit")) {
								clientList.remove(client);
								for(ClientInfo c: clientList) {
									if(!c.socket.equals(socketClient)) {
										PrintWriter p = new PrintWriter(c.socket.getOutputStream(), true);
										p.println(username+" left");
									}
								}
								
								socketClient.close();
								break;
							} else {
								System.out.println(username + " : " + message);
								for(ClientInfo c: clientList) {
									if(!c.socket.equals(socketClient)) {
										PrintWriter p = new PrintWriter(c.socket.getOutputStream(), true);
										p.println(username+" : "+message);
									}
								}
							}
						}				
					} catch(IOException e) {
						System.out.println("server error" + e.getMessage());
					}
				});
				
				clientThread.start();
			}
			
		} catch(IOException e) {
			System.out.println("server error" + e.getMessage());
		}
	}
	
	public static void main(String[] args) {
		Server server = new Server();
		server.startServer();
	}

}

class ClientInfo {
	Socket socket;
	String username;
	
	public ClientInfo(Socket socketClient, String username) {
		this.socket = socketClient;
		this.username = username;
	}
}
