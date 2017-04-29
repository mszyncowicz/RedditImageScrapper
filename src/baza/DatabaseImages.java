package baza;

import java.util.ArrayList;

public class DatabaseImages {
	
	final int max = 20;
	private Integer current;
	private WindowScreen app;
	private BazaConnection baza;
	DatabaseImages(WindowScreen app, BazaConnection baza){
		current = 0;
		this.app = app;
		this.baza = baza;
	}
	public ArrayList<Album> getAlbumy(boolean isNext){
		
		if(isNext)current += max;
		else current -=max;
		return baza.getAlbums(app,max,current-max);
	}
	public ArrayList<Album> getAlbumyByAuthor(boolean isNext,String author){
		
		if(isNext)current += max;
		else current -=max;
		return baza.getAlbums(app,max,current-max,"select * from album where author = '"+author + "'");
	}
	public Integer getCurrent() {
		return current;
	}
}
