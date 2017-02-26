package baza;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class WindowTest extends Application{
	String testGif = "https://giant.gfycat.com/BraveSingleChickadee.gif";
	String working = "https://fat.gfycat.com/AchingCleanFrilledlizard.mp4";
	@Override
	public void start(Stage primary) throws Exception {
		// TODO Auto-generated method stub
		BorderPane root = new BorderPane();
		ImageView s = new ImageView(new Image(working,400,300,true,false,true));
		root.setCenter(s);
		Scene scene = new Scene(root, 600, 300,Color.LAVENDER);
		primary.setScene(scene);
		primary.show();
		Button b = new Button("show");
		b.setOnAction(e -> s.setImage(new Image(testGif,400,300,false,true,false)));
		root.setBottom(b);
	}
	public static void main(String[] args){
		launch();
	}
}
