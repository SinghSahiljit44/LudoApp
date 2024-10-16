package ludoApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class OnlinePlayersWindow extends JPanel implements MouseMotionListener, MouseListener {
	
	Graphics2D g2;
	boolean clicked = false;
	int w;
	int h;
	int lx;
	int ly;
	String result ;
	String arrowClicked;
	String resultInvia;
	ArrayList<Integer> inviaCoordinate = new ArrayList<>();
	ArrayList<Integer> arrowCoordinate = new ArrayList<>();
	ArrayList<ActionListener> eventListeners = new ArrayList<>();
    boolean isInviaActive = false;
    
    private String userInputIP = "";
    private String userInputPorta = ""; 
    ArrayList<Integer> ipCoordinate = new ArrayList<>();
    ArrayList<Integer> portaCoordinate = new ArrayList<>();
    
    int mouseCont = 0;
    
    boolean serverAvviato = false;
    
    public OnlinePlayersWindow() {
      setPreferredSize(new Dimension(400, 300));
      this.addMouseListener(this);
	  this.addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        w = getWidth();
        h = getHeight();
        
        g2.setColor(new Color(153, 255, 255));
        g2.fillRect(0, 0, w, h);

        // 2/3 della pagina
        int w1 = w * 2 / 3;
        int rectH = h / 4;
        
        //freccetta 
        g2.setColor(new Color(153, 255, 255));
        arrowCoordinate.addAll(Arrays.asList(0, 0, w1/10, w1/10));
        g2.fillRect(0, 0, w1/10, w1/10);
        g2.setColor(Color.RED);
        checkArea(0, 0, w1/10, w1/10, "Indietro");
        g2.fillPolygon(new int[]{0, w1/20, w1/20},new int[]{w1/20,0,w1/10}, 3);
        g2.fillRect(w1/20, w1/40, w1/20, w1/20);
        
        // Testo al centro del rettangolo
        g2.setColor(Color.BLACK);
        int fontSize = Math.min(w1, rectH) / 3;
        g2.setFont(new Font("Arial", Font.BOLD, fontSize));
        
        String txt = "Inserire dati server:";
        int txtW = g2.getFontMetrics().stringWidth(txt);
        int txtH = g2.getFontMetrics().getHeight();
        int txtX = (w1 - txtW) / 2;
        int txtY = rectH - txtH;
        g2.drawString(txt, txtX, txtY);
        
        //2 rettangoli
        lx = w1/3;
        ly = (h*3/8);
        
        // 1/3 della pagina
        int w2 = w - w1;
        
        //coordinate invia
        inviaCoordinate.addAll(Arrays.asList(w1, (5*h)/6, w2, h/6));
        
        g2.setColor(Color.PINK);
        if(resultInvia != null && resultInvia.equalsIgnoreCase("B")) g2.setColor(Color.WHITE);
        checkArea(w1, (5*h)/6, w2, h/6, "Invia");
        g2.fillRect(w1, (5*h)/6, w2, h/6);
        
        creaScrittaCentrataConHighlight(w1, (5*h)/6, w2, h/6, "INVIA", Color.WHITE, Color.YELLOW);
        
        drawTextInput(g2, lx, h/4 + ly/4, lx, ly/2, userInputIP);
        ipCoordinate.addAll(Arrays.asList(lx, h/4 + ly/4, lx, ly/2));
        drawTextInput(g2, lx, h/4 + ly, lx, ly/2, userInputPorta);
        portaCoordinate.addAll(Arrays.asList(lx, h/4 + ly, lx, ly/2));
        
        if (serverAvviato) {
            drawServerMessage(g2);
        }
    }

	@Override
	public void mouseClicked(MouseEvent e){
		
		//freccetta per andare indietro
		int ax = arrowCoordinate.get(0);
		int ay = arrowCoordinate.get(1);
		int alx = arrowCoordinate.get(2);
		int aly = arrowCoordinate.get(3);
		
		arrowClicked = checkArea(ax, ay, alx, aly, "arrow");
		
		int ipPutX = ipCoordinate.get(0);
	    int ipPutY = ipCoordinate.get(0);
	    int ipPutWidth = ipCoordinate.get(0);
	    int ipPutHeight = ipCoordinate.get(0);
	    
	    if (e.getX() >= ipPutX && e.getX() <= ipPutX + ipPutWidth &&
	        e.getY() >= ipPutY && e.getY() <= ipPutY + ipPutHeight) {
	    	String input;
	    	do {
	    		input = JOptionPane.showInputDialog(this, "Inserisci l'IP");
	    	}while(!isValidIPAddress(input));
	        
	        if (input != null) {
	            userInputIP = input;
	            repaint();
	        }
	    }
		
	    int portaPutX = portaCoordinate.get(0);
	    int portaPutY = portaCoordinate.get(0);
	    int portaPutWidth = portaCoordinate.get(0);  
	    int portaPutHeight = portaCoordinate.get(0);  
	    
	    if (e.getX() >= portaPutX && e.getX() <= portaPutX + portaPutWidth &&
	        e.getY() >= portaPutY && e.getY() <= portaPutY + portaPutHeight) {
	        String input;
	        int n;
	        do {
	        	input = JOptionPane.showInputDialog(this, "Inserisci la porta");
	        	n = Integer.parseInt(input);
	        }while(!(n >= 1024 && n <= 65535));
	        
	        if (input != null) {
	            userInputPorta = input;
	            repaint();
	        }
	    }
	    
	  //invia
	  		int gx = inviaCoordinate.get(0);
	  		int gy = inviaCoordinate.get(1);
	  		int glx = inviaCoordinate.get(2);
	  		int gly = inviaCoordinate.get(3); 
	    
	  		resultInvia = checkArea(gx, gy, glx, gly, "Invia");
	  		
	    if (e.getX() >= gx && e.getX() <= gx + glx &&
	        e.getY() >= gy && e.getY() <= gy + gly) {
	        
	    	 serverAvviato = true;  // Imposta il messaggio per visualizzarlo
	         repaint();  // Ridisegna immediatamente per mostrare il messaggio

	         // Avvia il server in un nuovo thread per non bloccare l'interfaccia utente
	         new Thread(() -> {
	             LudoServer server = new LudoServer(userInputIP, Integer.parseInt(userInputPorta));

	             try {
	               
	                 Thread.sleep(1000);  

	                 server.waitPlayers(); 
	             } catch (IOException | InterruptedException er) {
	                 er.printStackTrace();
	             }

	             try {
	                 server.start(); 
	             } catch (IOException e1) {
	                 e1.printStackTrace();
	             }
	         }).start(); 
	        
	        
	        
	    }
	    
		fireEvents();
	
	}
	
	private void drawServerMessage(Graphics2D g2) {
		
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect(0, 0, getWidth(), getHeight());
		
        String message = "Server avviato, non chiudere la schermata";
        g2.setFont(new Font("Arial", Font.BOLD, 20)); 
        g2.setColor(Color.BLACK);  

        // Calcola le coordinate per centrare il testo
        int textWidth = g2.getFontMetrics().stringWidth(message);
        int textHeight = g2.getFontMetrics().getHeight();
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() - textHeight) / 2 + textHeight;

        // Disegna il testo centrato
        g2.drawString(message, x, y);
    }
	
	private static boolean isValidIPAddress(String ip) {
        String ipPattern = 
            "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
        return Pattern.matches(ipPattern, ip);
    }
	
	private void fireEvents() {
		for(ActionListener listener : eventListeners) {
			
			if(arrowClicked != null && arrowClicked.equalsIgnoreCase("arrow")) {
				ActionEvent stringEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, arrowClicked);
				listener.actionPerformed(stringEvent);
				arrowClicked = null;
			}
			
			if(resultInvia != null && resultInvia.equalsIgnoreCase("invia")) {
				ActionEvent stringEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, resultInvia);
				listener.actionPerformed(stringEvent);
				resultInvia = null;
			}
	    }
	}
    
    private void creaScrittaCentrataConHighlight(int x, int y, int lx, int ly, String nomeRett, Color colorTxt, Color highlightTxtColor) {
    	int fontSize = Math.min(lx / 4, h * 3 / 8) / 3;
        g2.setFont(new Font("Arial", Font.BOLD, fontSize));

        g2.setColor(colorTxt);
        if (result != null && result.equalsIgnoreCase(nomeRett))
            g2.setColor(highlightTxtColor);

        int txtW = g2.getFontMetrics().stringWidth(nomeRett);
        int txtH = g2.getFontMetrics().getHeight();
        int txtX = x + (lx - txtW) / 2;
        int txtY = y + (ly - txtH) / 2 + g2.getFontMetrics().getAscent();
        g2.drawString(nomeRett, txtX, txtY);
    }

    private void drawTextInput(Graphics2D g2, int x, int y, int width, int height, String inputTxt) {
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, width, height);  // Disegna lo spazio per l'input

        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, width, height);  // Disegna il bordo

        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString(inputTxt, x + 10, y + height / 2);  // Visualizza il testo inserito
    }
    
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	Point mousePosition;
	@Override
	public void mouseMoved(MouseEvent e) {
		mousePosition = e.getPoint();
		this.repaint();
	}
	
	private String checkArea(int x, int y, int lunghezza, int altezza, String area) {
		if(mousePosition != null && mousePosition.getX() >= x && mousePosition.getX() <= x + lunghezza && mousePosition.getY() >= y && mousePosition.getY() <= y + altezza) {
			g2.setColor(Color.WHITE);
			return area;
		}
		return "A";
	}
	
	public void addActionListener(ActionListener l) {
		eventListeners.add(l);
	}

}
