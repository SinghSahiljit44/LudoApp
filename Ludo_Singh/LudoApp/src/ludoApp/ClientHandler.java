package ludoApp;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private LudoServer server;
    private ObjectOutputStream out;
    private ObjectInputStream in; 
    private final int clientNumber;

    public ClientHandler(Socket socket, LudoServer server, int clientNumber) throws IOException {
        this.socket = socket;
        this.server = server;
        this.clientNumber = clientNumber;
        try {
        	out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Ricevo msg dal client
    @Override
    public void run() {
        try {
            Object o;
			
			while ((o = in.readObject()) != null) { 
				switch (o.getClass().getSimpleName()) {
				case "String":
					String command = (String) o;
					if(command.equalsIgnoreCase("dadoCliccato")) {
						server.setAck(true);
					}else {
						server.setAction(command);
					}
					break;
				}
			}
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error handling client: " + e);
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public int getClientNumber() {
    	return clientNumber;
    }
    
	public void sendObject(Object state) throws IOException {
		out.writeObject(state);
        out.flush();
        out.reset();
	}
}
