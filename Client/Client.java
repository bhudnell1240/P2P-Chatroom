package Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import SuperServer.ChatRoom;

public class Client {

	private ChatClientGUI chatView;
	private ChatRoomGUI chatRoomView;
	private ChatServer server;
	private String name;
	private String IP;
	private boolean firstEntry = true;
	private ObjectOutputStream writer;
	private ObjectInputStream inputFromServer;
	private Socket socketServer;
	public static String host = "localhost";
	ArrayList<ChatRoom> chatRoomList;

	public static void main(String[] args) {
		new Client();
	}

	public Client() {
		chatRoomView = new ChatRoomGUI(this);
		server = new ChatServer();
		connectToSuperServer();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static void main() {
		new Client();
	}

	@SuppressWarnings("unchecked")
	public void connectToSuperServer() {
		try {
			socketServer = new Socket(host, ChatServer.PORT_NUMBER);
			writer = new ObjectOutputStream(socketServer.getOutputStream());
			inputFromServer = new ObjectInputStream(socketServer.getInputStream());
			System.out.println("Found server who accepted me");
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null,
					"Could not find server at " + host + " on port " + ChatServer.PORT_NUMBER);
			System.exit(0);
		}

		try {
			chatRoomList = (ArrayList<ChatRoom>) inputFromServer.readObject();
			for (ChatRoom room : chatRoomList) {
				chatRoomView.model
						.addElement(room.getName() + " : " + room.getActiveUsers().size() + " active users\n");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Client lost server");
		}
	}

	public void sendMessageToSuperServer(String string) throws IOException {
		writer.writeUTF(string);
	}

	public void InputActionPerformed(ActionEvent ev) {
		try {
			if (firstEntry) {
				name = chatView.outgoing.getText();
				firstEntry = false;
				writer.writeObject(name + " has joined the chat");
			} else
				writer.writeObject(name + ": " + chatView.outgoing.getText());
			writer.flush();
		} catch (Exception ex) {
		}
		chatView.outgoing.setText("");
		chatView.requestFocus();

	}

	public void selectedChatRoomIndex(int index) throws IOException {
		ChatRoom room;
		if (index == -1 || index == 0) {
			// Create a new chatroom
			// JOptionPane newRoomName = new JOptionPane("Enter new chat room
			// name");
			String newName;
			newName = JOptionPane.showInputDialog("Enter new chat room name");
			room = new ChatRoom(newName);

		} else {
			room = chatRoomList.get(index);
		}
		// System.out.println("Chosen chatroom's name is " + room.getName());
		sendMessageToSuperServer("$Join$" + room.getName());
	}

}