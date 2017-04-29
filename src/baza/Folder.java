package baza;

import java.util.ArrayList;

public class Folder {
	private ArrayList<Album> albumy;
	private String nazwa;
	private Integer id;
	public Folder(String nazwa, Integer id) {
		this.nazwa = nazwa;
		this.id = id;
	}
	void setAlbumy(ArrayList<Album> albumy){
		this.albumy = albumy;
	}
	public ArrayList<Album> getAlbumy() {
		return albumy;
	}
	public String getNazwa() {
		return nazwa;
	}
	public Integer getId() {
		return id;
	}
}
