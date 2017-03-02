package baza;

import java.util.ArrayList;

public class DatabaseImages {
	//ArrayList<Album> albumy;
	final int max = 20;
	Integer current;
	WindowScreen app;
	BazaConnection baza;
	DatabaseImages(WindowScreen app, BazaConnection baza){
		current = 0;
		this.app = app;
		this.baza = baza;
	}
	ArrayList<Album> getAlbumy(boolean isNext){
		
		if(isNext)current += max;
		else current -=max;
		return baza.getAlbums(app,max,current-max);
	}
	ArrayList<Album> getAlbumyByAuthor(boolean isNext,String author){
		
		if(isNext)current += max;
		else current -=max;
		return baza.getAlbums(app,max,current-max,"select * from album where author = '"+author + "'");
	}
}
