package baza;

import java.util.ArrayList;

public class Folder {
	ArrayList<Album> albumy;
	String nazwa;
	Integer id;
	public Folder(String nazwa, Integer id) {
		this.nazwa = nazwa;
		this.id = id;
	}
	void setAlbumy(ArrayList<Album> albumy){
		this.albumy = albumy;
	}
}
