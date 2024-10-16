package ludoApp;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class InitialDialogueWindow extends JPanel implements MouseMotionListener, MouseListener {
	
	private Image backgroundImage;
	ArrayList<ActionListener> eventListeners = new ArrayList<>();
	
	private int w;
	private int h;
	private int lx;
	private int ly;
    
	private int rect1X;
	private int rect1Y;

	private int rect2X;
	private int rect2Y;
	
	Graphics2D g2;
	
	public InitialDialogueWindow() throws NullPointerException {
		backgroundImage = new ImageIcon(getClass().getResource("sfondo.jpg")).getImage();
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			
			w = getWidth();
			h = getHeight();
			lx = w / 4;
		    ly = h / 4;

		    rect1X = w / 2 - lx / 2;
		    rect1Y = h / 2 - ly;

		    rect2X = w / 2 - lx / 2;
		    rect2Y = h / 2 + ly / 2;

		    g2.setColor(Color.CYAN);
		    checkArea1(g2);
		    g2.fill(new RoundRectangle2D.Double(rect1X, rect1Y, lx, ly, 20, 20));
		    
		    g2.setColor(Color.CYAN);
		    checkArea2(g2);
	        g2.fill(new RoundRectangle2D.Double(rect2X, rect2Y, lx, ly, 20, 20));
	        
	        g2.setColor(Color.BLACK);
	        int fontSize = Math.min(lx, ly) / 6; 
	        g2.setFont(new Font("Arial", Font.BOLD, fontSize));
	        
	        String text0 = "LUDO";
	        int text0X = rect1X + (lx - g2.getFontMetrics().stringWidth(text0)) / 2;
	        int text0Y = ly - ly/4 ;
	        
	        String text1 = "PARTITA LOCALE";
	        int text1X = rect1X + (lx - g2.getFontMetrics().stringWidth(text1)) / 2;
	        int text1Y = rect1Y + ly / 2;

	        String text2 = "GIOCA ONLINE";
	        int text2X = rect2X + (lx - g2.getFontMetrics().stringWidth(text2)) / 2;
	        int text2Y = rect2Y + ly / 2;

	        g2.drawString(text0, text0X, text0Y);
	        g2.drawString(text1, text1X, text1Y);
	        g2.drawString(text2, text2X, text2Y);
	}

	private String checkArea1(Graphics2D g2) {
		if(mousePosition != null &&mousePosition.getX() >= rect1X && mousePosition.getX() <= rect1X + lx && mousePosition.getY() >= rect1Y && mousePosition.getY() <= rect1Y + ly) {
			g2.setColor(Color.ORANGE);
			return "Area 1";
		}
		return "";
	}
	
	private String checkArea2(Graphics2D g2) {
		if(mousePosition != null && mousePosition.getX() >= rect2X && mousePosition.getX() <= rect2X + lx && mousePosition.getY() >= rect2Y && mousePosition.getY() <= rect2Y + ly) {
			g2.setColor(Color.ORANGE);
			return "Area 2";
		}
		return "";
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

	@Override
	public void mouseClicked(MouseEvent e) {
		String stringValue = checkArea1(g2);
		if(stringValue.equalsIgnoreCase("")) stringValue = checkArea2(g2);
		if(stringValue.equalsIgnoreCase("")) return;
		
		for(ActionListener listener : eventListeners) {
	        ActionEvent stringEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, stringValue);
	        listener.actionPerformed(stringEvent);
	    }
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
			
	public void addActionListener(ActionListener l) {
		eventListeners.add(l);
	}
}
