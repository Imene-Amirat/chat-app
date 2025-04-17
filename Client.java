package chat_app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	
	private String username, ip;
	private int port;
	
	public Client(String username, String ip, int port) {
		this.username = username;
		this.ip = ip;
		this.port = port;
	}

	public void start() {
		try {
			Socket socket = new Socket(ip,port);
			
			BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
			
			toServer.println(username);
			
			Thread sender = new Thread(()->{
				try {
					Scanner s = new Scanner(System.in);
					while(true) {
						String message = s.nextLine();
						toServer.println(message);
						
						if(message.equalsIgnoreCase("quit")) {
							socket.close();
							break;
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			
			sender.start();
			
			Thread receive = new Thread(()->{
				try {
					String message;
					while((message = fromServer.readLine()) != null) {
						System.out.println(message);
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			
			receive.start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		System.out.print("Enter your username ");
		String username = sc.nextLine();
		
		System.out.print("Enter server IP ");
		String ip = sc.nextLine();
		
		System.out.print("Enter server Port ");
		int port = sc.nextInt();
		
		Client client = new Client(username,ip,port);
		client.start();

	}

}
