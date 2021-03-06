package Client;

import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer implements Runnable {

	public static int PORT_NUMBER = 4006;

	public static void main(String[] args) {
		new ChatServer();
	}

	private ArrayList<ObjectOutputStream> clientOutputStreams;

	public ChatServer() {
		clientOutputStreams = new ArrayList<ObjectOutputStream>();
	}

	@Override
	public void run() {
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSock = new ServerSocket(PORT_NUMBER);
			while (true) {
				Socket clientSocket = serverSock.accept();
				ObjectOutputStream writer = new ObjectOutputStream(clientSocket.getOutputStream());
				clientOutputStreams.add(writer);
				Thread t = new Thread(new LoopToReadClientsInputToServerInNewThread(clientSocket, clientOutputStreams));
				t.start();
				System.out.println("got a connection");
			}
		} catch (Exception ex) {
		}
	}
}