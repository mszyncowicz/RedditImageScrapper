package baza;

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public abstract class MediaNode {
	protected StackPane viewerWrapper;
	protected double widthSetting;
	protected double heightSetting;
	protected Stage currentStage;
	protected boolean isError = false;
	protected boolean isZoomed = false;
	MediaNode(StackPane viewerWrapper, Stage stage){
		this.viewerWrapper = viewerWrapper;
		this.currentStage = stage;
	}
	public abstract void setUp() throws Exception;
	public abstract void dispose();
	public abstract void onClick();
	public abstract void setNext(String url) throws Exception;
	public boolean hasError(){
		return isError;
	}
	public Stage getCurrentStage() {
		return currentStage;
	}
	public void setCurrentStage(Stage currentStage) {
		this.currentStage = currentStage;
	}

}
