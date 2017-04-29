package baza;

import java.sql.*;
import java.util.ArrayList;

public class BazaConnection {
	Connection myConn;
	private Statement stat;
	private Folder folder;
	BazaConnection(){
		try{
			myConn = DriverManager.getConnection("jdbc:sqlite:albums");
			stat = myConn.createStatement();
			stat.execute("PRAGMA foreign_keys=ON");
			myConn.setAutoCommit(true);
			createAll();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public boolean hasFolder(){
		return (!(folder == null));
	}
	private void createAll(){
		try {
			
			stat.execute("create table if not exists folder(id integer primary key autoincrement, nazwa varchar(255) unique)");
			stat.execute("create table if not exists album(id integer primary key autoincrement, title varchar(255), author varchar(32), data datetime DEFAULT CURRENT_TIMESTAMP,mainurl varchar(255), folderid integer references folder(id))");
			stat.execute("create table if not exists zdjecie(id integer primary key autoincrement, photoUrl varchar(255) not null, albumid integer references album(id) on delete cascade)");
			stat.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void dropAll(){
		try {
			
			stat.execute("drop table if exists album");
			stat.execute("drop table if exists zdjecie");
			stat.execute("drop table if exists folder");
			stat.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void setFolder(Folder folder){
		this.folder = folder;
	}
	public Folder getFolder(){
		return folder;
	}
	public ArrayList<Folder> showFolders(){

		ArrayList<Folder> folders = null;
		try {
			stat = myConn.createStatement();
			ResultSet allFolders = stat.executeQuery("select * from folder");
			while(allFolders.next()){
				String nazwa = allFolders.getString("nazwa");
				Integer id = allFolders.getInt("id");
				Folder nowy = new Folder (nazwa,id);
				if (folders != null){
					folders.add(nowy);
				}else {
					folders = new ArrayList<>();
					folders.add(nowy);
				}
			}
			allFolders.close();
			stat.close();
		} catch (Exception e) {
			e.printStackTrace();
			try{
				stat.close();
			}catch (Exception es) {
				
			}
		}
		return folders;
	}
	public Folder newFolder(String name){
		Folder nowy = null;
		try {
			stat = myConn.createStatement();
			stat.execute("insert into folder(nazwa) values ('" + name + "')");
			ResultSet generatedKeys = stat.executeQuery("SELECT last_insert_rowid()");
			Integer id;
			if (generatedKeys.next()) {
			    id = generatedKeys.getInt(1);
			}else {
				throw new NullPointerException();
			}
			nowy = new Folder(name,id);
			generatedKeys.close();
			stat.close();
		} catch (Exception e) {
			try{
				stat.close();
			}catch (Exception es) {
				
			}
			e.printStackTrace();
		}
		return nowy;
	}
	public Folder selectFolder(String name){
		Folder nowy = null;
		try {
			stat = myConn.createStatement();
			ResultSet generatedKeys = stat.executeQuery("select id from folder where nazwa = '" + name + "'");
			Integer id;
			if (generatedKeys.next()) {
			    id = generatedKeys.getInt(1);
			}else {
				throw new NullPointerException();
			}
			nowy = new Folder(name,id);
			this.folder = nowy;
			stat.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nowy;
	}
	public void deleteFolder (Folder folder){
		try {
			stat = myConn.createStatement();
			stat.execute("delete from folder where id = '" + folder.getId() + "'");
			stat.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	boolean deleteAlbum (String mainurl){
		try {
			stat = myConn.createStatement();
			stat.execute("delete from album where mainurl = '" + mainurl + "'");
			stat.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			
			return false;
		}
	}
	boolean insertAlbum(Album photos){
		try {
			//najpierw album
			photos.setTitle( photos.getTitle().replaceAll("'", "''"));
			stat = myConn.createStatement();
			stat.execute("insert into album(title,mainurl,author,folderid) values ('"+ photos.getTitle() +"','"+ photos.getUrl() +"','"+ photos.getAuthor() +"',"+ folder.getId() +")");
			ResultSet generatedKeys = stat.executeQuery("SELECT last_insert_rowid()");
			Integer id;
			if (generatedKeys.next()) {
			    id = generatedKeys.getInt(1);
			}else {
				throw new NullPointerException();
			}
			myConn.setAutoCommit(false);
			PreparedStatement insert = myConn.prepareStatement("insert into zdjecie(photoUrl,albumid) values(?,?)");
			for (String a : photos.getAlbum()){
				insert.setString(1, a);
				insert.setInt(2,id);
				insert.addBatch();
			}
			insert.executeBatch();
			myConn.commit();
			myConn.setAutoCommit(true);
			stat.close();
			return true;
			//album
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean isInFolder(Album a){
		if (folder == null) return false;
		else{
			try {
				stat = myConn.createStatement();
				ResultSet set = stat.executeQuery("SELECT id FROM album WHERE mainurl ='"+a.getUrl()+"' and folderid = "+ folder.getId());
				if (set.next()){
					set.getInt(1);
					stat.close();
					return true;
				}
				else{
					stat.close();
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		
	}
	public ArrayList<Album> getAlbums(WindowScreen app, Integer limit, Integer offset){
		ArrayList<Album> albumy = null;
		try {
			stat = myConn.createStatement();
			ResultSet albums = stat.executeQuery("select * from album where folderid  = " + folder.getId() + " order by id desc LIMIT " + limit.toString() + " OFFSET " + offset.toString());
			while(albums.next()){
				/*Dla ka¿dego albumu:
				 * 1. wyjmij title,mainurl
				 * 2. Znajdz wszystkie zdjecia
				 * 3. Utwórz obiekt album
				 * 4. dodaj album do albumy
				 * 
				 * Jako ze dostep do danych bedzie raczej wolny, najlepiej zrobic limity 
				 * Dostep bedzie przez odpowiednio zmodyfikowany parser
				 */
				Integer id = albums.getInt("id");
				String title = albums.getString("title");
				String url = albums.getString("mainurl");
				String author = albums.getString("author");
				System.out.println(id+ " " + limit + " " + offset);

				Integer sum = 0;
				Statement secondary = myConn.createStatement();
				ResultSet count = secondary.executeQuery("select count(*) from zdjecie where albumid = "+ id.toString());
				while (count.next()){
					sum = count.getInt("count(*)");
				}
				count.close();
				ResultSet photos = secondary.executeQuery("select photoUrl from zdjecie where albumid = "+ id.toString());
				String[] album = new String[sum];
				int i = 0;
				
				while(photos.next()){
					album[i++] = photos.getString("photoUrl");
 				}
		
				Album nowy = null;
				if (sum == 1){
					nowy = new Album(album[0],app);
					nowy.setTitle(title);
					nowy.setUrl(url);
					nowy.setAuthor(author);
				}else if(sum > 1){
					nowy = new Album(album,app);
					nowy.setTitle(title);
					nowy.setUrl(url);
					nowy.setAuthor(author);
				}else{
					System.out.println("Nie utworzono");
				}
				if (album != null && albumy != null){
					albumy.add(nowy);
				}else if (album != null){
					albumy = new ArrayList<>();
					albumy.add(nowy);
				}
				secondary.close();
			}
			stat.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return albumy;
	}
	public ArrayList<Album> getAlbums(WindowScreen app, Integer limit, Integer offset, String query){
		ArrayList<Album> albumy = null;
		try {
			stat = myConn.createStatement();
			ResultSet albums = stat.executeQuery(query +" LIMIT " + limit.toString() + " OFFSET " + offset.toString());
			while(albums.next()){
				/*Dla ka¿dego albumu:
				 * 1. wyjmij title,mainurl
				 * 2. Znajdz wszystkie zdjecia
				 * 3. Utwórz obiekt album
				 * 4. dodaj album do albumy
				 * 
				 * Jako ze dostep do danych bedzie raczej wolny, najlepiej zrobic limity 
				 * Dostep bedzie przez odpowiednio zmodyfikowany parser
				 */
				Integer id = albums.getInt("id");
				String title = albums.getString("title");
				String url = albums.getString("mainurl");
				String author = albums.getString("author");
				System.out.println(id+ " " + limit + " " + offset);

				Integer sum = 0;
				Statement secondary = myConn.createStatement();
				ResultSet count = secondary.executeQuery("select count(*) from zdjecie where albumid = "+ id.toString());
				while (count.next()){
					sum = count.getInt("count(*)");
				}
				count.close();
				ResultSet photos = secondary.executeQuery("select photoUrl from zdjecie where albumid = "+ id.toString());
				String[] album = new String[sum];
				int i = 0;
				
				while(photos.next()){
					album[i++] = photos.getString("photoUrl");
 				}
		
				Album nowy = null;
				if (sum == 1){
					nowy = new Album(album[0],app);
					nowy.setTitle(title);
					nowy.setUrl(url);
					nowy.setAuthor(author);
				}else if(sum > 1){
					nowy = new Album(album,app);
					nowy.setTitle(title);
					nowy.setUrl(url);
					nowy.setAuthor(author);
				}else{
					System.out.println("Bie utworzono");
				}
				if (album != null && albumy != null){
					albumy.add(nowy);
				}else if (album != null){
					albumy = new ArrayList<>();
					albumy.add(nowy);
				}
				secondary.close();
			}
			stat.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return albumy;
	}
}
