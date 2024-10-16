package ludoApp;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class LudoServer {
    private int porta;
    private ArrayList<ClientHandler> clients;
    private LudoModel model;
    private int numeroGiocatori = 0; 
    private final int maxGiocatori = 4;
    private String ipAddress;
    private final AtomicBoolean ack = new AtomicBoolean(false);
    private final AtomicReference<String> action = new AtomicReference<>(null);
    
    public LudoServer(String Ip, int porta) {
    	clients = new ArrayList<>();
        this.porta = porta;
        this.ipAddress = Ip;
    }

    public void waitPlayers() throws IOException {
    	InetAddress address = InetAddress.getByName(ipAddress);
		try(
			ServerSocket server = new ServerSocket(porta, maxGiocatori, address); 
    	 ){
    		System.out.println("Server in ascolto...");
    		while (numeroGiocatori < maxGiocatori) {
    			Socket clientSocket = server.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this, numeroGiocatori);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
                System.out.println(numeroGiocatori + " client connesso");                
                numeroGiocatori++;
             }
            System.out.println("Numero massimo di giocatori raggiunto!");
	        }catch(IOException e){
    		System.out.println("Errore durante la comunicazione! " + e);
    		e.printStackTrace();
	    }
        
    }
    
    public void start() throws IOException {
    	model = new LudoModel(numeroGiocatori, false);
    	broadcastObjectState(model);
    	while(true) {
    		model.lanciaDado();
    		model.rotateTurno();
    		
    		//CASO IN CUI NON POSSO MUOVERE ALCUNA PEDINA 
    		if (model.getIfInSpecialCondition() == true) {
    			waitUsersClickOnDice();
    			broadcastObjectState(model);
    			continue;
    		}
			
    		//CASO IN CUI POSSO MUOVERE UNA PEDINA
    		waitUsersClickOnDice();
			broadcastObjectState(model);
			setToken((Boolean)true, model.getCurrentPlayerId());
			model.setDiceRolled(true);
    		while(true) {
    			if(model.isActionValid(getAction()) == true) {
    				setToken((Boolean)false, model.getCurrentPlayerId());
        			model.stepNext();
        			broadcastObjectState(model);
        			model.setDiceRolled(false);
        			setAction(null);
        			break;
        		}
    		}
    		
    		//LEADERBOARD ALLA FINE DELLA PARTITA
    		if(model.getLeaderBoard().size() == 4) {
    			broadcastObjectState(model.getLeaderBoard());
    			break;
    		}
    	}
    }

	private void waitUsersClickOnDice() throws IOException {
		setToken((Boolean)true, model.getCurrentPlayerId());
		while(true) {
			if(ack.get()) break;
		}
		ack.set(false);
		setToken((Boolean)false, model.getCurrentPlayerId());
	}
    
    
    public void setAction(String action) {
        this.action.set(action); 
    }
    
    public String getAction() {
        return this.action.get();
    }

    
    public void setAck(boolean ack) {
        this.ack.set(ack);
    }
    
    private void setToken(Object value, int clientNumber) throws IOException {
    	for(ClientHandler client: clients) {
    		if (client.getClientNumber() == clientNumber) {
    			client.sendObject(value);
    		}
    	}
    }
    
    
    private void broadcastObjectState(Object state) throws IOException {
    	for (ClientHandler client: clients) {
    		client.sendObject(state);
    	}
    }
    
    
    private static boolean isValidIPAddress(String ip) {
        String ipPattern = 
            "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
        return Pattern.matches(ipPattern, ip);
    }
    
    
    public static void main(String[] args) throws IOException {
    	Scanner scanner = new Scanner(System.in);
        
        String ip;
        while (true) {
            System.out.println("Inserisci l'indirizzo IP:");
            ip = scanner.nextLine();
            
            if (isValidIPAddress(ip)) {
                break; 
            } else {
                System.out.println("Indirizzo IP non valido. Riprova.");
            }
        }
        
        int port;
        while (true) {
            try {
                System.out.println("Inserisci la porta:");
                port = Integer.parseInt(scanner.nextLine());
                
                if (port >= 1024 && port <= 65535) {
                    break;
                } else {
                    System.out.println("La porta deve essere compresa tra 1024 e 65535. Riprova.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Errore nel formato del numero. Inserisci un numero valido.");
            }
        }
        
        LudoServer ludoServer = new LudoServer(ip, port);
        
        try {
            ludoServer.waitPlayers();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ludoServer.start();
        
        scanner.close();
    }
}

