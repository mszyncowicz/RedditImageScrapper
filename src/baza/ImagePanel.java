package baza;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import baza.RedditParser.Link;

class ImagePanel{
	
	private VBox container;
	private ImagePanel current;
	private WindowScreen app;
	public ArrayList<Album> albums;
	public void setApp(WindowScreen app){
		this.app =app;
	}
	public void setPanel(ImagePanel panel){
		this.current = panel;
	}
	public ImagePanel getPanel(){
		return this.current;
	}
	
	public ImagePanel(WindowScreen app){
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
		a.setAuthor(sz.author);
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
					app.setAuthor(a.getAuthor());
					app.databaseBrowser(app.getScroll());
				});
				if (s!=null){
					container.getChildren().add(s);
				}
				this.albums.add(a);
				
			}
			
		}
		
	}
	public void setAuthorLink(Album a,HBox s){
		Hyperlink author = a.getAuthorLink();
		if (author != null){
			author.setOnAction(e->{
				//app.author = a.author;
				app.startParsing(app.getScroll(), a.getAuthor(), true);
			});
			
		}
		
	}
	public VBox getView(){
		return this.container;
	}
	public void setView(VBox cont){
		this.container = cont;
	}
	public void createCopy(VBox s){
		s.getChildren().addAll(container.getChildren());

	}
	public void reset(){
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
	}
	
}


