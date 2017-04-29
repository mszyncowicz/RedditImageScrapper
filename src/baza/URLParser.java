package baza;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.*;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.regex.*;
public class URLParser {
	//String url;
	protected Document doc;
	protected HashMap<String,Integer> mapa = new HashMap<>();
	URLParser(){
	}
	URLParser(String html){
		this.changeDoc(html,false);
	}
	public void changeDoc(String html, boolean isURL){
		try{
			if (!isURL) doc = Jsoup.parseBodyFragment(html);
			else{
				URL url = new URL(html);
				doc = Jsoup.parse(url, 3000);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
  
	
}


