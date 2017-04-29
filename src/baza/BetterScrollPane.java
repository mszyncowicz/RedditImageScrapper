package baza;

import javafx.scene.control.ScrollPane;

/**
 * 
 * @author Fytyny
 * ScrollPane extended by essential variables for mouse middle button scrolling.
 */
public class BetterScrollPane extends ScrollPane{
	double x;
	double y;
	double lockX;
	double lockY;
	double delta = 0 ;
	boolean lock=false;
	public void setlock(boolean lock){
		this.lock = lock;
	}
	public boolean getLock(){
	
		return lock;
	}
	public void setCurrentMousePosition(double x, double y){
		this.x = x;
		this.y = y;
	}
	public void setLockCursor(double x, double y){
		lockX = x;
		lockY =y;
	}
	public void setDelta(){
		double height = this.getHeight();
		this.delta = (this.y - this.lockY)/ (height * 100000);
	}
}