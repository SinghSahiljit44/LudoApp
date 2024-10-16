package ludoApp;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JFrame;

public class LudoAppCntrl {
	
	class IntWrapper {
	    private int value;
	    IntWrapper(int value) {
	        this.value = value;
	    }
	    void setValue(int value) {
	    	this.value = value;
	    }
	    int getValue() {
	    	return value;
	    }
	}

	class BooleanWrapper {
	    private boolean value;
	    BooleanWrapper(boolean value) {
	        this.value = value;
	    }
	    void setValue(boolean value) {
	    	this.value = value;
	    }
	    boolean getValue() {
	    	return value;
	    }
	}

	private InitialDialogueWindow dialogue;
	private OfflinePlayersWindow offlinePlayersWindow;
	private OnlinePlayersWindow onlinePlayersWindow;
	private JFrame frame;
	
	public LudoAppCntrl(InitialDialogueWindow dialogue, OfflinePlayersWindow offlinePlayersWindow, OnlinePlayersWindow onlinePlayersWindow, JFrame frame) {
		this.dialogue = dialogue;
		this.offlinePlayersWindow = offlinePlayersWindow;
		this.onlinePlayersWindow = onlinePlayersWindow;
		this.frame = frame;
	}

	public void initializeGame() {
		InitialDialogueWindow dialogue = new InitialDialogueWindow();
		OfflinePlayersWindow offlinePlayersWindow = new OfflinePlayersWindow();
		
		frame.getContentPane().add(dialogue);
        dialogue.setLayout(null);
        
        //Oggetti che contengono info degli eventi 
        IntWrapper numeroDiGiocatoriOffline = new IntWrapper(0);
        BooleanWrapper bot = new BooleanWrapper(false);
        BooleanWrapper goBack = new BooleanWrapper(false);
        BooleanWrapper gioca = new BooleanWrapper(false);
        
  		//A seconda del evento succede qualcosa
  		HashMap<String, DoSomething> eventi = new HashMap<>();
  		popolaHashMapEventi(numeroDiGiocatoriOffline, bot, goBack, gioca, eventi);
        
      	//ACTION LISTENERS VARI
		dialogue.addActionListener(e -> {
			String result = e.getActionCommand();
			if(result.equalsIgnoreCase("Area 1")) {
				frame.getContentPane().remove(dialogue);
				frame.getContentPane().add(offlinePlayersWindow);
				//Revalidate serve al layout per ricalcolare la posizione e le dimensionei dei componenti interni al contenitore, quindi nel nostro caso indica il contentPane
				frame.revalidate();
		        frame.repaint();
			};
			if(result.equalsIgnoreCase("Area 2")) {
				frame.getContentPane().remove(dialogue);
				frame.getContentPane().add(onlinePlayersWindow);
				//Revalidate serve al layout per ricalcolare la posizione e le dimensionei dei componenti interni al contenitore, quindi nel nostro caso indica il contentPane
				frame.revalidate();
		        frame.repaint();
			}
		});
		
		offlinePlayersWindow.addActionListener(e -> {
			String resultOffline = e.getActionCommand();
			for (Entry<String, DoSomething> entry : eventi.entrySet()) {
				if(entry.getKey().equalsIgnoreCase(resultOffline)) {
					entry.getValue().doSomething(resultOffline);
					break;
				}
			}
			if(goBack.getValue() == true) {
				frame.getContentPane().remove(offlinePlayersWindow);
				frame.getContentPane().add(dialogue);
				frame.revalidate();
		        frame.repaint();
		        goBack.setValue(false);
			}
			
			if(gioca.getValue() == true) {
				frame.getContentPane().remove(offlinePlayersWindow);
				LudoView ludoView = new LudoView();
				ludoView.addActionListenerEsci(el -> {
					frame.getContentPane().remove(ludoView);
					frame.getContentPane().add(dialogue);
					frame.revalidate();
					frame.repaint();
					
				});
				LudoModel ludoModel = new LudoModel(numeroDiGiocatoriOffline.getValue(), bot.getValue());
				LudoCntrl ludoCntrl = new LudoCntrl(ludoModel, ludoView);
				ludoCntrl.initializeLudoView();
				frame.getContentPane().add(ludoView);
				frame.revalidate();
		        frame.repaint();
				gioca.setValue(false);
			}
		});
		
		onlinePlayersWindow.addActionListener(e -> {
			String resultOnline = e.getActionCommand();
			for (Entry<String, DoSomething> entry : eventi.entrySet()) {
				if(entry.getKey().equalsIgnoreCase(resultOnline)) {
					entry.getValue().doSomething(resultOnline);
					break;
				}
			}
			if(goBack.getValue() == true) {
				frame.getContentPane().remove(onlinePlayersWindow);
				frame.getContentPane().add(dialogue);
				frame.revalidate();
		        frame.repaint();
		        goBack.setValue(false);
			}
		});
	}

	private void popolaHashMapEventi(IntWrapper numeroDiGiocatoriOffline, BooleanWrapper bot, BooleanWrapper goBack,
			BooleanWrapper gioca, HashMap<String, DoSomething> eventi) {
		eventi.put("1", (String input) -> numeroDiGiocatoriOffline.setValue(1));
  		eventi.put("2", (String input) -> numeroDiGiocatoriOffline.setValue(2));
  		eventi.put("3", (String input) -> numeroDiGiocatoriOffline.setValue(3));
  		eventi.put("4", (String input) -> numeroDiGiocatoriOffline.setValue(4));
  		eventi.put("B", (String input) -> bot.setValue(true));
  		eventi.put("arrow", (String input) -> goBack.setValue(true));
  		eventi.put("gioca", (String input) -> gioca.setValue(true));
	}
}
