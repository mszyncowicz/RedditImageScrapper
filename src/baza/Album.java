package baza;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import javafx.scene.input.ClipboardContent;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.labs.util.event.MouseControlUtil;

public class Album{
	String[] album;
	boolean isZoomed = false;
	HBox box;
	String author;
	int current = 0;
	Image painting;
	ImageView paint;
	Scene scene;
	Button button1;
	Button button2;
	Button like;
	String title,url;
	Integer vote;
	boolean isFav = false;
	boolean isAlbum = true;
	Media media;
	File f;
	MediaView viewer;
	WindowScreen app;
	boolean isPlaying = false;
	StackPane viewerWrapper;
	javafx.scene.media.MediaPlayer player;
	boolean isVideo = false;
	Album(WindowScreen app){
		setApp(app);
	}
	void setPainting(){
			try {
				paint = new ImageView();
				paint.setPreserveRatio(true);
				paint.setFitWidth(500);
			} catch (Exception e) {

				current = -1;
			}

	}
	void setApp(WindowScreen app){
		this.app = app;
	}
	void setPhoto (String photo){
		if (photo.substring(photo.length()-4).contains(".gif")){

			try {
				this.painting = new Image(photo,true);
				System.gc();
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			
		}else if (photo.contains(".mp4")){
			try {
				
				if (photo.charAt(4) == 's'){
					photo = photo.substring(5);
					photo = "http" + photo;
	
				}
				this.media = new Media(new URL(photo).toURI().toURL().toString());
			    player = new javafx.scene.media.MediaPlayer(media);
			    System.out.println("odtwarzam " + photo);
			    
			    this.viewer = new MediaView(player);
			    isVideo = true;
			} catch (Exception e) {
						// TODO: handle exception
				e.printStackTrace();
			}
			
		}else{
			painting = new Image(photo,true);
			paint = new ImageView(painting);
			paint.setPreserveRatio(true);
			paint.setFitWidth(500);
			//isAlbum = false;
		}
		
		
	}
	Album(String photo,WindowScreen app){
		setApp(app);
		isAlbum = false;
		this.album = new String[1];
		this.album[0] = photo;
		System.out.println("to: " + photo);
		if(photo != null) this.setPhoto(photo);
	}
	Album(String[] album,WindowScreen app){
		setApp(app);
		if (album.length <2) isAlbum = false;
		this.album = album;
		
		if(album[0] != null) this.setPhoto(album[0]);
	}
	void remove(){
		try{ 
			System.out.println(this.url);
			album = null;
			if (!isVideo){
				System.out.println("nie vid");
				this.painting.cancel();
				this.paint.setImage(null);
			}else{
				System.out.println("vid");
				javafx.scene.media.MediaPlayer s = this.player;
				

				//this.player.stop();
				System.out.println("vid");
				this.player = null;
				new Thread(new Runnable(){
					
					public void run(){
						s.dispose();
					}
					
				}).start();
				this.player = null;
				System.out.println("vid");
				this.viewer =null;
				
	
				System.out.println("usuwam " +album[current]);
				
				
			}
			this.album = null;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		//System.gc();
	}
	int next(){
		if (current+1 == album.length){
			current =0;
		}else{
			current++;
		}
		painting = new Image(album[current]);
		this.setImage(painting);
		System.out.println(album[current] +" " + album.length);
		return current;
	}
	void setImage(Image a){
		Timeline timeline1 = new Timeline();
		Timeline timeline2 = new Timeline();
		timeline1.getKeyFrames().addAll(
				new KeyFrame(Duration.ZERO,
						new KeyValue(paint.opacityProperty(),1)
				
				 ),new KeyFrame(Duration.millis(300),
							new KeyValue(paint.opacityProperty(),0)));
		timeline2.getKeyFrames().addAll(
				new KeyFrame(Duration.ZERO,
						new KeyValue(paint.opacityProperty(),0)
				
				 ),new KeyFrame(Duration.millis(200),
							new KeyValue(paint.opacityProperty(),1)));
		timeline1.setOnFinished(e -> {
			paint.setImage(a);
			timeline2.play();
		});
		timeline1.play();
		
	}
	int prev(){
		if(current-1 <0){
			current = album.length-1;
		} else {
			current--;
		}
		System.out.println(album[current] +" " + album.length);
		painting = new Image(album[current],500,0,true,false);
		this.setImage(painting);
		return current;
	}
	ImageView getImageView(){
		return paint;
	}
	void setUrl(String url){
		this.url= url;
	}
	void setTitle(String title){
		this.title = title;
	}
	void setVote(Integer vote){
		this.vote = vote;
	}
	boolean isZoomed(){
		return isZoomed;
	}
	void zoom(){
		if(isZoomed){
			paint.setFitWidth(500);
			isZoomed = false;
			System.out.println(this.album[current]);
		}else{
			paint.setFitWidth(this.painting.getWidth());
			isZoomed = true;
		}
		
	}
	HBox getAlbumPane(){
		button1 = new Button();
		button2 = new Button();
		button1.setText("next");
		button2.setText("prev");
		like = new Button();
		like.setText("+");
		Button download = new Button();
		button1.setMinWidth(200);
		button2.setMinWidth(200);
		VBox vbox = new VBox();
		
		HBox hbox = new HBox();
		vbox.setAlignment(Pos.CENTER);

		Label title = new Label();
		title.setText(this.title);
		title.setId("Title");
		
		Hyperlink url = new Hyperlink();
		url.setText(this.url);
		url.setOnAction(t -> {
		    app.getHostServices().showDocument(url.getText());
		});
		url.setId("Url");
		Label vote = null;
		if (this.author!= null){
			vote = new Label();
			
			vote.setText(this.author.toString());
			vote.setId("Vote");
			vbox.setId("Content");
		}
		
		Album a = this;
		vbox.getChildren().addAll(title);
		if (this.author != null){
			vbox.getChildren().addAll(vote);
		}
		if (!this.isVideo){
			this.viewerWrapper = new StackPane();
			this.viewerWrapper.getChildren().add(this.getImageView());
			vbox.getChildren().add(this.viewerWrapper);
		} else{
			this.viewerWrapper = new StackPane();
			this.viewerWrapper.getChildren().add(this.viewer);
			vbox.getChildren().add(this.viewerWrapper);
			
		}
		like.setId("Like");
		like.setOnAction(e-> {
			
			Platform.runLater(new Runnable(){
				@Override
				public void run(){
					if (app.baza.hasFolder()){
						if (!app.baza.isInFolder(a)){
							if(app.baza.insertAlbum(a)){
								like.setStyle("-fx-text-fill:Crimson;-fx-font-size: 12px; ");
							}else{
								Alert alert = new Alert(AlertType.INFORMATION);
								alert.setTitle("Error");
								alert.setHeaderText("Database Error!!");
								alert.setContentText("Its our fault lol");

								alert.showAndWait();
							}
						}else{
							if(app.baza.deleteAlbum(a.url)){
								like.setStyle("-fx-text-fill:black; -fx-font-size: 18px");
							}else{
								Alert alert = new Alert(AlertType.INFORMATION);
								alert.setTitle("Error");
								alert.setHeaderText("Database Error!!");
								alert.setContentText("Its our fault lol");

								alert.showAndWait();
							}
						}
						
					} else{
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("No folder selected");
						alert.setHeaderText("No folder selected");
						alert.setContentText("You have to select  folder first!");

						alert.showAndWait();
					}
					hbox.getParent().requestFocus();

				}
			});
			e.consume();
		});
		
		download.setText("\u25BC");
		download.setId("Like");
		download.setOnAction(e->{
			new Thread(new Runnable() {
				public void run() {
					try {
						//using current folder
						String path = System.getProperty("user.dir").toString();
						String folder = "/Downloads/";
						new File(path + folder).mkdirs();
						URL zet = new URL(a.album[current]);
						File sa = new File(path + folder + zet.getFile());
						sa.createNewFile();
						FileUtils.copyURLToFile(zet, sa);
						System.out.println(sa.getAbsolutePath());
					} catch (Exception e2) {
						// TODO: handle exception						
						e2.printStackTrace();
					}
				}
			}).start();
			hbox.getParent().requestFocus();
		});
		HBox list= new HBox();
		list.getChildren().addAll(like,download);
		this.viewerWrapper.setOnMouseEntered(e -> {
			Platform.runLater(new Runnable(){
				@Override
				public void run(){
					viewerWrapper.getChildren().add(list);
					StackPane.setAlignment(list, Pos.TOP_LEFT);
					if (app.baza.isInFolder(a)){
						like.setStyle("-fx-text-fill:Crimson; ");
					} else{
						like.setStyle("-fx-font-size: 18px;");
					}
					
				}
			});
			e.consume();
		});
		this.viewerWrapper.setOnMouseExited(e -> {
			this.viewerWrapper.getChildren().remove(list);
			e.consume();
		});
		
		
		
		button1.setOnAction(e -> {
		
			Thread t = new Thread(new Runnable(){
				@Override
				public void run(){
					next();
				
				}
			});
			t.start();
			e.consume();
		});
		button2.setOnAction(e -> {
		
			Thread t = new Thread(new Runnable(){
				@Override
				public void run(){
					prev();
				
				}
			});
			t.start();
			e.consume();
		});

		
		hbox.setAlignment(Pos.CENTER);
		
		
		
		vbox.getChildren().add(url);
		vbox.setMaxHeight(600);
		vbox.setMaxWidth(500);
		//layout.getChildren().add(layout);
		if (this.isAlbum){
			hbox.getChildren().add(button2);
			hbox.getChildren().add(vbox);
			hbox.getChildren().add(button1);
		} else hbox.getChildren().add(vbox);
		
		
		hbox.setMaxSize(500, 600);
		if (!this.isVideo){
			//this.viewerWrapper.setOnMouseEntered(e-> hbox.setCursor(Cursor.CLOSED_HAND));
			//this.viewerWrapper.setOnMouseExited(e-> hbox.setCursor(Cursor.DEFAULT));
			
			this.viewerWrapper.setOnMouseClicked(e -> {
				if (!album[current].contains(".gif"))
					this.zoom();
				else{
					try {
						Stage stage = new Stage();
						stage.setTitle("Gif player");
			            ImageView nowy = new ImageView();
			            StackPane s = new StackPane();
			            s.getChildren().add(nowy);
			            
			            
					 	new Thread(new Runnable(){
					 		@Override
							public void run(){
					 			System.out.println("yo");
					            Image gif = new Image(album[current]);
					            
					            nowy.setImage(gif);
					            stage.setHeight(gif.getHeight());
					            stage.setWidth(gif.getWidth());
					            
					 		}
					 	}).start();
					 	
			            //Image gif = new Image(album[current]);
			            Scene as = new Scene(s, 200,200);
			            s.prefHeightProperty().bind(as.heightProperty());
			            s.prefWidthProperty().bind(as.widthProperty());
			            nowy.fitWidthProperty().bind(s.widthProperty());
			            nowy.fitHeightProperty().bind(s.heightProperty());
			            nowy.setPreserveRatio(true);
			            stage.setScene(as);
			            stage.show();
			            
			        }
			        catch (Exception ed) {
			            ed.printStackTrace();
			        }
				}
				e.consume();
			});
		}else{
			this.viewerWrapper.prefHeightProperty().bind(this.viewer.fitHeightProperty());
			this.viewerWrapper.prefWidthProperty().bind(this.viewer.fitWidthProperty());
			this.viewerWrapper.setMaxSize(app.prim.getMaxWidth(), app.prim.getMaxHeight());
			Label label = new Label("\u25B6");
			label.setFont(new Font(150));
			//label.setStyle("-fx-text-fill:yellow;");
		//previus color	ALICEBLUE
			//color.
			label.setStyle("-fx-text-fill: #ff1e6d;  -fx-border-insets: 2 0 0 0; -fx-border-width: 2; -fx-effect: dropshadow( gaussian , Black , 0,0,3,3 );");
			this.viewerWrapper.getChildren().add(label);
			this.viewer.setPreserveRatio(true);
			this.viewer.setFitWidth(500);
			label.setMouseTransparent(true);
			player.setOnError(new Runnable(){
    	    	public void run(){
    	    		viewerWrapper.getChildren().remove(label);
    	    		Label error = new Label ("Video is corrupted");
    	    		viewerWrapper.getChildren().add(error);
    	    		player.dispose();
  
    	    	}
    	    });
			this.viewerWrapper.setOnMouseClicked(e -> {
				if(e.getButton().equals(MouseButton.PRIMARY)){
			 		 if (e.getClickCount()== 2){
			 			 if (this.isZoomed){
			 				isZoomed = false;
			 				this.viewer.setFitWidth(500);
			 			 }else{
			 				 this.isZoomed = true;
			 				this.viewer.setFitWidth(1200);
			 			 }
			 		 }
			 		 if(play()) this.viewerWrapper.getChildren().remove(label);
			 		 else this.viewerWrapper.getChildren().add(label);
			 	 } else{
			 		 this.player.stop();
			 	 }
			 	 
			 	 
				e.consume();
			});
			
		}
		//MouseControlUtil.makeDraggable(this.viewerWrapper);
		
		this.viewerWrapper.setOnDragDetected(e->{
			e.consume();
		});
		hbox.setPadding(new Insets(0,0,0,0));
		vbox.setPadding(new Insets(0,0,0,0));
		box = hbox;
		return hbox;
	}
	
	boolean play(){
		if (!isPlaying){
			isPlaying = true;
			player.play();
			System.out.println(isPlaying);
			player.setCycleCount(MediaPlayer.INDEFINITE);
		}else{
			isPlaying = false;
			player.pause();
		}
		return isPlaying;
	}

	
}
