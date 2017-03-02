package baza;



import java.util.ArrayList;

import baza.RedditParser.Link;
import javafx.scene.input.MouseButton;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
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
			a.author = sz.author;
			HBox s = a.getAlbumPane();
			if (s!=null){
				setAuthorLink(a,s);
				container.getChildren().add(s);
			}
			albums.add(a);
			System.out.println("dodadadiadoijaeo " + albums.size());

	}
	public void addAlbum(String[] album, Link sz){
		System.out.println( "bla " + sz.title + sz.author );
		Album a = new Album(album,app);
		a.setTitle(sz.title);
		a.setVote(sz.vote);
		a.setUrl(sz.url);
		a.author = sz.author;
		System.out.println(a.title + " " + a.author + " " + sz.title +" " +a.vote );
		HBox s = a.getAlbumPane();
		
		if (s!=null){
			setAuthorLink(a,s);
			container.getChildren().add(s);
		}
		albums.add(a);
		System.out.println("dodadadiadoijaeo " + albums.size());
	}
	public void addAlbums(ArrayList<Album> albumsy){
		for( Album a : albumsy){
			if(a != null){
				
				HBox s = a.getAlbumPane();
				Hyperlink author = a.getAuthorLink();
				author.setOnAction(e->{
					app.author = a.author;
					app.databaseBrowser(app.scroll);
				});
				if (s!=null){
					container.getChildren().add(s);
				}
				this.albums.add(a);
				
			}
			
		}
		
	}
	void setAuthorLink(Album a,HBox s){
		Hyperlink author = a.getAuthorLink();
		author.setOnAction(e->{
			//app.author = a.author;
			app.startParsing(app.scroll, a.author, true);
		});
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
	String author;
	ImagePanel panel;
	BazaConnection baza;
	boolean isNextLoaded = false;
	BetterScrollPane scroll;
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
		//baza.newFolder("folder");
		//baza.setFolder(baza.selectFolder("folder"));
		prim = primary;
		scroll = new BetterScrollPane();
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
		
		Button bookmarks = new Button("Show");
		
		top.getChildren().addAll(link1,link2,link3);
		top.getChildren().add(this.createBookmarkMenu(baza,bookmarks));
		top.getChildren().add(bookmarks);
		bookmarks.setAlignment(Pos.CENTER_RIGHT);
		top.setAlignment(Pos.TOP_CENTER);
		link3.setOnAction(e -> this.startParsing(scroll, link2.getText(),false));
		bookmarks.setOnAction(e -> {
			author = null;
			this.databaseBrowser(scroll);
			});
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
			if (author == null) panel.getPanel().addAlbums(dbimg.getAlbumy(true));
			else panel.getPanel().addAlbums(dbimg.getAlbumyByAuthor(true,author));
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
					if (author == null) panel.getPanel().addAlbums(dbimg.getAlbumy(isNext));
					else panel.getPanel().addAlbums(dbimg.getAlbumyByAuthor(isNext,author));
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
	public void startParsing(ScrollPane scroll, String url, boolean isAuthor){
		WindowScreen ill = this;
		if(parser!=null) parser.deleteAll();
		this.panel = new ImagePanel(this);
		Button b = new Button();
		b.setText("next");
		parser = new ImageParser(this);
		if (!isAuthor)parser.setPage(url);
		else parser.setAuthor(url);
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
	MenuBar createBookmarkMenu(BazaConnection baza, Button bookmarks){
		MenuBar menuFolder = new MenuBar ();
		Menu menuFile = new Menu("Bookmarks");
		menuFolder.getMenus().addAll(menuFile);
		SeparatorMenuItem separator = new SeparatorMenuItem();

		bookmarks.setDisable(true);
		MenuItem i = new MenuItem("Add folder");
		MenuItem j = new MenuItem("Delete folder");
		
		
		
		
		menuFile.getItems().addAll(i,j,separator);
		ArrayList<Folder> folders = baza.showFolders();
		ArrayList<MenuItem> items = new ArrayList<>();
	
		if (folders != null) {
			for (Folder ba : folders) {
				CheckMenuItem d = new CheckMenuItem(ba.nazwa);
				d.setOnAction(e -> {
					if (d.isSelected()) {

						for (MenuItem za : menuFile.getItems()) {
							if (za instanceof CheckMenuItem) {
								CheckMenuItem dwa = (CheckMenuItem) za;
								dwa.setSelected(false);
							}
						}

						baza.setFolder(ba);
						bookmarks.setDisable(false);
						d.setSelected(true);
					} else {
						baza.setFolder(null);
						bookmarks.setDisable(true);
						d.setSelected(false);
					}
					e.consume();
				});

				items.add(d);
			}
		}
		i.setOnAction(e->{
			Stage stage = new Stage();
			TextField name = new TextField("Inser unique folder name");
			Button go = new Button("Add");
			HBox root = new HBox();
			root.getChildren().addAll(name,go);
			go.setOnAction(f->{
				Platform.runLater(new Runnable() {
					public void run() {
						Folder nowy = baza.newFolder(name.getText());
						CheckMenuItem d = new CheckMenuItem(nowy.nazwa);
						d.setOnAction(g-> {
							if (d.isSelected()){
							
								for (MenuItem za : menuFile.getItems()){
									if (za instanceof CheckMenuItem){
										CheckMenuItem dwa = (CheckMenuItem)za;
										dwa.setSelected(false);
									}
								}
						
								baza.setFolder(nowy);
								bookmarks.setDisable(false);
								d.setSelected(true);
							}else{
								baza.setFolder(null);
								bookmarks.setDisable(true);
								d.setSelected(false);
							}
							g.consume();
							});
						menuFile.getItems().add(d);
						stage.close();
						f.consume();
					}
				});
				
			});
			Scene scena = new Scene(root);
			stage.setScene(scena);
			stage.setResizable(false);
			stage.show();
			e.consume();
		});
		
		j.setOnAction(e->{
			Platform.runLater(new Runnable() {
				public void run() {
			if (baza.getFolder() != null){
				String folderName = baza.getFolder().nazwa;
				Stage stage = new Stage();
				stage.setResizable(false);
				TextField name = new TextField("Inser current folder name to delete");
				Button go = new Button("Delete");
				HBox root = new HBox();
				root.getChildren().addAll(name,go);
				go.setOnAction(f->{
					
							System.out.println(name.getText() + " " + folderName + " " + name.equals(folderName) );
							if (name.getText().equals(folderName)){
							
								baza.deleteFolder(baza.getFolder());
								baza.setFolder(null);
						
								MenuItem toDelete = null;
								for (MenuItem za : menuFile.getItems()){
									if (za instanceof CheckMenuItem){
										if (za.getText().equals(folderName)){
											toDelete = za;
										}
										
									}
								}
								if (toDelete != null) menuFile.getItems().remove(toDelete);
					
							}
							stage.close();
							f.consume();
							
						
					
				
				});
				Scene scene = new Scene(root);
				stage.setScene(scene);
				stage.show();
			}else{
				alert("Select folder first","you have to select folder to delete it");
			}
			e.consume();
				}
			});
		});
		
		menuFile.getItems().addAll(items);
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