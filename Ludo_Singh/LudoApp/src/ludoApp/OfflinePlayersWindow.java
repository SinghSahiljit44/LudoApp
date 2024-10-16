package ludoApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class OfflinePlayersWindow extends JPanel implements MouseMotionListener, MouseListener{
	
	Graphics2D g2;
	boolean clicked = false;
	int w;
	int h;
	int lx;
	int ly;
	String result;
	String resultBot;
	String arrowClicked;
	String resultGioca;
	HashMap<String, ArrayList<Integer>> mappaAree = new HashMap<>();
	ArrayList<Integer> botCoordinate = new ArrayList<>();
	ArrayList<Integer> giocaCoordinate = new ArrayList<>();
	ArrayList<Integer> arrowCoordinate = new ArrayList<>();
	ArrayList<ActionListener> eventListeners = new ArrayList<>();

	
    public OfflinePlayersWindow() {
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

        // 2/3 della pagina
        int w1 = w * 2 / 3;
        g2.setColor(Color.BLUE);
        g2.fillRect(0, 0, w1, h);

        // rettangolo che contiene "Quanti giocatori?"
        g2.setColor(Color.GREEN);
        int rectH = h / 4;
        g2.fillRect(0, 0, w1, rectH);
        
        //freccetta 
        g2.setColor(Color.GRAY);
        arrowCoordinate.addAll(Arrays.asList(0, 0, w1/10, w1/10));
        g2.fillRect(0, 0, w1/10, w1/10);
        g2.setColor(Color.RED);
        checkArea(0, 0, w1/10, w1/10, "Indietro");
        g2.fillPolygon(new int[]{0, w1/20, w1/20},new int[]{w1/20,0,w1/10}, 3);
        g2.fillRect(w1/20, w1/40, w1/20, w1/20);
        
        // Testo al centro del rettangolo
        g2.setColor(Color.WHITE);
        int fontSize = Math.min(w1, rectH) / 3;
        g2.setFont(new Font("Arial", Font.BOLD, fontSize));
        
        String txt = "Quanti giocatori?";
        int txtW = g2.getFontMetrics().stringWidth(txt);
        int txtH = g2.getFontMetrics().getHeight();
        int txtX = (w1 - txtW) / 2;
        int txtY = rectH - txtH;
        g2.drawString(txt, txtX, txtY);
        
        //4 rettangoli
        lx = w1 / 2;
        ly = (h*3/4)/2;
        
	    mappaAree.put("1", new ArrayList<>(Arrays.asList(0, h/4, lx, ly)));
	  	mappaAree.put("2", new ArrayList<>(Arrays.asList(lx, h/4, lx, ly)));
	  	mappaAree.put("3", new ArrayList<>(Arrays.asList(0, h/4 + ly, lx, ly)));
	    mappaAree.put("4", new ArrayList<>(Arrays.asList(lx, h/4 + ly, lx, ly)));
        
	    //R1
        g2.setColor(Color.CYAN);
        String rect1 = "1";
        if(result != null && result.equalsIgnoreCase(rect1)) g2.setColor(Color.WHITE);
        checkArea(0, h/4, lx, ly, rect1);
        g2.fillRect(0, h/4, lx, ly);
        
        fontSize = Math.min(w1/2, h*3/8) / 3;
        g2.setFont(new Font("Arial", Font.BOLD, fontSize));
        
        creaScrittaCentrataConHighlight(0, h/4, lx, ly, "1", Color.WHITE, Color.BLACK);
        
        //R2
        g2.setColor(Color.BLACK);
        String rect2 = "2";
        if(result != null && result.equalsIgnoreCase(rect2)) g2.setColor(Color.WHITE);
        checkArea(lx, h/4, lx, ly, rect2);
        g2.fillRect(lx, h/4, lx, ly);
        
        creaScrittaCentrataConHighlight(lx, h/4, lx, ly, "2", Color.WHITE,Color.BLACK);
        
        //R3 
        g2.setColor(Color.ORANGE);
        String rect3 = "3";
        if(result != null && result.equalsIgnoreCase(rect3)) g2.setColor(Color.WHITE);
        checkArea(0, h/4 + ly, lx, ly, rect3);
        g2.fillRect(0, h/4 + ly, lx, ly);
        
        creaScrittaCentrataConHighlight(0, h/4 + ly, lx, ly, "3", Color.WHITE, Color.BLACK);
        
        //R4
        g2.setColor(Color.RED);
        String rect4 = "4";
        if(result != null && result.equalsIgnoreCase(rect4)) g2.setColor(Color.WHITE);
        checkArea(lx, h/4 + ly, lx, ly, rect4);
        g2.fillRect(lx, h/4 + ly, lx, ly);
        
        creaScrittaCentrataConHighlight(lx, h/4 + ly, lx, ly, "4", Color.WHITE, Color.BLACK);

        // 1/3 della pagina
        int w2 = w - w1;
        g2.setColor(Color.YELLOW);
        g2.fillRect(w1, 0, w2, h);

        // Coordinate bot
        int l = Math.min(w2, h) / 2;
        int squareX = w1 + (w2 - l) / 2;
        int squareY = (h - l) / 2;
        botCoordinate.addAll(Arrays.asList(squareX, squareY, l, l));
        
        //Quadrato bot 
        g2.setColor(Color.RED);
        if(resultBot != null && resultBot.equalsIgnoreCase("B")) g2.setColor(Color.WHITE);
        checkArea(squareX, squareY, l, l, "B");
        g2.fillRect(squareX, squareY, l, l);
        
        // Testo "bot?"
        fontSize = Math.min(w2, h) / 12;
        g2.setFont(new Font("Arial", Font.BOLD, fontSize));
        
        g2.setColor(Color.BLACK);
        String textAboveSquare = "BOT?";
        int textAboveSquareWidth = g2.getFontMetrics().stringWidth(textAboveSquare);
        int textAboveSquareX = w1 + (w2 - textAboveSquareWidth) / 2;
        int textAboveSquareY = squareY - g2.getFontMetrics().getHeight();
        g2.drawString(textAboveSquare, textAboveSquareX, textAboveSquareY);
        
        //coordinate gioca
        giocaCoordinate.addAll(Arrays.asList(w1, (5*h)/6, w2, h/6));
        
        g2.setColor(Color.PINK);
        if(resultGioca != null && resultGioca.equalsIgnoreCase("B")) g2.setColor(Color.WHITE);
        checkArea(w1, (5*h)/6, w2, h/6, "gioca");
        g2.fillRect(w1, (5*h)/6, w2, h/6);
        
        creaScrittaCentrataConHighlight(w1, (5*h)/6, w2, h/6, "GIOCA", Color.WHITE, Color.YELLOW);
    }

	@Override
	public void mouseClicked(MouseEvent e) {
		
		if(result == null) {
			highlightSelectedArea();
		} else if (result != null && mousePosition.getX() >= 0 && mousePosition.getX() <= w*2/3 && mousePosition.getY() >= h/4 && mousePosition.getY() <= h) {
			highlightSelectedArea();
		}
		
		//Così facendo non rendiamo globali tutte le variabili
		int qx = botCoordinate.get(0);
		int qy = botCoordinate.get(1);
		int qlx = botCoordinate.get(2);
		int qly = botCoordinate.get(3);
		
		//freccetta per andare indietro
		int ax = arrowCoordinate.get(0);
		int ay = arrowCoordinate.get(1);
		int alx = arrowCoordinate.get(2);
		int aly = arrowCoordinate.get(3);
		
		//gioca
		int gx = giocaCoordinate.get(0);
		int gy = giocaCoordinate.get(1);
		int glx = giocaCoordinate.get(2);
		int gly = giocaCoordinate.get(3);
		
		//L'area con bot può variare il colore se schiaccio sopra l'area del bot 
		verifyBot(qx, qy, qlx, qly);
		
		arrowClicked = checkArea(ax, ay, alx, aly, "arrow");
		resultGioca = checkArea(gx, gy, glx, gly, "gioca");
		
		fireEvents();
	
	}

	private void verifyBot(int qx, int qy, int qlx, int qly) {
		if(resultBot != null && resultBot.equalsIgnoreCase("B") && mousePosition.getX() >= qx && 
				mousePosition.getX() <= qx + qlx && mousePosition.getY() >= qy && mousePosition.getY() <= qy + qly) {
			resultBot = null;
			g2.setColor(Color.RED);
		}else if (resultBot == null){
			resultBot = checkArea(qx, qy, qlx, qly, "B");
		}else if (resultBot.equalsIgnoreCase("A") && mousePosition.getX() >= qx && 
				mousePosition.getX() <= qx + qlx && mousePosition.getY() >= qy && mousePosition.getY() <= qy + qly) {
			resultBot = checkArea(qx, qy, qlx, qly, "B");
		}
	}

	private void fireEvents() {
		for(ActionListener listener : eventListeners) {
			if(!result.equalsIgnoreCase("A")) {
				ActionEvent stringEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, result);
				listener.actionPerformed(stringEvent);
			}
			
			if(resultBot != null && resultBot.equalsIgnoreCase("B")) {
				ActionEvent stringEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, resultBot);
		        listener.actionPerformed(stringEvent);
			}
			
			if(arrowClicked.equalsIgnoreCase("arrow")) {
				ActionEvent stringEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, arrowClicked);
				listener.actionPerformed(stringEvent);
				arrowClicked = null;
			}
			
			if(resultGioca != null && resultGioca.equalsIgnoreCase("gioca")) {
				ActionEvent stringEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, resultGioca);
				listener.actionPerformed(stringEvent);
				resultGioca = null;
			}
	    }
	}

	private void highlightSelectedArea() {
		for (Entry<String, ArrayList<Integer>> entry : mappaAree.entrySet()) {
            String key = entry.getKey();
            ArrayList<Integer> value = entry.getValue();
            int x = value.get(0);
            int y = value.get(1);
            int lunghezza = value.get(2);
            int altezza = value.get(3);
            result = checkArea(x, y, lunghezza, altezza, key);
            if (!result.equalsIgnoreCase("A")) break;
            repaint();
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
