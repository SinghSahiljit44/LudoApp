package ludoApp;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;

public class LudoCntrl {
	
	LudoModel model;
	LudoView view;
	private boolean condDado = true;
	private boolean condPedine = false;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private final ReentrantLock lock = new ReentrantLock();
	
	LudoCntrl(LudoModel model, LudoView view) {
		this.model = model;
		this.view = view;
		
		view.addActionListenerDado(e -> {
			if (condDado == true) {
				view.dadoAnimation(model.lanciaDado());	
				model.rotateTurno();
				if (model.getIfInSpecialCondition() == true) {
					gestisciBotSequenziale();
					condDado = true;
					condPedine = false;
				}else {
					condDado = false;
					condPedine = true;
				}
			}
		});
		
		view.addActionListenerPedine(e -> {
			String action = e.getActionCommand();
			System.out.println(action);
			if (condPedine == true) {
				if(model.isActionValid(action)) {
					model.stepNext();
					SoundCntrl.MOSSA.play();
					view.posPedine(model.getPosizioniPedine());
					view.stepNext();
					 if (model.getIfNextBot() == true) {
	                        condDado = false;
	                        condPedine = false;
	                        gestisciBotSequenziale(); 
	                    } else {
	                        condDado = true;
	                        condPedine = false;
	                    }
				}
			}
		});
		
		model.addActionListenerGameFinished(e -> {
			view.gameFinished(model.getLeaderBoard());
		});
	}
	
	private void gestisciBotSequenziale() {
        if (model.getIfNextBot() == true) {
            condDado = false;
            condPedine = false;

            scheduler.schedule(() -> {
                SwingUtilities.invokeLater(() -> {
                    gestisciBot();
                });
            }, 2, TimeUnit.SECONDS);
        }
    }
	
	private void gestisciBot() {
        lock.lock();
        try {
            view.dadoAnimation(model.lanciaDado()); 
            model.rotateTurno();
            model.stepNextBot();
            scheduler.schedule(() -> {
                SwingUtilities.invokeLater(() -> {
                    view.posPedine(model.getPosizioniPedine());
                    view.stepNext();
                    if (model.getIfNextBot() == true) {
                        gestisciBotSequenziale();
                    } else {
                        condDado = true;
                        condPedine = false;
                    }
                });
            }, 2, TimeUnit.SECONDS);
        } finally {
            lock.unlock();
        }
    }

	public void initializeLudoView() {
		view.posPedine(model.getPosizioniPedine());
	}
	
}