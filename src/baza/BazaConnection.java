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
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	boolean hasFolder(){
		return (!(folder == null));
	}
	void createAll(){
		try {
			
			stat.execute("create table if not exists folder(id integer primary key autoincrement, nazwa varchar(255) unique)");
			stat.execute("create table if not exists album(id integer primary key autoincrement, title varchar(255), data datetime DEFAULT CURRENT_TIMESTAMP,mainurl varchar(255), folderid integer references folder(id))");
			stat.execute("create table if not exists zdjecie(id integer primary key autoincrement, photoUrl varchar(255) not null, albumid integer references album(id) on delete cascade)");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	void dropAll(){
		try {
			
			stat.execute("drop table if exists album");
			stat.execute("drop table if exists zdjecie");
			stat.execute("drop table if exists folder");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	void setFolder(Folder folder){
		this.folder = folder;
	}
	Folder getFolder(){
		return folder;
	}
	ArrayList<Folder> showFolders(){
		ArrayList<Folder> folders = null;
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return folders;
	}
	Folder newFolder(String name){
		Folder nowy = null;
		try {
			stat.execute("insert into folder(nazwa) values ('" + name + "')");
			ResultSet generatedKeys = stat.executeQuery("SELECT last_insert_rowid()");
			Integer id;
			if (generatedKeys.next()) {
			    id = generatedKeys.getInt(1);
			}else {
				throw new NullPointerException();
			}
			nowy = new Folder(name,id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nowy;
	}
	Folder selectFolder(String name){
		Folder nowy = null;
		try {

			ResultSet generatedKeys = stat.executeQuery("select id from folder where nazwa = '" + name + "'");
			Integer id;
			if (generatedKeys.next()) {
			    id = generatedKeys.getInt(1);
			}else {
				throw new NullPointerException();
			}
			nowy = new Folder(name,id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nowy;
	}
	void deleteFolder (Folder folder){
		try {
			stat.execute("delete folder where id = '" + folder.id + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	boolean deleteAlbum (String mainurl){
		try {
			stat.execute("delete from album where mainurl = '" + mainurl + "'");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	boolean insertAlbum(Album photos){
		try {
			//najpierw album
			photos.title = photos.title.replaceAll("'", "''");

			stat.execute("insert into album(title,mainurl,folderid) values ('"+ photos.title +"','"+ photos.url +"',"+ folder.id +")");
			ResultSet generatedKeys = stat.executeQuery("SELECT last_insert_rowid()");
			Integer id;
			if (generatedKeys.next()) {
			    id = generatedKeys.getInt(1);
			}else {
				throw new NullPointerException();
			}
			myConn.setAutoCommit(false);
			PreparedStatement insert = myConn.prepareStatement("insert into zdjecie(photoUrl,albumid) values(?,?)");
			for (String a : photos.album){
				insert.setString(1, a);
				insert.setInt(2,id);
				insert.addBatch();
			}
			insert.executeBatch();
			myConn.commit();
			myConn.setAutoCommit(true);
			return true;
			//album
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	boolean isInFolder(Album a){
		if (folder == null) return false;
		else{
			try {
				ResultSet set = stat.executeQuery("SELECT id FROM album WHERE mainurl ='"+a.url+"' and folderid = "+ folder.id);
				if (set.next()){
					set.getInt(1);
					return true;
				}
				else return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		
	}
	ArrayList<Album> getAlbums(WindowScreen app, Integer limit, Integer offset){
		ArrayList<Album> albumy = null;
		try {
			ResultSet albums = stat.executeQuery("select * from album where folderid  = " + folder.id + " order by id desc LIMIT " + limit.toString() + " OFFSET " + offset.toString());
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
				}else if(sum > 1){
					nowy = new Album(album,app);
					nowy.setTitle(title);
					nowy.setUrl(url);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		return albumy;
	}
}
