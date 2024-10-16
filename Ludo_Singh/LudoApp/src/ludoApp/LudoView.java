package ludoApp;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

public class LudoView extends JPanel implements MouseMotionListener, MouseListener{
	
	int xOffset;
	int yOffset;
	int squareSize;
	int padding;
	
	private ArrayList<BufferedImage> imageList = new ArrayList<>();
	private String folderPath = "src\\fotoDadi\\copy";
	private File folder = new File(folderPath);
	private File[] files = folder.listFiles();
	private Timer t;
	private Point mousePosition;
    private int loopCounter = 0;
    private final int maxLoops = 1;
    private int currentImageIndex = 0;
    private BufferedImage image;
    private HashMap <Integer, ArrayList<Integer>> coordCirc;
    private boolean animationCompleted = false;
    private boolean isAnimating = false;
    private ArrayList<ActionListener> dadoEventListeners = new ArrayList<>();
    private ArrayList<ActionListener> esciEventListeners = new ArrayList<>();
    private ArrayList<ActionListener> pedineEventListeners = new ArrayList<>();
    private HashMap<String, ArrayList<Integer>> posPedine;
    private Map<Integer, int[]> coordinateMapRosse = new HashMap<>();
    private Map<Integer, int[]> coordinateMapBlu = new HashMap<>();
    private Map<Integer, int[]> coordinateMapVerdi = new HashMap<>();
    private Map<Integer, int[]> coordinateMapGialle = new HashMap<>();
	private boolean repaint = false;
	private boolean gameFinished = false;
	private ArrayList<Colore> leaderBoard = new ArrayList<>(); 

	LudoView() {
		 this.addMouseListener(this);
		 this.addMouseMotionListener(this);
		 popolaHashMapPosizioni();
	}
	
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawLudoBoard(g2);
    }
    
    private void drawLudoBoard(Graphics2D g2) {
        // Impostazioni di stile
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Dimensioni della tavola del gioco Ludo
        int boardSize = Math.min(getWidth(), getHeight());
        squareSize = boardSize / 15; // Dimensione di ciascuna cella
        padding = 1; // Sottile bordo nero tra le celle

        // Calcola le coordinate x e y per centrare la tavola
        xOffset = (getWidth() - 15 * (squareSize + padding) + padding) / 2;
        yOffset = (getHeight() - 15 * (squareSize + padding) + padding) / 2;

        // Disegna le caselle del percorso principale
        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                int x = xOffset + col * (squareSize + padding);
                int y = yOffset + row * (squareSize + padding);
                g2.setColor(getSquareColor(row, col));
                g2.fillRect(x, y, squareSize, squareSize);
                g2.setColor(Color.BLACK); // Colore del bordo
                g2.drawRect(x, y, squareSize, squareSize);
            }
        }
        
        creaTriangoli(g2);
        
        creaBasi(g2);
      
        //Contorno LUDO
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(xOffset , yOffset, 15 * (squareSize + padding) - 2*padding, 15 * (squareSize + padding) - 2*padding);
      
        creaCerchi(g2);
      
        casellaDado(g2);
        
        drawStar(g2, 13, 7, squareSize);
        drawStar(g2, 9, 13, squareSize);
        drawStar(g2, 3, 9, squareSize);
        drawStar(g2, 7, 3, squareSize); 
        
        disegnaPedineAll(g2);
        
        /*if (leaderBoard.size() <= 2) {
        	leaderBoard.add(Colore.ROSSO);
        	leaderBoard.add(Colore.BLU);
        	leaderBoard.add(Colore.VERDE);
        	leaderBoard.add(Colore.GIALLO);
        }*/
        drawLeaderBoard(g2);
    }

	private void drawLeaderBoard(Graphics2D g2) {
		if(gameFinished == true) {
		    	
		    	int leaderboardWidth = 9 * (squareSize + padding);
			    int leaderboardHeight = 9 * (squareSize + padding);
			    int leaderboardX = xOffset + 3 * (squareSize + padding);
			    int leaderboardY = yOffset + 3 * (squareSize + padding);
			    int leaderBoardSquareSize = Math.min(leaderboardX, leaderboardY)/9;
			    // Sfondo grigio chiaro
		    	g2.setColor(new Color(204, 249, 229));
			    g2.fillRect(leaderboardX, leaderboardY, leaderboardWidth, leaderboardHeight);      	
		    	int textHeight = g2.getFontMetrics().getHeight();
		        int spacing = textHeight + leaderBoardSquareSize;
		        int textY = leaderboardY + spacing ;
		        for (Colore colore : leaderBoard) {
		            g2.setColor(Color.BLACK);
		            g2.setFont(new Font("Serif", Font.PLAIN, 2*leaderBoardSquareSize));
		            int textWidth = g2.getFontMetrics().stringWidth(colore.toString());
		            // Centra il testo orizzontalmente nel rettangolo della classifica
		            int textX = leaderboardX + (leaderboardWidth - textWidth) / 2;

		            g2.drawString(colore.toString(), textX, textY);
		            textY += 4*leaderBoardSquareSize;
		        }

		        // Bottone di uscita
		        int buttonWidth = 14 * (leaderBoardSquareSize);
		        int buttonHeight = 3 * (leaderBoardSquareSize);
		        int buttonX = leaderboardX + (leaderboardWidth - buttonWidth) / 2;
		        int buttonY = leaderboardY + leaderboardHeight - buttonHeight - leaderBoardSquareSize;

		        // Disegna il bottone
		        g2.setColor(Color.RED);
		        g2.fillRect(buttonX, buttonY, buttonWidth, buttonHeight);

		        // Imposta il font e ottieni le metriche
		        g2.setFont(new Font("Serif", Font.PLAIN, leaderBoardSquareSize));
		        FontMetrics fm = g2.getFontMetrics();

		        // Testo del bottone
		        String buttonText = "Esci";

		        // Calcola la larghezza e l'altezza del testo
		        int textWidth = fm.stringWidth(buttonText);
		        int textHeight1 = fm.getHeight();
		        int ascent = fm.getAscent();

		        // Centra il testo orizzontalmente
		        int buttonTextX = buttonX + (buttonWidth - textWidth) / 2;

		        // Centra il testo verticalmente
		        int buttonTextY = buttonY + 2* (buttonHeight + textHeight1) / 3 - ascent;

		        // Disegna il testo nel bottone
		        g2.setColor(Color.WHITE);
		        g2.drawString(buttonText, buttonTextX, buttonTextY);

		    }
	}

    private Color getColoreFromEnum(Colore colore) {
        switch (colore) {
            case ROSSO:
                return Color.RED;
            case BLU:
                return Color.BLUE;
            case VERDE:
                return Color.GREEN;
            case GIALLO:
                return Color.YELLOW;
            default:
                return Color.BLACK;
        }
    }
    
    private void disegnaPedineAll(Graphics2D g2) {
        Map<String, Color> pedineColori = new HashMap<>();
        pedineColori.put("Verde", Color.GREEN);
        pedineColori.put("Rosso", Color.RED);
        pedineColori.put("Blu", Color.BLUE);
        pedineColori.put("Giallo", Color.YELLOW);

        int pedinaSize = squareSize * 3 / 4; // Dimensione della pedina
        int separazione = 3; // Offset tra pedine sulla stessa casella

        // Mappa per tracciare quante pedine ci sono su ciascuna posizione
        Map<Integer, Integer> pedineSullaStessaPosizione = new HashMap<>();

        // Itera attraverso tutte le pedine e disegnale
        for (Map.Entry<String, ArrayList<Integer>> entry : posPedine.entrySet()) {
            String colore = entry.getKey();
            ArrayList<Integer> posizioni = entry.getValue();

            // Ottieni il colore della pedina
            Color colorePedina = pedineColori.get(colore);

            // Disegna le quattro pedine
            for (int i = 0; i < posizioni.size(); i++) {
                int posizione = posizioni.get(i);
                int x = 0;
                int y = 0;

                if (posizione == 0) {
                    // Gestisci le pedine nella posizione 0
                    int xBase = 0, yBase = 0;

                    if (colorePedina.equals(Color.RED)) {
                        xBase = 11;
                        yBase = 2;
                    } else if (colorePedina.equals(Color.BLUE)) {
                        xBase = 2;
                        yBase = 11;
                    } else if (colorePedina.equals(Color.GREEN)) {
                        xBase = 2;
                        yBase = 2;
                    } else {
                        xBase = 11;
                        yBase = 11;
                    }

                    int xOffsetFactor = (i % 2 == 0) ? 0 : 2;
                    int yOffsetFactor = (i < 2) ? 0 : 2;

                    drawPedina(g2, xOffset + (xBase + xOffsetFactor) * (squareSize + padding), 
                               yOffset + (yBase + yOffsetFactor) * (squareSize + padding) - pedinaSize / 2, 
                               pedinaSize, colorePedina);
                } else {
                    // Calcola le coordinate della casella sul tabellone
                    Point coord = calcolaCoordinateCasella(posizione, colore);
                    x = coord.x;
                    y = coord.y;

                    // Verifica quante pedine ci sono già in questa posizione
                    int numeroPedineSovrapposte = pedineSullaStessaPosizione.getOrDefault(posizione, 0);
                    
                    // Gestione speciale per la posizione 57 (spostamento solo sull'asse Y)
                    if (posizione == 57 && (colorePedina.equals(Color.GREEN)||colorePedina.equals(Color.YELLOW))) {
                        int offsetY = numeroPedineSovrapposte * separazione;
                        // Disegna la pedina con offset solo sull'asse Y
                        drawPedina(g2, x, y + offsetY, pedinaSize, colorePedina);
                    } else {
                        // Spostamento su X per tutte le altre posizioni
                        int offsetX = numeroPedineSovrapposte * separazione;
                        // Disegna la pedina con offset solo sull'asse X
                        drawPedina(g2, x + offsetX, y, pedinaSize, colorePedina);
                    }

                    // Incrementa il conteggio delle pedine per questa posizione
                    pedineSullaStessaPosizione.put(posizione, numeroPedineSovrapposte + 1);
                }
            }
        }
    }
  
    public void stepNext() {
    	repaint();
    }
    
    public Point calcolaCoordinateCasella(int posizione, String colore) {
    	Point coordinata = new Point();
    	final int DIM_CASELLA = squareSize + padding; // Dimensione della casella
        final int HALF_SQUARE_SIZE = squareSize / 2;
        
    	if (colore.equalsIgnoreCase("Rosso")) {
	        int[] coords = coordinateMapRosse.get(posizione);
	        coordinata.x = coords[0];
	        coordinata.y = coords[1];
    	}
    	
    	if (colore.equalsIgnoreCase("Blu")) {
	        int[] coords = coordinateMapBlu.get(posizione);
	        coordinata.x = coords[0];
	        coordinata.y = coords[1];
    	}
    	
    	if (colore.equalsIgnoreCase("Verde")) {
	        int[] coords = coordinateMapVerdi.get(posizione);
	        coordinata.x = coords[0];
	        coordinata.y = coords[1];
    	}
    	
    	if (colore.equalsIgnoreCase("Giallo")) {
	        int[] coords = coordinateMapGialle.get(posizione);
	        coordinata.x = coords[0];
	        coordinata.y = coords[1];
    	}
    
    	coordinata.x = xOffset + coordinata.x * DIM_CASELLA;
    	coordinata.x = coordinata.x + HALF_SQUARE_SIZE;
    	coordinata.y = yOffset + coordinata.y * DIM_CASELLA;
    	
    	return coordinata;
    }

    private void popolaHashMapPosizioni() {
    	popolaHashMapPosizioniRosse();
    	popolaHashMapPosizioniBlu();
		popolaHashMapPosizioniVerdi();
		popolaHashMapPosizioniGialle();
	}
    
	private void popolaHashMapPosizioniRosse() {
		for (int posizione = 1; posizione <= 57; posizione++) {
		    int x = 0;
		    int y = 0;

		    if (posizione >= 1 && posizione <= 5) {
		        x = 8;
		        y = posizione;
		    } else if (posizione >= 6 && posizione <= 10) {
		        x = 8 + (posizione - 5);
		        y = 6;
		    } else if (posizione >= 11 && posizione <= 13) {
		        x = 14;
		        y = 5 + (posizione - 10);
		    } else if (posizione >= 14 && posizione <= 18) {
		        x = 14 - (posizione - 13);
		        y = 8;
		    } else if (posizione >= 19 && posizione <= 23) {
		        x = 8;
		        y = 8 + (posizione - 18);
		    } else if (posizione >= 24 && posizione <= 26) {
		        x = 9 - (posizione - 23);
		        y = 14;
		    } else if (posizione >= 27 && posizione <= 31) {
		        x = 6;
		        y = 14 - (posizione - 26);
		    } else if (posizione >= 32 && posizione <= 36) {
		        x = 6 - (posizione - 31);
		        y = 8;
		    } else if (posizione >= 37 && posizione <= 39) {
		        x = 0;
		        y = 9 - (posizione - 36);
		    } else if (posizione >= 40 && posizione <= 44) {
		        x = 0 + (posizione - 39);
		        y = 6;
		    } else if (posizione >= 45 && posizione <= 49) {
		        x = 6;
		        y = 6 - (posizione - 44);
		    } else if (posizione >= 50 && posizione <= 51) {
		        x = 5 + (posizione - 49);
		        y = 0;
		    } else if (posizione >= 52 && posizione <= 57) {
		        x = 7;
		        y = (posizione - 51);
		    }
		    
		    coordinateMapRosse.put(posizione, new int[]{x, y});
		}
	}

	private void popolaHashMapPosizioniBlu() {
		for (int posizione = 1; posizione <= 57; posizione++) {
		    int x = 0;
		    int y = 0;

		    if (posizione >= 1 && posizione <= 5) {
		        x = 6;
		        y = 14 - posizione;
		    } else if (posizione >= 6 && posizione <= 10) {
		        x = 6 - (posizione - 5);
		        y = 8;
		    } else if (posizione >= 11 && posizione <= 13) {
		        x = 0;
		        y = 9 - (posizione - 10);
		    } else if (posizione >= 14 && posizione <= 18) {
		        x = (posizione - 13);
		        y = 6;
		    } else if (posizione >= 19 && posizione <= 23) {
		        x = 6;
		        y = 6 - (posizione - 18);
		    } else if (posizione >= 24 && posizione <= 26) {
		        x = 5 + (posizione - 23);
		        y = 0;
		    } else if (posizione >= 27 && posizione <= 31) {
		        x = 8;
		        y = 0 + (posizione - 26);
		    } else if (posizione >= 32 && posizione <= 36) {
		        x = 8 + (posizione - 31);
		        y = 6;
		    } else if (posizione >= 37 && posizione <= 39) {
		        x = 14;
		        y = 5 + (posizione - 36);
		    } else if (posizione >= 40 && posizione <= 44) {
		        x = 14 - (posizione - 39);
		        y = 8;
		    } else if (posizione >= 45 && posizione <= 49) {
		        x = 8;
		        y = 8 + (posizione - 44);
		    } else if (posizione >= 50 && posizione <= 51) {
		        x = 9 - (posizione - 49);
		        y = 14;
		    } else if (posizione >= 52 && posizione <= 57) {
		        x = 7;
		        y = 14 - (posizione - 51);
		    }
		    
		    coordinateMapBlu.put(posizione, new int[]{x, y});
		}
	}

	private void popolaHashMapPosizioniVerdi() {
		for (int posizione = 1; posizione <= 57; posizione++) {
		    int x = 0;
		    int y = 0;

		    if (posizione >= 1 && posizione <= 5) {
		        x = posizione;
		        y = 6;
		    } else if (posizione >= 6 && posizione <= 10) {
		        x = 6;
		        y = 6 - (posizione - 5);
		    } else if (posizione >= 11 && posizione <= 13) {
		        x = 5 + (posizione - 10);
		        y = 0;
		    } else if (posizione >= 14 && posizione <= 18) {
		        x = 8;
		        y = 0 + (posizione - 13);
		    } else if (posizione >= 19 && posizione <= 23) {
		        x = 8 + (posizione - 18);
		        y = 6;
		    } else if (posizione >= 24 && posizione <= 26) {
		        x = 14;
		        y = 5 + (posizione - 23);
		    } else if (posizione >= 27 && posizione <= 31) {
		        x = 14 - (posizione - 26);
		        y = 8;
		    } else if (posizione >= 32 && posizione <= 36) {
		        x = 8;
		        y = 8 + (posizione - 31);
		    } else if (posizione >= 37 && posizione <= 39) {
		        x = 9 - (posizione - 36);
		        y = 14;
		    } else if (posizione >= 40 && posizione <= 44) {
		        x = 6;
		        y = 14 - (posizione - 39);
		    } else if (posizione >= 45 && posizione <= 49) {
		        x = 6 - (posizione - 44);
		        y = 8;
		    } else if (posizione >= 50 && posizione <= 51) {
		        x = 0;
		        y = 9 - (posizione - 49);
		    } else if (posizione >= 52 && posizione <= 57) {
		        x = (posizione - 51);
		        y = 7;
		    }
		    
		    coordinateMapVerdi.put(posizione, new int[]{x, y});
		}
	}
	
	private void popolaHashMapPosizioniGialle() {
		for (int posizione = 1; posizione <= 57; posizione++) {
		    int x = 0;
		    int y = 0;

		    if (posizione >= 1 && posizione <= 5) {
		        x = 14 - (posizione);
		        y = 8;
		    } else if (posizione >= 6 && posizione <= 10) {
		        x = 8;
		        y = 8 + (posizione - 5);
		    } else if (posizione >= 11 && posizione <= 13) {
		        x = 9 - (posizione - 10);
		        y = 14;
		    } else if (posizione >= 14 && posizione <= 18) {
		        x = 6;
		        y = 14 - (posizione - 13);
		    } else if (posizione >= 19 && posizione <= 23) {
		        x = 6 - (posizione - 18);
		        y = 8;
		    } else if (posizione >= 24 && posizione <= 26) {
		        x = 0;
		        y = 9 - (posizione - 23);
		    } else if (posizione >= 27 && posizione <= 31) {
		        x = posizione - 26;
		        y = 6;
		    } else if (posizione >= 32 && posizione <= 36) {
		        x = 6;
		        y = 6 - (posizione - 31);
		    } else if (posizione >= 37 && posizione <= 39) {
		        x = 5 + (posizione - 36);
		        y = 0;
		    } else if (posizione >= 40 && posizione <= 44) {
		        x = 8;
		        y = posizione - 39;
		    } else if (posizione >= 45 && posizione <= 49) {
		        x = 8 + (posizione - 44);
		        y = 6;
		    } else if (posizione >= 50 && posizione <= 51) {
		        x = 14;
		        y = 5 + (posizione - 49);
		    } else if (posizione >= 52 && posizione <= 57) {
		        x = 14 - (posizione - 51);
		        y = 7;
		    }
		    
		    coordinateMapGialle.put(posizione, new int[]{x, y});
		}
	}
	
	private void creaCerchi(Graphics2D g2) {
		g2.setColor(Color.WHITE);
		  coordCirc =  new LinkedHashMap<>();
		  popolaHashMapCircBasi(squareSize, padding, xOffset, yOffset, coordCirc);
		  
		  for (Entry<Integer, ArrayList<Integer>> entry : coordCirc.entrySet()) {
			  ArrayList<Integer> values = entry.getValue();
			  for(int v: values) {
				  g2.setColor(Color.WHITE);
				  fillCircle(entry.getKey(), v, squareSize * 3/4, g2);
				  g2.setColor(Color.BLACK);
				  drawCircle(entry.getKey(), v, squareSize * 3/4, g2);
			  }
		  }
	}

	private void creaBasi(Graphics2D g2) {
		  //Quadrato verde
		  g2.setColor(Color.GREEN);
		  g2.fillRect(xOffset, yOffset, 6 * (squareSize + padding) - padding, 6 * (squareSize + padding) - padding);
		  g2.setColor(Color.BLACK);
		  g2.drawRect(xOffset, yOffset, 6 * (squareSize + padding) - padding , 6 * (squareSize + padding) - padding);
		  
		  //Quadrato rosso
		  g2.setColor(Color.RED);
		  g2.fillRect(xOffset + 9 * (squareSize + padding), yOffset, 6 * (squareSize + padding) - padding, 6 * (squareSize + padding) - padding);
		  g2.setColor(Color.BLACK);
		  g2.drawRect(xOffset + 9 * (squareSize + padding), yOffset, 6 * (squareSize + padding) - padding , 6 * (squareSize + padding) - padding);
		  
		  //Quadrato blu
		  g2.setColor(Color.BLUE);
		  g2.fillRect(xOffset, yOffset + 9 * (squareSize + padding), 6 * (squareSize + padding) - padding, 6 * (squareSize + padding) - padding);
		  g2.setColor(Color.BLACK);
		  g2.drawRect(xOffset, yOffset + 9 * (squareSize + padding), 6 * (squareSize + padding) - padding , 6 * (squareSize + padding) - padding);
		  
		  //Quadrato giallo
		  g2.setColor(Color.YELLOW);
		  g2.fillRect(xOffset + 9 * (squareSize + padding), yOffset + 9 * (squareSize + padding), 6 * (squareSize + padding) - padding, 6 * (squareSize + padding) - padding);
		  g2.setColor(Color.BLACK);
		  g2.drawRect(xOffset + 9 * (squareSize + padding), yOffset + 9 * (squareSize + padding), 6 * (squareSize + padding) - padding , 6 * (squareSize + padding) - padding);
	}

	private void creaTriangoli(Graphics2D g2) {
		//Triangolo verde
        g2.setColor(Color.GREEN);
        int [] xPosVerde = {xOffset + 6 * (squareSize + padding) - padding , xOffset + 6 * (squareSize + padding) - padding, getWidth() / 2 + padding};
        int [] yPosVerde = {yOffset + 6 * (squareSize + padding) - padding, yOffset + 9 * (squareSize + padding) - padding, getHeight() / 2 + padding};
        g2.fillPolygon(xPosVerde, yPosVerde, 3);
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.BLACK);
        g2.drawPolygon(xPosVerde, yPosVerde, 3);
        
        //Triangolo rosso
        g2.setColor(Color.RED);
        int [] xPosRosso = {xOffset + 6 * (squareSize + padding) - padding , xOffset + 9 * (squareSize + padding) - padding, getWidth() / 2 + padding};
        int [] yPosRosso = {yOffset + 6 * (squareSize + padding) - padding, yOffset + 6 * (squareSize + padding) - padding, getHeight() / 2 + padding};
        g2.fillPolygon(xPosRosso, yPosRosso, 3);
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.BLACK);
        g2.drawPolygon(xPosRosso, yPosRosso, 3);
        
        //Triangolo blu
        g2.setColor(Color.BLUE);
        int [] xPosBlue = {xOffset + 6 * (squareSize + padding) - padding , xOffset + 9 * (squareSize + padding) - padding, getWidth() / 2 + padding};
        int [] yPosBlue = {yOffset + 9 * (squareSize + padding) - padding, yOffset + 9 * (squareSize + padding) - padding, getHeight() / 2 + padding};
        g2.fillPolygon(xPosBlue, yPosBlue, 3);
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.BLACK);
        g2.drawPolygon(xPosBlue, yPosBlue, 3);
        
        //Triangolo giallo
        g2.setColor(Color.YELLOW);
        int [] xPosYellow = {xOffset + 9 * (squareSize + padding) - padding , xOffset + 9 * (squareSize + padding) - padding, getWidth() / 2 + padding};
        int [] yPosYellow = {yOffset + 6 * (squareSize + padding) - padding, yOffset + 9 * (squareSize + padding) - padding, getHeight() / 2 + padding};
        g2.fillPolygon(xPosYellow, yPosYellow, 3);
        g2.setStroke(new BasicStroke(2));
        g2.setColor(Color.BLACK);
        g2.drawPolygon(xPosYellow, yPosYellow, 3);
	}
 
	public void drawPedina(Graphics2D g2, int x, int y, int pedinaSize, Color colore) {
	        int halfSize = pedinaSize / 2;
	        g2.setStroke(new BasicStroke(2));

	        // Triangolo con la base appoggiata al diametro della semicirconferenza
	        int[] xPos = {x - halfSize, x + halfSize, x};
	        int[] yPos = {y, y, y + pedinaSize};
	        g2.setColor(colore);
	        g2.fillPolygon(xPos, yPos, 3);
	        g2.setColor(Color.BLACK);
	        g2.drawLine(x - halfSize,y, x , y + pedinaSize);
	        g2.drawLine(x + halfSize, y, x , y + pedinaSize);

	        // Mezza circonferenza
	        g2.setColor(colore);
	        g2.fillArc(x - halfSize, y - halfSize, pedinaSize, pedinaSize, 0, 180);
	        g2.setColor(Color.BLACK);
	        g2.drawArc(x - halfSize, y - halfSize, pedinaSize, pedinaSize, 0, 180);
	    }

	private void popolaHashMapCircBasi(int squareSize, int padding, int xOffset, int yOffset,
			HashMap<Integer, ArrayList<Integer>> coordCirc) {
		  coordCirc.put(xOffset + 2 * (squareSize + padding), new ArrayList<>(Arrays.asList(yOffset + 2 * (squareSize + padding), yOffset + 4 * (squareSize + padding), 
				  yOffset + 11 * (squareSize + padding), yOffset + 13 * (squareSize + padding))));
		  coordCirc.put(xOffset + 4 * (squareSize + padding), new ArrayList<>(Arrays.asList(yOffset + 2 * (squareSize + padding), yOffset + 4 * (squareSize + padding), 
				  yOffset + 11 * (squareSize + padding), yOffset + 13 * (squareSize + padding))));
		  coordCirc.put(xOffset + 11 * (squareSize + padding), new ArrayList<>(Arrays.asList(yOffset + 2 * (squareSize + padding), yOffset + 4 * (squareSize + padding), 
				  yOffset + 11 * (squareSize + padding), yOffset + 13 * (squareSize + padding))));
		  coordCirc.put(xOffset + 13 * (squareSize + padding), new ArrayList<>(Arrays.asList(yOffset + 2 * (squareSize + padding), yOffset + 4 * (squareSize + padding), 
				  yOffset + 11 * (squareSize + padding), yOffset + 13 * (squareSize + padding))));
	}

    private Color getSquareColor(int row, int col) {
        // Logica per determinare il colore delle caselle
        if ((row <= 5 && col <= 5) || (row == 6 && col == 1) || (row == 7 && col >= 1 && col <= 5)) {
            return Color.GREEN;
        } else if((row <= 5 && col >= 9) || (row == 1 && col == 8) || (col == 7 && row >= 1 && row <= 5)) {
            return Color.RED; // Modifica a seconda delle tue esigenze
        }else if((row >= 9 && col <= 5) || (row == 13 && col == 6) || (col == 7 && row >= 9 && row <= 13)) {
            return Color.BLUE;
        }else if((row >= 9 && col >= 9) || (row == 8 && col == 13) || (row == 7 && col >= 9 && col <= 13)) {
                return Color.YELLOW;
        }else return Color.WHITE;
    }
    
    void fillCircle(int xc, int yc, int r, Graphics2D g2) {
    	g2.fillOval(xc - r, yc - r, r * 2, r * 2);
    } 
    
    void drawCircle(int xc, int yc, int r, Graphics2D g2) {
    	g2.drawOval(xc - r, yc - r, r * 2, r * 2);
    } 
    
    private void drawStar(Graphics2D g2, int col, int row, int size) {
        // Calcola il centro della casella
    	// Col e row sono diminuiti di 1 in modo che il metodo centri la stella nel quadratino in successivo in diagonale
        int x = xOffset + (col-1) * (squareSize + padding) + squareSize / 2;
        int y = yOffset + (row-1) * (squareSize + padding) + squareSize / 2 + 3;
        
        // Definisce il raggio maggiore della stella in funzione della dimensione della casella
        int r = size / 2; // raggio massimo uguale alla metà della dimensione della casella

        // Coordinate dei punti della stella
        int[] xPoints = new int[10];
        int[] yPoints = new int[10];

        for (int i = 0; i < 10; i++) {
            // Ogni punto della stella è calcolato a partire dal centro, alternando il raggio massimo e quello minimo
            int angle = i * 36; // angolo per ogni punto della stella
            int radius = (i % 2 == 0) ? r : r / 2; // alterna tra il raggio massimo e la metà del raggio massimo
            xPoints[i] = x + (int) (Math.cos(Math.toRadians(angle - 90)) * radius); // calcolo della x
            yPoints[i] = y + (int) (Math.sin(Math.toRadians(angle - 90)) * radius); // calcolo della y
        }

        g2.setColor(Color.BLACK); // colore del bordo
        g2.drawPolygon(xPoints, yPoints, 10); // disegna il contorno della stella
    }
    
    public void gameFinished(ArrayList<Colore> leaderBoard) {
    	gameFinished = true;
    	this.leaderBoard = leaderBoard;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Point point = e.getPoint();
        
        int row = (point.y - yOffset) / (squareSize + padding);
        int col = (point.x - xOffset) / (squareSize + padding);
        
        int r = squareSize * 3/4;
        
        if (row == 7 && col == 7) {
        	for(ActionListener listener : dadoEventListeners) {
        		ActionEvent stringEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "dadoCliccato");
				listener.actionPerformed(stringEvent);
        	}
        }
        
        mouseClickedRectangles(row, col);
        mouseClickedCircles(point, r);

        
        if(gameFinished) {
        	int leaderboardWidth = 9 * (squareSize + padding);
		    int leaderboardHeight = 9 * (squareSize + padding);
        	int leaderboardX = xOffset + 3 * (squareSize + padding);
		    int leaderboardY = yOffset + 3 * (squareSize + padding);
		    int leaderBoardSquareSize = Math.min(leaderboardX, leaderboardY)/9;
        	int buttonWidth = 14 * (leaderBoardSquareSize);
	        int buttonHeight = 3 * (leaderBoardSquareSize);
	        int buttonX = leaderboardX + (leaderboardWidth - buttonWidth) / 2;
	        int buttonY = leaderboardY + leaderboardHeight - buttonHeight - leaderBoardSquareSize;
	        
	        if (e.getX() >= buttonWidth && e.getX() <= buttonWidth + buttonX &&
			        e.getY() >= buttonHeight && e.getY() <= buttonHeight + buttonY) {
				
	        	for(ActionListener listener : esciEventListeners) {
	        		ActionEvent stringEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "fineGioco");
                    listener.actionPerformed(stringEvent);
	        	}
	        	
			    }
        }


   }
    
	private void mouseClickedCircles(Point point, int r) {
		int contatore = 0;
        for (Map.Entry<Integer, ArrayList<Integer>> entry : coordCirc.entrySet()) {
            int xCentro = entry.getKey(); // La chiave rappresenta la coordinata x del centro del cerchio
            ArrayList<Integer> yCoordList = entry.getValue();
            
            // Verifica ogni coordinata y associata alla chiave
            for (int i = 0; i < yCoordList.size(); i++) {
                int yCentro = yCoordList.get(i);
                
                // Calcola la distanza tra il punto e il centro del cerchio
                double distanza = Math.sqrt(Math.pow(point.getX() - xCentro, 2) + Math.pow(point.getY() - yCentro, 2));
                
                // Verifica se la distanza è minore o uguale al raggio del cerchio
                if (distanza <= r) {
                    for (ActionListener listener : pedineEventListeners) {
                        ActionEvent stringEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,  "c "+i+" "+contatore);
                        listener.actionPerformed(stringEvent);
                    }
                }
            }
            contatore++;
        }
	}

	private void mouseClickedRectangles(int row, int col) {
		//Rettangolino 3*6 a sx
        if((row >= 6 && row <=8) && ((col >= 0 && col <=5)) ) {
	        for(ActionListener listener : pedineEventListeners) {
	    		ActionEvent stringEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "q " + col + " " + row);
				listener.actionPerformed(stringEvent);
				}
        }
        
        //Rettangolino 3*6 a dx
        if((row >= 6 && row <=8) && ((col >= 9 && col <=14)) ) {
            for(ActionListener listener : pedineEventListeners) {
        		ActionEvent stringEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,  "q " + col + " " + row);
    			listener.actionPerformed(stringEvent);
    			}
        }
        
        //Rettangolino 6*3 in alto
        if((row >= 0 && row <=5) && ((col >= 6 && col <=8)) ) {
            for(ActionListener listener : pedineEventListeners) {
        		ActionEvent stringEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "q " + col + " " + row);
    			listener.actionPerformed(stringEvent);
    			}
        }
        
        //Rettangolino 6*3 in basso
        if((row >= 9 && row <=14) && ((col >= 6 && col <=8)) ) {
            for(ActionListener listener : pedineEventListeners) {
        		ActionEvent stringEvent = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "q " + col + " " + row);
    			listener.actionPerformed(stringEvent);
    			}
        }
	}

    private void casellaDado(Graphics2D g2) {
        int dadoX = xOffset + 7 * (squareSize + padding);
        int dadoY = yOffset + 7 * (squareSize + padding);
        int dadoSize = squareSize + padding;

        // Disegna il riquadro bianco per il dado
        g2.setColor(Color.WHITE);
        g2.fillRect(dadoX, dadoY, dadoSize, dadoSize);
        g2.setColor(Color.BLACK);

        if (image != null) {
            // Ridimensiona e disegna l'immagine del dado all'interno del riquadro
            g2.drawImage(image, dadoX, dadoY, dadoSize, dadoSize, this);
        } else {
            // Usa un'immagine di default se l'immagine è null
            ImageIcon imageIcon = new ImageIcon("src\\fotoDadi\\copy\\uno.jpg");
            if (imageIcon != null) {
                Image img = imageIcon.getImage();
                g2.drawImage(img, dadoX, dadoY, dadoSize, dadoSize, this);
            }
        }
        
        g2.drawRect(dadoX, dadoY, dadoSize, dadoSize);
        
    }
	
    public void dadoAnimation(int valoreDado) {
    	SoundCntrl.DADO.play();
		currentImageIndex = 0;
		
		 if (isAnimating) {
		        return; // Se l'animazione è già in corso, esci dalla funzione
		    }
		 
		isAnimating = true;
		
		t = new Timer(50, new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        if (loopCounter >= maxLoops) {
		            t.stop();
		            String nomeFoto = trovaNomeGiustoFotoDadoFinale(valoreDado);
		            image = caricaImmagine(nomeFoto);
		            isAnimating = false;
		            repaint();
		            loopCounter = 0;
		        } else {
		            if (currentImageIndex >= files.length) {
		                currentImageIndex = 0;
		                loopCounter++;
		            }
		            try {
		                image = ImageIO.read(files[currentImageIndex]);
		                repaint();
		            } catch (IOException ex) {
		                ex.printStackTrace();
		            }
		            currentImageIndex++;
		        }
		    }
		});
		t.start();
	}
	
	private String trovaNomeGiustoFotoDadoFinale(int valoreDado) {
		String nomeFoto = "default";
		switch(valoreDado) {
			case 1:
				nomeFoto = "uno";
				break;
			case 2:
				nomeFoto = "due";
				break;
			case 3:
				nomeFoto = "tre";
				break;
			case 4:
				nomeFoto = "quattro";
				break;
			case 5:
				nomeFoto = "cinque";
				break;
			case 6:
				nomeFoto = "sei";
				break;
			default:
				nomeFoto = "uno";
		}
		return nomeFoto;
	}
    
	private BufferedImage caricaImmagine(String nomeFoto) {
	        BufferedImage img = null;
	        File file = new File("src" + File.separator + "fotoDadi" + File.separator + "copy" + File.separator + nomeFoto + ".jpg");
	        if (!file.exists()) {
	            return null;
	        }
	        try {
	            img = ImageIO.read(file);
	        } catch (IOException e) {
	            System.err.println("Errore durante il caricamento dell'immagine: " + e.getMessage());
	            e.printStackTrace();
	        }
	        return img;
	    }

	public void addActionListenerEsci(ActionListener listener) {

		esciEventListeners.add(listener);
	}

    public void addActionListenerDado(ActionListener listener) {
		dadoEventListeners.add(listener);
	}
	
	public void addActionListenerPedine(ActionListener listener) {
		pedineEventListeners.add(listener);
	}
    
	public void posPedine(HashMap<String, ArrayList<Integer>> posizioniPedine) {
		posPedine = posizioniPedine;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		mousePosition = e.getPoint();
		this.repaint();
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
}
