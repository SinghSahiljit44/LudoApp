package ludoApp;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class LudoClient implements Runnable{
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private LudoView view;
    private JFrame frame;
    private boolean token = false;
    private String ip;
    private int port;
    private LudoModel model;
    private boolean initialized = false; 
    private OutputHandler outHandler;
    
    public LudoClient(String ip, int port) throws IOException {
        
    	this.ip =  ip;
    	this.port = port;
    	
    	view = new LudoView();
        
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); 
        frame.revalidate();
        frame.repaint();
        frame.setVisible(true);
        
        
        view.addActionListenerDado(e -> {
        	if(initialized == true && token == true) {
        		try {
					outHandler.sendData((Object)e.getActionCommand());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
        });
        
        view.addActionListenerPedine(e -> {
        	if(initialized == true && token == true) {
        		try {
        			String command = e.getActionCommand();
        			if(model != null ) {
        				outHandler.sendData((Object)command);
        			}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
        	}
        });

    }
    
    public void shutdown() throws IOException, InterruptedException {
		if (!clientSocket.isClosed()) {
			clientSocket.close();
			in.close();
			out.close();
			Thread.sleep(10000);
			System.exit(0);
		}
	}
    
    private static boolean isValidIPAddress(String ip) {
        String ipPattern = 
            "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
        return Pattern.matches(ipPattern, ip);
    }

    @Override
	public void run() {
		try {
			clientSocket = new Socket(ip, port);
			out = new ObjectOutputStream(clientSocket.getOutputStream());
	        in = new ObjectInputStream(clientSocket.getInputStream());
			System.out.println("Creato client socket.");
			outHandler = new OutputHandler();
			Thread t = new Thread(outHandler);
			t.start();

			Object o;
			
			while ((o = in.readObject()) != null) { 
				switch (o.getClass().getSimpleName()) {
				case "LudoModel":
					model = (LudoModel) o;
					if(initialized == false) {
						view.posPedine(model.getPosizioniPedine());
						SwingUtilities.invokeLater(() -> {
						    frame.getContentPane().add(view);
						    frame.revalidate();
						    frame.repaint();
						});
						frame.setVisible(true);;
						initialized = true;
					}else {
						SwingUtilities.invokeLater(() -> {
							if(model.getDiceRolled() == false) view.dadoAnimation(model.getValoreDado());
							view.posPedine(model.getPosizioniPedine());
							view.stepNext();
							frame.revalidate();
						    frame.repaint();
						});
					}
					break;
				case "Boolean":
					token = (Boolean) o;
					break;
				case "ArrayList":
					ArrayList<Colore> leaderBoard = (ArrayList<Colore>) o;
					view.gameFinished(leaderBoard);
					break;
				}
			}	
			
		}
		catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			try {
				shutdown();
			} catch (IOException | InterruptedException e1) {
				e1.printStackTrace();
			} 
		}
	} 

    public static void main(String[] args) throws IOException {
    	Scanner scanner = new Scanner(System.in); 
    	
    	String ip;
        while (true) {
             System.out.println("Inserisci l'indirizzo IP:");
             ip = scanner.nextLine();
             if (isValidIPAddress(ip)) {
                 break; // IP is valid, exit the loop
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
         
         try {
             LudoClient client = new LudoClient(ip, port);
             client.run();
             //client.start();
         } catch (IOException e) {
             e.printStackTrace();
         }
         scanner.close();
    }
     
    class OutputHandler implements Runnable {

		private boolean finished = false;

		@Override
		public void run() {	
			try {
				while (!finished) {
					
				} 
			} catch (Exception e) {
				try {
					shutdown();
				} catch (IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
			
		}
		
		public void sendData (Object s) throws IOException {
			out.writeObject(s);
			out.flush();
			out.reset();
		}
		
	}
    
}