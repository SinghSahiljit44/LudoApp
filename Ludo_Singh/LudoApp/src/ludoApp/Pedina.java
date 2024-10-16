package ludoApp;

import java.io.Serializable;

public class Pedina implements Serializable{
	
	private Colore colore;
	private int idPedina;
	private boolean finished = false;
	private int pos; 
	private boolean isAlive;
	private boolean bot;
	private boolean safeZone = true; 
	
	Pedina(Colore colore, int identificativoPedina, boolean bot){
		this.colore = colore;
		this.pos = 0;
		this.bot = bot;
		this.idPedina = identificativoPedina;
	}
	
	public boolean avanza(int passi) {
		if(pos + passi > 57) return false;
		pos = Math.max(pos + passi, 0);
		checkSafeZone();
		if(pos == 57) finished = true; 
		return true;
	}
	
	public boolean getBot() {
		return bot;
	}
	
	public int getPos() {
		return pos;
	}
	
	public Colore getColore() {
		return colore;
	}
	
	public int getId() {
		return idPedina;
	}
	
	public void morta() {
		if(safeZone == false) {
			pos = 0;
		}
	}
	
	public boolean getSafeZone() {
		return safeZone;
	}
	
	public void checkSafeZone() {
		if (pos == 0) safeZone = true;
		if (pos == 1) safeZone = true;
		else if (pos == 9) safeZone = true;
		else if (pos == 14) safeZone = true;
		else if (pos == 22) safeZone = true;
		else if (pos == 27) safeZone = true;
		else if (pos == 35) safeZone = true;
		else if (pos == 40) safeZone = true;
		else if (pos == 48) safeZone = true;
		else if (pos >= 52 && pos <= 57) safeZone = true;
		else safeZone = false;
	} 
	
	public boolean checkSafeZone(int pos) {
		if (pos == 0) return true;
		if (pos == 1) return true;
		else if (pos == 9) return true;
		else if (pos == 14) return true;
		else if (pos == 22) return true;
		else if (pos == 27) return true;
		else if (pos == 35) return true;
		else if (pos == 40) return true;
		else if (pos == 48) return true;
		else if (pos >= 52 && pos <= 57) return true;
		else return false;
	} 
	
	public void setPos(int posizione) {
		pos = posizione;
	}
	
	public boolean getFinished() {
		return finished;
	}
	
	public void setSafeZone(boolean value) {
		safeZone = value;
	}
	
	public boolean canAdvance(int passi) {
		if(pos + passi > 57) {
			return false;
		}else if(pos == 57) {
			return false;
		} else {
			return true;
		}
	}
	
}


