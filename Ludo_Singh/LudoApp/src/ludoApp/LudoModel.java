package ludoApp;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

public class LudoModel implements Serializable{
	
	private int numeroGiocatori;
	private boolean bot;
	private int turno = 0; 
	private int valoreDado = 0;
	private int giocatoreCorrente = 0;
	private int currentPedinaID; 
	private boolean specialCondition;
	private boolean diceRolled = false;

	private ArrayList<Pedina> pedineRosse = new ArrayList<>();
	private ArrayList<Pedina> pedineBlu = new ArrayList<>();	
	private ArrayList<Pedina> pedineVerdi = new ArrayList<>();
	private ArrayList<Pedina> pedineGialle = new ArrayList<>();
	
	transient private HashMap <Integer, Runnable> mappaPedine = new HashMap<>();
	private ArrayList<ActionListener> gameFinishedListeners = new ArrayList<>();
	private ArrayList<Colore> leaderBoard = new ArrayList<>();
	
	LudoModel(int numeroGiocatori, boolean bot) {
		if(bot == true ) {
			this.numeroGiocatori = 4;
		}else {
			this.numeroGiocatori = numeroGiocatori;
		}	
		this.bot = bot;
		
		if(bot == false) {
			popolaHashMap();
			Runnable action = mappaPedine.get(numeroGiocatori);
	        if (action != null) {
	            action.run();
	        }
		}else {
			int numeroBot = 4 - numeroGiocatori;
			popolaHashMapBot(numeroBot);
			Runnable action = mappaPedine.get(numeroGiocatori);
	        if (action != null) {
	            action.run();
	        }
		}
	}
	
	//StepNext per player e bot 
	public void stepNext() {

        ArrayList<Pedina> pedineGiocatore = getPedineGiocatore(giocatoreCorrente);

        for (Pedina pedina: pedineGiocatore) {
        	if (pedina.getId() == currentPedinaID) {
        		if(pedina.getPos() == 0 && valoreDado != 6) {
        			pedina.avanza(0);
        		}else if (valoreDado == 6 && pedina.getPos() == 0) {
        			pedina.avanza(1);
        		} else {
        			pedina.avanza(valoreDado);
        			uccidiPedine(pedina);
        		}
        	}
        }
        
        if (controllaVittoria(pedineGiocatore) == true) {
            if (!leaderBoard.contains(pedineGiocatore.get(0).getColore())) {
            	leaderBoard.add(pedineGiocatore.get(0).getColore());
            }
        }
        
        if(leaderBoard.size() == numeroGiocatori) {
        	for(ActionListener actionListener: gameFinishedListeners) 
        		actionListener.actionPerformed(null);
        	}
    }
	public void stepNextBot() {
		 
		ArrayList<Pedina> pedineGiocatore = getPedineGiocatore(giocatoreCorrente);
		
		makeNextMoveBot(pedineGiocatore);
		
		if (controllaVittoria(pedineGiocatore) == true) {
	            if (!leaderBoard.contains(pedineGiocatore.get(0).getColore())) {
	            	leaderBoard.add(pedineGiocatore.get(0).getColore());
	            }
	        }
	        
        if(leaderBoard.size() == numeroGiocatori) {
        	for(ActionListener actionListener: gameFinishedListeners) 
        		actionListener.actionPerformed(null);
        	}
	}
	
	public int lanciaDado() {
		Random rand = new Random();
		int result = rand.nextInt(6) + 1;
		valoreDado = result;
		
		return result;
	}
	
	public boolean isActionValid(String action) {
    	ArrayList<Pedina> pedineGiocatore = getPedineGiocatore(giocatoreCorrente);
    	Colore colorePlayer = pedineGiocatore.get(0).getColore();
    	int posizione = convertiSistemaDiRiferimentoModel(action, colorePlayer);
    	if(posizione < 0) return false;
    	if (posizione == 0) {
    		for (Pedina pedina : pedineGiocatore) {
        		if(pedina.getPos() == 0 && valoreDado == 6) {
        			if(pedina.getId() == currentPedinaID) {
        				return true;
        			}
        		} 
    	    }
    		return false;  	
    	}else {
    		for(Pedina pedina: pedineGiocatore) {
        		if(pedina.getPos() == posizione) {
    	        	currentPedinaID = pedina.getId();
    	        	return true;
    	        }
        	}
        	return false;
    	}	
    }
	
    public void rotateTurno() {
	   giocatoreCorrente = turno % numeroGiocatori;
	   ArrayList<Pedina> pedineGiocatore = getPedineGiocatore(giocatoreCorrente);
	   
	   if(pedineGiocatore.get(0).getPos() == 0 && pedineGiocatore.get(1).getPos() == 0 && pedineGiocatore.get(2).getPos() == 0 
				 && pedineGiocatore.get(3).getPos() == 0 && valoreDado != 6) {
			turno = turno + 1;
			specialCondition = true;
		}else if (pedineGiocatore.get(0).canAdvance(valoreDado) == false && pedineGiocatore.get(1).canAdvance(valoreDado) == false 
					 && pedineGiocatore.get(2).canAdvance(valoreDado) == false && pedineGiocatore.get(3).canAdvance(valoreDado) == false) {
			turno = turno + 1;
			specialCondition = true;
		}else if (valoreDado == 6){
			specialCondition = false;
		}else {
			turno = turno + 1;
			specialCondition = false;
		 }
    }
  
    //Metodi per i bot
  	private void makeNextMoveBot(ArrayList<Pedina> pedineGiocatore) {
  		//Se sono in base e non ho 6 allora non posso muovermi 
  		for(Pedina pedina: pedineGiocatore) {
  			if(pedina.getPos() != 0 || valoreDado == 6) break;
  			return;
  		}
  		
  		//Se sono in base e tiro 6 allora vado in posizione 1
  		for(Pedina pedina: pedineGiocatore) {
  			if(pedina.getPos() == 0 && valoreDado == 6) {
  				SoundCntrl.MOSSA.playWithDelay(2000);
  				pedina.avanza(1);
  				return;
  			}
  		}
  		
  		//Se posso uccidere qualche pedina allora lo faccio 
  		for (Pedina pedina: pedineGiocatore) {
  			int posPedina = pedina.getPos();
  			boolean safeZone = pedina.getSafeZone();
  			pedina.avanza(valoreDado);
  			if(canKill(pedina) == true && pedina.getPos() != 0) {
  				SoundCntrl.MOSSA.playWithDelay(2000);
  				uccidiPedine(pedina);
  				turno--;
  				return;
  			}
  			pedina.setPos(posPedina);
  			pedina.setSafeZone(safeZone);
  		}
  		
  		//Se posso andare in safeZone allora lo faccio
  		for(Pedina pedina: pedineGiocatore) {
  			if(pedina.checkSafeZone(valoreDado + pedina.getPos()) == true && pedina.getPos() != 0) {
  				SoundCntrl.MOSSA.playWithDelay(2000);
  				pedina.avanza(valoreDado);
  				return;
  			}
  		}
  		
  		//Muovo la pedina più avantin non in safeZone 
  		ArrayList<Pedina> pedineInOrdine = new ArrayList<>(pedineGiocatore);

		// Ordinamento in base alla posizione
		Collections.sort(pedineInOrdine, Comparator.comparingInt(Pedina::getPos));
		
		// Avanzamento della prima pedina non in zona sicura
		for (int i = pedineInOrdine.size() - 1; i >= 0; i--) {
		    Pedina pedina = pedineInOrdine.get(i);
		    if (!pedina.getSafeZone() && pedina.getPos() != 0) {
		    	SoundCntrl.MOSSA.playWithDelay(2000);
		        pedina.avanza(valoreDado);
		        if(pedina.getPos() == 57) {
		        	turno--;
		        }
		        return;
		    }
		}
  		
		//Se non si verificano nessuna delle altre casistiche allora avanza la pedina più in avanti 
		for(int i = pedineInOrdine.size() - 1; i >= 0; i--)
		if(pedineInOrdine.get(i).getPos() != 0 && pedineInOrdine.get(i).canAdvance(valoreDado)) {
			SoundCntrl.MOSSA.playWithDelay(2000);
			pedineInOrdine.get(i).avanza(valoreDado);
	  		return;
		}
  	}
  	private boolean canKill(Pedina pedina) {
     	 ArrayList<Pedina> pedineAltriGiocatori = new ArrayList<>();

          // Recupera tutte le pedine degli altri giocatori
          for (int i = 0; i < numeroGiocatori; i++) {
              if (i != giocatoreCorrente) {
                  pedineAltriGiocatori.addAll(getPedineGiocatore(i));
              }
          }
          
          for (Pedina altraPedina : pedineAltriGiocatori) {
              if (stessaPosizione(pedina, altraPedina) && pedina.getSafeZone() == false && altraPedina.getSafeZone() == false) {
            	  SoundCntrl.MANGIA.playWithDelay(2000);
                  return true; 
              }
          }
          
          return false;
     }
  	
  	//Metodi per l'uccisione delle pedine   
    private void uccidiPedine(Pedina pedina) {
        ArrayList<Pedina> pedineAltriGiocatori = new ArrayList<>();

        // Recupera tutte le pedine degli altri giocatori
        for (int i = 0; i < numeroGiocatori; i++) {
            if (i != giocatoreCorrente) {
                pedineAltriGiocatori.addAll(getPedineGiocatore(i));
            }
        }

        for (Pedina altraPedina : pedineAltriGiocatori) {
            if (stessaPosizione(pedina, altraPedina) && pedina.getSafeZone() == false && altraPedina.getSafeZone() == false) {
                altraPedina.morta(); 
                if(altraPedina.getPos() == 0 && pedina.getBot() == false) {
                	SoundCntrl.MANGIA.play();
                	turno--;
                }
            }
        }
    }

    // Verifica se due pedine sono sulla stessa posizione relativa sul tabellone
    private boolean stessaPosizione(Pedina pedina1, Pedina pedina2) {
        Colore colore1 = pedina1.getColore();
        Colore colore2 = pedina2.getColore();

        int pos1 = pedina1.getPos();
        int pos2 = pedina2.getPos();

        // Converte la posizione della seconda pedina al sistema di coordinate del colore della prima pedina
        int pos2Convertita = convertiSistemaDiRiferimento(pos2, colore1, colore2);
        
        return pos1 == pos2Convertita;
    }

    // Converte una posizione da un sistema di coordinate relativo da un colore a un altro
    private int convertiSistemaDiRiferimento(int pos, Colore coloreAltraPedina, Colore colorePedinaPrincipale) {
        int offset = getOffset(coloreAltraPedina, colorePedinaPrincipale);
        
        int posConvertita = (pos + offset) % 52;
        
        return posConvertita;
    }
    
    // Calcola l'offset tra due colori
    private int getOffset(Colore coloreAltraPedina, Colore colorePedinaPrincipale) {
        int offset = 0;

        switch (coloreAltraPedina) {
            case ROSSO:
                if (colorePedinaPrincipale == Colore.GIALLO) offset = 13;
                else if (colorePedinaPrincipale == Colore.BLU) offset = 26;
                else if (colorePedinaPrincipale == Colore.VERDE) offset = 39;
                break;
            case GIALLO:
                if (colorePedinaPrincipale == Colore.ROSSO) offset = -13;
                else if (colorePedinaPrincipale == Colore.BLU) offset = 13;
                else if (colorePedinaPrincipale == Colore.VERDE) offset = 26;
                break;
            case BLU:
                if (colorePedinaPrincipale == Colore.ROSSO) offset = -26;
                else if (colorePedinaPrincipale == Colore.GIALLO) offset = -13;
                else if (colorePedinaPrincipale == Colore.VERDE) offset = 13;
                break;
            case VERDE:
                if (colorePedinaPrincipale == Colore.ROSSO) offset = -39;
                else if (colorePedinaPrincipale == Colore.GIALLO) offset = -26;
                else if (colorePedinaPrincipale == Colore.BLU) offset = -13;
                break;
        }

        // Aggiusta l'offset per rimanere nel range 0-51
        offset = (offset + 52) % 52;

        return offset;
    }  
    
	private boolean controllaVittoria(ArrayList<Pedina> pedineGiocatore) {
	       // Controlla se tutte le pedine sono nella posizione finale
	       for (Pedina pedina : pedineGiocatore) {
	           if (!pedina.getFinished()) {
	               return false;
	           }
	       }
	       return true;
	}
	
	//Action Listerners
    public void addActionListenerGameFinished(ActionListener listener) {
    	gameFinishedListeners.add(listener);
    }
    
    //Metodi per popolare l'hashmap contenente come Key il numero di gioctori e come VALUE l'azione da compiere a seconda del numero di giocatori 
    void riempiArrayList(ArrayList<Pedina> pedine, Colore colore, boolean bot) {
		for(int id = 0; id < 4; id++) {
			Pedina pedina;
			if(bot == true) {
				pedina = new Pedina(colore, id, true);
			}
			else {
				pedina = new Pedina(colore, id, false);
			}
			pedine.add(pedina);
		}
	}
 
    private void popolaHashMap() {
		mappaPedine.put(1, () -> riempiArrayList(pedineRosse, Colore.ROSSO, false));
		mappaPedine.put(2, () -> {riempiArrayList(pedineRosse, Colore.ROSSO, false);
			riempiArrayList(pedineBlu, Colore.BLU, false);
			});
		mappaPedine.put(3, () -> {riempiArrayList(pedineRosse, Colore.ROSSO, false);
			riempiArrayList(pedineBlu, Colore.BLU, false);
			riempiArrayList(pedineVerdi, Colore.VERDE, false);
		});
		mappaPedine.put(4, () -> {riempiArrayList(pedineRosse, Colore.ROSSO, false);
			riempiArrayList(pedineBlu, Colore.BLU, false);
			riempiArrayList(pedineVerdi, Colore.VERDE, false);
			riempiArrayList(pedineGialle, Colore.GIALLO, false);
		});
	}
	private void popolaHashMapBot(int numeroBot) {
		switch(numeroBot){
		case 1:
			mappaPedine.put(3, () -> {
			riempiArrayList(pedineRosse, Colore.ROSSO, false);
			riempiArrayList(pedineBlu, Colore.BLU, false);
			riempiArrayList(pedineVerdi, Colore.VERDE, false);
			riempiArrayList(pedineGialle, Colore.GIALLO, true);
			});
			break;
		case 2:
			mappaPedine.put(2, () -> {
			riempiArrayList(pedineRosse, Colore.ROSSO, false);
			riempiArrayList(pedineBlu, Colore.BLU, false);
			riempiArrayList(pedineVerdi, Colore.VERDE, true);
			riempiArrayList(pedineGialle, Colore.GIALLO, true);
			});
			break;
		case 3:
			mappaPedine.put(1, () -> {
			riempiArrayList(pedineRosse, Colore.ROSSO, false);
			riempiArrayList(pedineBlu, Colore.BLU, true);
			riempiArrayList(pedineVerdi, Colore.VERDE, true);
			riempiArrayList(pedineGialle, Colore.GIALLO, true);
			});
			break;
		default:
			break;
		}
	}
	
	//Metodi per ottenere la posizione di una pedina di un certo colore a partire dalle coordinate x e y sulla matricie/tavola
	private void selectPedinaInBase(int x, int y, Colore colore) {
		if(colore == Colore.ROSSO) {
			if (x == 2 && y == 0) {
				currentPedinaID = 0;
			}else if(x == 3 && y == 0) {
				currentPedinaID = 1;
			}else if (x == 2 && y == 1) {
				currentPedinaID = 2;
			}else if (x == 3 && y == 1) {
				currentPedinaID = 3;
			}
		}
		
		if(colore == Colore.BLU) {
			if (x == 0 && y == 2) {
				currentPedinaID = 0;
			}else if(x == 1 && y == 2) {
				currentPedinaID = 1;
			}else if (x == 0 && y == 3) {
				currentPedinaID = 2;
			}else if (x == 1 && y == 3) {
				currentPedinaID = 3;
			}
		}
		
		if(colore == Colore.VERDE) {
			if (x == 0 && y == 0) {
				currentPedinaID = 0;
			}else if(x == 1 && y == 0) {
				currentPedinaID = 1;
			}else if (x == 0 && y == 1) {
				currentPedinaID = 2;
			}else if (x == 1 && y == 1) {
				currentPedinaID = 3;
			}
		}
		
		if(colore == Colore.GIALLO) {
			if (x == 2 && y == 2) {
				currentPedinaID = 0;
			}else if(x == 3 && y == 2) {
				currentPedinaID = 1;
			}else if (x == 2 && y == 3) {
				currentPedinaID = 2;
			}else if (x == 3 && y == 3) {
				currentPedinaID = 3;
			}
		}
	}
	private int convertiSistemaDiRiferimentoModel(String action, Colore colorePlayer) {
   	 if(action != null) {
   		 String[] parti = action.split(" ");
   		 String cOrQ = parti[0];
        int x = Integer.parseInt(parti[1]);
        int y = Integer.parseInt(parti[2]);
        
        if(cOrQ.equalsIgnoreCase("c")) {
       		 selectPedinaInBase(y, x, colorePlayer);
       		 return 0;
        }else {
       	 if(colorePlayer == Colore.ROSSO) {
       		 return getPosizioneRosse(x, y);
       	 } else if (colorePlayer == Colore.BLU) {
       		 return getPosizioneBlu(x, y);
       	 }else if (colorePlayer == Colore.VERDE) {
       		 return getPosizioneVerdi(x, y);
       	 }else if (colorePlayer == Colore.GIALLO){
       		 return getPosizioneGialle(x, y);
       	 }else {
       		 return -1;
       	 }
        }
   	 }
   	 return -1;
	}
	private int getPosizioneRosse(int x, int y) {
	        if (x == 8 && y >= 1 && y <= 5) {
	            return y;
	        } else if (x >= 9 && x <= 13 && y == 6) {
	            return x - 8 + 5;
	        } else if (x == 14 && y >= 6 && y <= 8) {
	            return y - 5 + 10;
	        } else if (x >= 9 && x <= 13 && y == 8) {
	            return 13 + (14 - x);
	        } else if (x == 8 && y >= 9 && y <= 13) {
	            return 18 + (y - 8);
	        } else if (x >= 6 && x <= 8 && y == 14) {
	            return 23 + (9 - x);
	        } else if (x == 6 && y >= 9 && y <= 13) {
	            return 26 + (14 - y);
	        } else if (x >= 1 && x <= 5 && y == 8) {
	            return 31 + (6 - x);
	        } else if (x == 0 && y >= 6 && y <= 8) {
	            return 36 + (9 - y);
	        } else if (x >= 1 && x <= 5 && y == 6) {
	            return 39 + x;
	        } else if (x == 6 && y >= 1 && y <= 5) {
	            return 44 + (6 - y);
	        } else if (x >= 6 && x <= 7 && y == 0) {
	            return 49 + (x - 5);
	        } else if (x == 7 && y >= 1 && y <= 6) {
	            return 51 + y;
	        }

	        return -1;
	    }
    private int getPosizioneBlu(int x, int y) {
        if (x == 6 && y >= 9 && y <= 13) {
            return 14 - y;
        } else if (x >= 1 && x <= 5 && y == 8) {
            return 6 + (5 - x);
        } else if (x == 0 && y >= 6 && y <= 8) {
            return 10 + (9 - y);
        } else if (x >= 1 && x <= 5 && y == 6) {
            return 13 + x;
        } else if (x == 6 && y >= 1 && y <= 5) {
            return 19 + (5 - y);
        } else if (x >= 6 && x <= 8 && y == 0) {
            return 23 + (x - 5);
        } else if (x == 8 && y >= 1 && y <= 5) {
            return 26 + y;
        } else if (x >= 9 && x <= 13 && y == 6) {
            return 31 + (x - 8);
        } else if (x == 14 && y >= 6 && y <= 8) {
            return 36 + (y - 5);
        } else if (x >= 9 && x <= 13 && y == 8) {
            return 39 + (14 - x);
        } else if (x == 8 && y >= 9 && y <= 13) {
            return 44 + (y - 8);
        } else if (x >= 7 && x <= 8 && y == 14) {
            return 49 + (9 - x);
        } else if (x == 7 && y >= 8 && y <= 13) {
            return 51 + (14 - y);
        }

        return -1;
    }
    private int getPosizioneVerdi(int x, int y) {
        if (x >= 1 && x <= 5 && y == 6) {
            return x;
        } else if (x == 6 && y >= 1 && y <= 5) {
            return 6 + (5 - y);
        } else if (x >= 6 && x <= 8 && y == 0) {
            return 10 + (x - 5);
        } else if (x == 8 && y >= 1 && y <= 5) {
            return 13 + y;
        } else if (x >= 9 && x <= 13 && y == 6) {
            return 18 + (x - 8);
        } else if (x == 14 && y >= 6 && y <= 8) {
            return 23 + (y - 5);
        } else if (x >= 9 && x <= 13 && y == 8) {
            return 26 + (14 - x);
        } else if (x == 8 && y >= 9 && y <= 13) {
            return 31 + (y - 8);
        } else if (x >= 6 && x <= 8 && y == 14) {
            return 36 + (9 - x);
        } else if (x == 6 && y >= 9 && y <= 13) {
            return 39 + (14 - y);
        } else if (x >= 1 && x <= 5 && y == 8) {
            return 44 + (6 - x);
        } else if (x == 0 && y >= 7 && y <= 8) {
            return 49 + (9 - y);
        } else if (x >= 1 && x <= 6 && y == 7) {
            return 51 + x;
        }

        return -1; 
    }
    private int getPosizioneGialle(int x, int y) {
        if (x >= 9 && x <= 13 && y == 8) {
            return 14 - x;
        } else if (x == 8 && y >= 9 && y <= 13) {
            return 5 + (y - 8);
        } else if (x >= 6 && x <= 8 && y == 14) {
            return 10 + (9 - x);
        } else if (x == 6 && y >= 9 && y <= 13) {
            return 13 + (14 - y);
        } else if (x >= 1 && x <= 5 && y == 8) {
            return 18 + (6 - x);
        } else if (x == 0 && y >= 6 && y <= 8) {
            return 23 + (9 - y);
        } else if (x >= 1 && x <= 5 && y == 6) {
            return 26 + x;
        } else if (x == 6 && y >= 1 && y <= 5) {
            return 31 + (6 - y);
        } else if (x >= 6 && x <= 8 && y == 0) {
            return 36 + (x - 5);
        } else if (x == 8 && y >= 1 && y <= 5) {
            return 39 + y;
        } else if (x >= 9 && x <= 13 && y == 6) {
            return 44 + (x - 8);
        } else if (x == 14 && y >= 6 && y <= 7) {
            return 49 + (y - 5);
        } else if (x >= 8 && x <= 13 && y == 7) {
            return 51 + (14 - x);
        }

        return -1; 
    }
    
    //Getter pubblici
    public boolean getIfNextBot() {
    	int nextPlayer = turno % numeroGiocatori;
    	ArrayList<Pedina> currentPlayer = getPedineGiocatore(nextPlayer);
    	return currentPlayer.get(0).getBot();
    }
	public boolean getIfInSpecialCondition() {
		return specialCondition;
	}     
    public ArrayList<Colore> getLeaderBoard() {
    	return leaderBoard;
    }
    public int getValoreDado() {
    	return valoreDado;
    }
    public HashMap<String, ArrayList<Integer>> getPosizioniPedine() {
	    HashMap<String, ArrayList<Integer>> posizioniPedine = new HashMap<>();

	    ArrayList<Integer> posizioniRosse = new ArrayList<>();
	    ArrayList<Integer> posizioniBlu = new ArrayList<>();
	    ArrayList<Integer> posizioniVerdi = new ArrayList<>();
	    ArrayList<Integer> posizioniGialle = new ArrayList<>();

	    for (Pedina pedina : pedineRosse) {
	        posizioniRosse.add(pedina.getPos());
	    }
	    posizioniPedine.put("Rosso", posizioniRosse);

	    if (pedineBlu.size() != 0) {
	    for (Pedina pedina : pedineBlu) {
	        posizioniBlu.add(pedina.getPos());
	    }
	    posizioniPedine.put("Blu", posizioniBlu);
	    }

	    if (pedineVerdi.size() != 0) {
	    for (Pedina pedina : pedineVerdi) {
	        posizioniVerdi.add(pedina.getPos());
	    }
	    posizioniPedine.put("Verde", posizioniVerdi);
	    }
	    
	    if (pedineGialle.size() != 0) {
	    for (Pedina pedina : pedineGialle) {
	        posizioniGialle.add(pedina.getPos());
	    }
	    posizioniPedine.put("Giallo", posizioniGialle);
	    }

	    return posizioniPedine;
	}

    public boolean getDiceRolled() {
    	return diceRolled;
    }
    
    public void setDiceRolled(boolean value) {
    	diceRolled = value;
    }

    public String getCurrentPlayer() {
    	switch (giocatoreCorrente) {
        case 0:
            return "rosso";
        case 1:
        	return "giallo";
        case 2:
        	return "blu";
        case 3:
        	return "verde";
        default:
            return "";
    }
    }
    
    public int getCurrentPlayerId() {
    	return giocatoreCorrente;
    }
    
    //Getter privati
    private ArrayList<Pedina> getPedineGiocatore(int giocatore) {
        switch (giocatore) {
            case 0:
                return pedineRosse;
            case 1:
            	return pedineGialle;
            case 2:
            	return pedineBlu;
            case 3:
            	return pedineVerdi;
            default:
                return new ArrayList<>();
        }
    }  

}
