package ludoApp;
import java.awt.EventQueue;
import javax.swing.JFrame;

public class LudoApp {
	
	private JFrame frame;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LudoApp window = new LudoApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public LudoApp() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		InitialDialogueWindow dialogue = new InitialDialogueWindow();
		OfflinePlayersWindow offlinePlayersWindow = new OfflinePlayersWindow();
		OnlinePlayersWindow onlinePlayersWindow = new OnlinePlayersWindow();
		LudoAppCntrl ludoAppCntrl = new LudoAppCntrl(dialogue, offlinePlayersWindow, onlinePlayersWindow, frame);
		ludoAppCntrl.initializeGame();
	}

}
