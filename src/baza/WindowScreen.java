package baza;



import java.util.ArrayList;

import baza.RedditParser.Link;
import javafx.scene.input.MouseButton;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.*;



class ImagePanel{
	
	VBox container;
	ImagePanel current;
	WindowScreen app;
	void setApp(WindowScreen app){
		this.app =app;
	}
	void setPanel(ImagePanel panel){
		this.current = panel;
	}
	ImagePanel getPanel(){
		return this.current;
	}
	ArrayList<Album> albums;
	ImagePanel(WindowScreen app){
		setApp(app);
		container = new VBox();
		container.getStyleClass().add("hbox");
		container.setAlignment(Pos.CENTER);
		container.setPadding(new Insets(0,0,0,0));
		albums = new ArrayList<>();
	}
	public void addAlbum(String album, Link sz){
			Album a = new Album(album,app);
			a.setTitle(sz.title);
			a.setVote(sz.vote);
			a.setUrl(sz.url);
			HBox s = a.getAlbumPane();
			if (s!=null){
				container.getChildren().add(a.getAlbumPane());
			}
			albums.add(a);
			System.out.println("dodadadiadoijaeo " + albums.size());

	}
	public void addAlbum(String[] album, Link sz){
		Album a = new Album(album,app);
		a.setTitle(sz.title);
		a.setVote(sz.vote);
		a.setUrl(sz.url);
		HBox s = a.getAlbumPane();
		if (s!=null){
			container.getChildren().add(a.getAlbumPane());
		}
		albums.add(a);
		System.out.println("dodadadiadoijaeo " + albums.size());
	}
	public void addAlbums(ArrayList<Album> albumsy){
		for( Album a : albumsy){
			if(a != null){
				
				HBox s = a.getAlbumPane();
			
				if (s!=null){
					container.getChildren().add(a.getAlbumPane());
				}
				this.albums.add(a);
				
			}
			
		}
		
	}
	VBox getView(){
		return this.container;
	}
	void setView(VBox cont){
		this.container = cont;
	}
	void createCopy(VBox s){
		s.getChildren().addAll(container.getChildren());

	}
	void reset(){
		this.container = new VBox();
		container.getStyleClass().add("hbox");
		container.setAlignment(Pos.CENTER);
		container.setPadding(new Insets(0,0,0,0));
		System.out.println("jsfsb " + albums.size());
		System.out.println("bla");
		int i = 0;
		for (Album a : albums){
			try{
				System.out.println("bla " + (i++));
			a.remove();
			}catch(Exception e){
				
			}
		}
		System.out.println("bla");
		albums.removeAll(albums);		
		System.gc();
	}
	
}





public class WindowScreen extends Application{
	//showgifs;
	final int max = 20;
	ImagePanel panel;
	BazaConnection baza;
	boolean isNextLoaded = false;
	Thread main = Thread.currentThread();
	ImageParser parser;
	Stage prim;
	public static void main(String[] args) {
		launch(args);
	}
	
	void setThread(Thread s){
		this.main = s;
	}
	Thread getThread(){
		return main;
	}
	@Override
	public void start(Stage primary) throws Exception {
		primary.setTitle("Reddit Image Scrapper - RIS");
		baza = new BazaConnection();
		//baza.dropAll();
		//baza.createAll();
		baza.newFolder("folder");
		baza.setFolder(baza.selectFolder("folder"));
		prim = primary;
		BetterScrollPane scroll = new BetterScrollPane();
		BorderPane root = new BorderPane();
		root.setCenter(scroll);
		HBox top = new HBox();
		top.setPadding(new Insets(5,5,5,5));
		Label link1= new Label();
		TextField link2 = new TextField();
		Button link3 = new Button();
		link3.setText("Show images");
		link2.setPrefWidth(200);
		link1.setText("https://www.reddit.com/r/");
		
		Button bookmarks = new Button("Bookmarks");
		
		top.getChildren().addAll(link1,link2,link3,bookmarks);
		bookmarks.setAlignment(Pos.CENTER_RIGHT);
		top.setAlignment(Pos.TOP_CENTER);
		link3.setOnAction(e -> this.startParsing(scroll, link2.getText()));
		bookmarks.setOnAction(e -> this.databaseBrowser(scroll));
		root.setTop(top);

		Scene scene = new Scene(root, 1200, 800,Color.LAVENDER);
		primary.setScene(scene);
		primary.setMaximized(true);
		primary.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          @Override
			public void handle(WindowEvent we) {
	              if (scroll.getLock()){
	            	  scroll.setlock(false);
	              }
	          }
	      }); 
		primary.show();
		
		scroll.getStyleClass().add("hbox");
		scroll.getStylesheets().add("style.css");
		scroll.setVvalue(0);
		scroll.setFitToWidth(true);
		//scroll.setFitToHeight(true);
		scroll.setPannable(true);
		scroll.setOnMousePressed(e -> {
			
			if(e.getButton() == MouseButton.MIDDLE){
				if(!scroll.getLock()){
					scroll.setlock(true);
					
					scroll.setCursor(Cursor.CROSSHAIR);
					scroll.setLockCursor(e.getX(), e.getY());
					System.out.println("rozpoczeto! " +scroll.getLock());
					Thread t = new Thread(new Runnable(){
						@Override
						public void run(){
								try {
									System.out.println("rozpoczeto! t");
									while (scroll.getLock()){
								
										double vvalue = scroll.getVvalue();
										double newvvalue = vvalue + scroll.delta;
										if (newvvalue> 1){
											newvvalue = 1;
										}else if(newvvalue<0){
											newvvalue =0;
										}
										scroll.setVvalue(newvvalue);
										
										
									}
									
									System.out.println("Zakonczono");
								} catch (Exception e) {
									e.getMessage();
								}
								
							
							
						
						}
					});
					t.start();
				}else{
					scroll.setlock(false);
					System.out.println("nie scrollujemy! " + Thread.activeCount() + scroll.lock);
					scroll.setCursor(Cursor.DEFAULT);
				}
			}else{
				if (scroll.getLock()){
					scroll.setlock(false);
					scroll.setCursor(Cursor.DEFAULT);
				}
				System.out.println("nie scrollujemy! " + Thread.activeCount());
			}
		});
		scroll.setOnMouseMoved(e-> {
			scroll.setCurrentMousePosition(e.getX(), e.getY());
			if(scroll.getLock()){
				scroll.setDelta();
			}
			
		});

	}
	public void alert(String title, String contentText){
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(title);
		alert.setContentText(contentText);

		alert.showAndWait();
	}
	public void databaseBrowser(ScrollPane scroll){
		WindowScreen app = this;
		if(parser!=null) parser.deleteAll();
		if (panel != null){
			panel.getPanel().reset();
			panel.reset();
			}
		parser = null;
		//this.panel = new ImagePanel(this);
		ImagePanel panel = new ImagePanel(this);
		panel.setPanel(new ImagePanel(app));
		DatabaseImages dbimg = new DatabaseImages(this,this.baza);
		try{
			panel.getPanel().addAlbums(dbimg.getAlbumy(true));
			System.out.println(dbimg.current);
			panel.setView(panel.getPanel().getView());
			scroll.setContent(panel.getView());
			scroll.setVvalue(0.0);
		}catch(NullPointerException e){
			alert("Folder jest pusty","Brak zawartoœci do wyœwietlenia");
			e.printStackTrace();
		}
		Button b = new Button();
		b.setText("next");
		Button p = new Button("prev");
		
		isNextLoaded = false;
		if (panel.getPanel().albums.size() == max) panel.getView().getChildren().add(b);
		//t.start();
		b.setOnAction(e->{
			dbButtonAction(panel, scroll, dbimg, b,p,this,true);
		});
		p.setOnAction(e->{
			dbButtonAction(panel, scroll, dbimg, b,p,this,false);
		});
	}
	void dbButtonAction(ImagePanel panel, ScrollPane scroll, DatabaseImages dbimg, Button b, Button p,WindowScreen app, boolean isNext){
		Platform.runLater(new Runnable() {
			@Override
			public void run(){
				panel.getPanel().reset();
				isNextLoaded = false;
				try{
					panel.setPanel(new ImagePanel(app));
					panel.getPanel().addAlbums(dbimg.getAlbumy(isNext));
				}catch(NullPointerException e){
					alert("Riched end","There is no more images");
				}
				int size = panel.getPanel().albums.size();
				panel.setView(panel.getPanel().getView());
				scroll.setContent(panel.getView());
				
				scroll.setVvalue(0.0);
				panel.setPanel(new ImagePanel(app));
				
				System.out.println(size);
				if (size == max) panel.getView().getChildren().add(b);
				if (dbimg.current >=max) panel.getView().getChildren().add(p);
			}
		});
	}
	public void startParsing(ScrollPane scroll, String url){
		WindowScreen ill = this;
		if(parser!=null) parser.deleteAll();
		this.panel = new ImagePanel(this);
		Button b = new Button();
		b.setText("next");
		parser = new ImageParser(this);
		parser.setPage(url);
		parser.setPanel(new ImagePanel(this));
		parser.lookFor();
		parser.parseImages();
		panel.setPanel(parser.getPanel());
		panel.setView(panel.getPanel().getView());

		this.panel.getView().getChildren().add(b);
		panel.setApp(ill);
		scroll.setContent(panel.getView());

		
		parser.resetPanel();
		parser.rest();
		new Thread(new Runnable(){
			@Override
			public void run(){
				parser.resetPanel();
				parser.parseImages();
				isNextLoaded = true;
				getThread().interrupt();
				System.out.println("sg");
			}
		}).start();
		
		b.setOnAction(e-> {
			Platform.runLater(new Runnable() {
				@Override
				public void run(){
					//panel.reset();
					panel.getPanel().reset();
					while(!isNextLoaded){
						try{
							//main = Thread.currentThread();
							setThread(Thread.currentThread());
							wait(10000);
						}catch(Exception e){
	
						}
					
					}
					isNextLoaded = false;
					Thread t = new Thread(new Runnable(){
						@Override
						public void run(){
							parser.resetPanel();
							
							System.out.println("przed parsem");
							parser.parseImages();
							
							isNextLoaded = true;
							getThread().interrupt();
						}
					});
					panel.setView(parser.getPanel().getView());
					int size = parser.getPanel().albums.size();
					
					panel.setApp(ill);
					scroll.setContent(panel.getView());
					panel.setPanel(parser.getPanel());
					t.start();
					scroll.setVvalue(0.0);
					System.out.println("isajfisahfoasf " + panel.getPanel().albums.size());
					if (size == parser.max) panel.getView().getChildren().add(b);
					
					
					if (parser.nextPage.equals(parser.prevPage)){
						alert("riched the end of Reddit","Please select diffrent reddit or click showimages to replay parsing this one!");
					}
					
				}
			});
			
		});
	}
	void buttonAction(){
		
	}
	public void setText(final String[] newText) {
	    
	}
	Menu createBookmarkMenu(DatabaseImages dbimg){
		Menu menuFolder = new Menu ("Folders");
		return menuFolder;
	}


}


class BetterScrollPane extends ScrollPane{
	double x;
	double y;
	double lockX;
	double lockY;
	double delta = 0 ;
	boolean lock=false;
	void setlock(boolean lock){
		this.lock = lock;
	}
	boolean getLock(){
	
		return lock;
	}
	void setCurrentMousePosition(double x, double y){
		this.x = x;
		this.y = y;
	}
	void setLockCursor(double x, double y){
		lockX = x;
		lockY =y;
	}
	void setDelta(){
		double height = this.getHeight();
		this.delta = (this.y - this.lockY)/ (height * 100000);
	}
}