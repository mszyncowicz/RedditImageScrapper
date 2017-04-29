package baza;



import java.io.File;
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

public class WindowScreen extends Application{
	//showgifs;
	private final int max = 20;
	private String author;
	private ImagePanel panel;
	private BazaConnection baza;
	private boolean isNextLoaded = false;
	private BetterScrollPane scroll;
	private Thread main = Thread.currentThread();
	private ImageParser parser;
	private Stage primaryStage;
	
	public BazaConnection getBaza() {
		return baza;
	}

	public void setBaza(BazaConnection baza) {
		this.baza = baza;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public static void main(String[] args) {
		
		launch(args);
	}
	
	public void setThread(Thread s){
		this.main = s;
	}
	public Thread getThread(){
		return main;
	}
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public BetterScrollPane getScroll() {
		return scroll;
	}

	public void setScroll(BetterScrollPane scroll) {
		this.scroll = scroll;
	}
	
	
	@Override
	public void start(Stage primary) throws Exception {
		primary.setTitle("Reddit Image Scrapper - RIS");
		baza = new BazaConnection();
		//baza.dropAll();
		//baza.createAll();
		//baza.newFolder("folder");
		//baza.setFolder(baza.selectFolder("folder"));
		primaryStage = primary;
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
	public void stop(){
		
	}
	private void alert(String title, String contentText){
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
			panel.setView(panel.getPanel().getView());
			scroll.setContent(panel.getView());
			scroll.setVvalue(0.0);
		}catch(NullPointerException e){
			alert("Folder jest pusty","Brak zawarto�ci do wy�wietlenia");
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
				if (dbimg.getCurrent() >=max) panel.getView().getChildren().add(p);
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

	private MenuBar createBookmarkMenu(BazaConnection baza, Button bookmarks){
		MenuBar menuFolder = new MenuBar ();
		Menu menuFile = new Menu("Bookmarks");
		menuFolder.getMenus().addAll(menuFile);
		SeparatorMenuItem separator = new SeparatorMenuItem();

		bookmarks.setDisable(true);
		MenuItem i = new MenuItem("Add folder");
		MenuItem j = new MenuItem("Delete folder");
		MenuItem k = new MenuItem("Add external link");
		k.setDisable(true);
		
		
		menuFile.getItems().addAll(k,i,j,separator);
		ArrayList<Folder> folders = baza.showFolders();
		ArrayList<MenuItem> items = new ArrayList<>();
	
		if (folders != null) {
			for (Folder ba : folders) {
				CheckMenuItem d = new CheckMenuItem(ba.getNazwa());
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
						k.setDisable(false);
						d.setSelected(true);
					} else {
						baza.setFolder(null);
						bookmarks.setDisable(true);
						k.setDisable(true);
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
						CheckMenuItem d = new CheckMenuItem(nowy.getNazwa());
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
								k.setDisable(false);
								d.setSelected(true);
							}else{
								baza.setFolder(null);
								bookmarks.setDisable(true);
								k.setDisable(true);
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
				String folderName = baza.getFolder().getNazwa();
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
		k.setOnAction(e->{
			addExternal();
			e.consume();
		});
		
		menuFile.getItems().addAll(items);
		return menuFolder;
	}
	private void addExternal(){
		Stage stage = new Stage();
		//stage.setResizable(false);
		Button addMore = new Button("Add more");
		Button add = new Button("Add link");
		Label labelForTitle = new Label("Title:");
		TextField tFForTitle = new TextField("");
		tFForTitle.setPrefWidth(300);
		HBox title = new HBox();
		title.getChildren().addAll(labelForTitle,tFForTitle);
		
		ArrayList<TextField> tFForLink = new ArrayList<>();
		Label labelForLink = new Label("Link:");
		tFForLink.add(new TextField());
		tFForLink.get(tFForLink.size()-1).setPrefWidth(300);
		HBox link = new HBox();
		link.getChildren().addAll(labelForLink,tFForLink.get(tFForLink.size()-1),addMore);
		VBox list = new VBox();
		list.getChildren().add(link);
		BorderPane root = new BorderPane();
		BetterScrollPane root1 = new BetterScrollPane();
		addMore.setOnAction(e->{
			if (tFForLink.get(tFForLink.size()-1).getText().isEmpty()){
				alert("Empty field","First put something in previous field");
			}else if (!tFForLink.get(tFForLink.size()-1).getText().contains(".jpg") && !tFForLink.get(tFForLink.size()-1).getText().contains(".png") && !tFForLink.get(tFForLink.size()-1).getText().contains(".webm") && !tFForLink.get(tFForLink.size()-1).getText().contains(".mp4") && !tFForLink.get(tFForLink.size()-1).getText().contains(".gif")){
				alert("invalid file format","Format is not Supported");
			}else{
				tFForLink.get(tFForLink.size()-1).setDisable(true);
				TextField nField = new TextField("");
				nField.setPrefWidth(300);
				tFForLink.add(nField);
				HBox link2 = new HBox();
				link2.getChildren().addAll(new Label("Link:"),nField,addMore);
				list.getChildren().add(link2);
			}
		});
		
		add.setOnAction(e->{
			if(tFForTitle.getText().isEmpty()){
				alert("No title","Put some title first!");
			} else{
				String[] links = new String[tFForLink.size()];
				int j = 0;
				for(TextField i : tFForLink){
					links[j++] = i.getText();
				}
				Album a = new Album(links, this);
				a.setTitle(tFForTitle.getText());
				a.setUrl(links[0]);
				baza.insertAlbum(a);
				stage.close();
			}
		});
		
		root.setTop(title);
		root.setCenter(list);
		root.setBottom(add);
		root1.setContent(root);
		Scene scene = new Scene(root1,500, 500);
		stage.setScene(scene);
		stage.show();
	}


}


