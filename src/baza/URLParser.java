package baza;
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
	Document doc;
	HashMap<String,Integer> mapa = new HashMap<>();
	URLParser(){
	}
	URLParser(String html){
		this.changeDoc(html);
	}
	void changeDoc(String html){
		try{
			doc = Jsoup.parseBodyFragment(html);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
   void parseTable(){
	   try{
	   Element table = doc.select("table").get(2);
	   Elements rows = table.select("tr");
	   for (int i = 1; i < rows.size()-1; i++) {
		   Element row = rows.get(i);
	       Elements cols = row.select("td");
	       Matcher m = Pattern.compile("\\<td>([^)]+)\\</td>").matcher(cols.get(3).toString());
	       //System.out.println(cols.get(3).text());
	       String a = cols.get(1).text().toString();
	       String z = cols.get(2).text().replaceAll(",", "");
	       if(mapa.get(a) != null){
	    	  int sum = mapa.get(a)+Integer.parseInt(z);
	    	  mapa.put(a, sum);
	       }
	       else mapa.put(a, Integer.parseInt(z));
	   }
	   
	   }catch (Exception e){
		   e.printStackTrace();
	   }
   }
 
   void toDB(){
	   BazaConnection polacz = new BazaConnection();
	   int size = mapa.size();
	   int i = 0;

	   try{
		   polacz.myConn.setAutoCommit(false);
		   PreparedStatement myStmt = polacz.myConn.prepareStatement("insert into imionam(name,nr) values (?,?) on duplicate key update nr=values(nr)");
		   for (String a : mapa.keySet()){
			   myStmt.setString(1, a);
			   myStmt.setInt(2, mapa.get(a));
			   myStmt.addBatch();
		   }
		   myStmt.executeBatch();
		   polacz.myConn.commit();
		   System.out.println("enx");
	   }catch (Exception e){
		   e.printStackTrace();
	   }
	   
   }
	
}
class ShowProgress implements Runnable{
    public int i;
    int max;
    ShowProgress(int i, int max){
        this.i = i;
        this.max = max;
    }
    @Override public void run(){
        while(i<max){
            System.out.println (i + " / " + max);
            try{
                  Thread.sleep(1000);
            } catch(Exception e){
                
            }
        }
  
    }
}
class RedditParser extends URLParser{
	ArrayList<Link> urls;

	RedditParser(){
		urls = new ArrayList<>();
	}
	
	String nextPage(){
		Element e = doc.getElementsByClass("next-button").get(0);
		Element a = e.select("a").get(0);
		return a.attr("href");
	}
	
	ArrayList<Link> parseImages(){
		ArrayList<Link> result = this.urls;
		   try{
			   //int all = doc.getElementsByAttributeValue("data-type", "link").size();
			  // int div = doc.getElementsByClass("score unvoted").size();
			   Elements allLinks = doc.getElementsByAttributeValue("data-type", "link");
			  // Pattern tumblr = Pattern.compile("http[s]*://[a-z0-9]+\\.tumblr.com/post/[0-9]+/[a-z0-9\\-]");
			  // Pattern album = Pattern.compile("http[s]*:\\/\\/[a-z0-9\\.]*imgur.com\\/a\\/[A-Za-z0-9]+");
			   //Pattern imgur = Pattern.compile("http[s]*:\\/\\/[a-z0-9\\.]*imgur.com\\/[A-Za-z0-9[^/]]{2,}");
			  // Pattern gfycat = Pattern.compile("http[s]*:\\/\\/gfycat.com\\/[A-Za-z0-9]{2,}");
			  // System.out.println(image);
			   for (int i = 0; i< allLinks.size();i++){
				   String a = allLinks.get(i).attr("data-url");
				   
				   String title = "title";
				   try{
				  // title = doc.getElementsByAttributeValue("href", a).get(1).text();
				   title = allLinks.get(i).select("a").get(1).text();
				   }catch (Exception e){
						  

				   }
	
				   //System.out.println("is Tumblr? " + tumblr.matcher(a).find());
				   //System.out.println("is Album imgurr? " + album.matcher(a).find());
				   //System.out.println("is imgurr? " + imgur.matcher(a).find());
				 //  System.out.println(doc.getElementsByClass("score unvoted").get(i).attr("title"));
				   Integer vote = 0;
				   try{
					  // vote = Integer.parseInt(allLinks.get(i).attr("data-rank"));
					   vote = Integer.parseInt(allLinks.get(i).getElementsByClass("score unvoted").get(0).text());

				   }catch (Exception e){
					  

				   }
				   //System.out.println(vote);
				   if (vote > 0){
					   if (a.contains(".jpg") || a.contains(".png") || a.contains(".gif") || a.contains(".mp4")){
					   //System.out.println(vote);
						   urls.add(new Link(a,vote, title));
					   } else if (a.contains("imgur.com/a/")){
						   
						   urls.add(new ImgurAlbum(a,vote, title));
					   }else if (a.contains("imgur")){

						   urls.add(new ImgurPhoto(a,vote, title));
					   }else if (a.contains("tumblr")){
						   urls.add(new TumblrAlbum(a,vote, title));
					   } else if (a.contains("gfycat.com")){
		
						   urls.add(new GfycatGif(a,vote, title));
					   }else if(a.contains("reddit")){
						   //System.out.println("Z³apanooooooooooooooooooooooooooooooooooooooooooooooooooooo ");
						   urls.add(new RedUpload(a,vote, title));
						  // Pattern red = Pattern.compile("https:\\/\\/i.reddituploads.com\\/[0-9a-z]{2,}");
						   //String data = allLinks.get(i).getElementsByClass("expando expando-uninitialized").attr("data-cachedhtml");
						   //System.out.println(data);
						   //Matcher z = red.matcher(data);
						   //if (z.find()){
							//   System.out.println("Z³apanooooooooooooooooooooooooooooooooooooooooooooooooooooo " + z.group(0));
							//   urls.add(new Link(z.group(0),vote,title));
						   //}
					   }
					    else if (a.contains("deviantart")){
					    	urls.add(new DeviantArt(a,vote, title));
						   
					   }
				   }
				   
			   }
			  
			  // System.out.println(image + " " + this.urls.size());
		   }catch(Exception e){
			   e.printStackTrace();
		   }
		   return result;
	   }
	void rest(){
		this.urls = new ArrayList<>();
	}
	void reset(){
		this.mapa = new HashMap<>();
		
		
	}
	class Link{
		String url;
		String photoUrl;
		
		Integer vote;

		String title;
		Link(String url,Integer vote,String title){
			this.url = url;
			this.vote = vote;
			this.title = title;
		}
		String[] getPhoto(){
			String[] s = new String[1];
			s[0] = this.url;
			return s;
		}
	}
	class ImgurPhoto extends Link{
		String photoUrl;
		ImgurPhoto(String url,Integer vote,String title){
			super(url,vote, title);
		}
		@Override
		String[] getPhoto(){
			String[] result = new String[1];
			if(photoUrl != null)result[0] = this.photoUrl;
			else{
				title = doc.getElementsByClass("post-title-container").select("h1").text();
				
				Element e = doc.getElementsByAttributeValue("property", "og:image").get(0);
				result[0] = e.attr("content");
				while(result[0].charAt(result[0].length()-1) != 'f' && result[0].charAt(result[0].length()-1) != 'g'  && result[0].charAt(result[0].length()-1) != 'm' ){
					result[0] = result[0].substring(0, result[0].length()-1);
				}
				this.photoUrl = result[0];
				return result;
			}
			return result;
		}
	}
	class ImgurAlbum extends Link{
		String[] photoUrl;
		ImgurAlbum(String url,Integer vote,String title){
			super(url,vote, title);
		}
		@Override
		String[] getPhoto(){
			if(photoUrl != null)return this.photoUrl;
			else{
				title = doc.getElementsByClass("post-title-container").select("h1").text();
				//Elements e = doc.getElementsByClass("post-images").select("img");
				Elements e = doc.getElementsByAttributeValue("itemtype", "http://schema.org/ImageObject");
				Elements es = doc.getElementsByAttributeValue("itemtype", "http://schema.org/VideoObject");
				String[] result = new String[e.size() + es.size()];
				for (int i = 0; i<e.size();i++){
					result[i] = "https://i.imgur.com/" + e.get(i).attr("id") + ".jpg";
				}
				for (int j = 0; j<es.size();j++){
					result[j + e.size()] = "https://i.imgur.com/" + es.get(j).attr("id") + ".gif";
				}
				this.photoUrl = result;
				return result;
			}
		}
	}
	class TumblrAlbum extends Link{
		String[] photoUrl;
		TumblrAlbum(String url,Integer vote,String title){
			super(url,vote, title);
		}
		@Override
		String[] getPhoto(){
			if(photoUrl != null)return this.photoUrl;
			else{
				Elements e = doc.getElementsByAttributeValue("property", "og:image");
			
				String[] result = new String[e.size()];
				for (int i = 0; i<e.size(); i++){
					result[i] = e.get(i).attr("content");
					
				}
				this.photoUrl = result;
				return result;
			}
		}
		
	}
	class GfycatGif extends Link{
		String photoUrl;
		GfycatGif(String url,Integer vote,String title){
			super(url,vote, title);
		}
		@Override
		String[] getPhoto(){
			String[] result = new String[1];
			if(photoUrl != null)result[0] = this.photoUrl;
			else{
				//Elements e = doc.getElementsByAttributeValue("name", "twitter:image");
				System.out.println(url);
				Elements e = doc.getElementsByAttributeValue("id", "mp4Source");
				result[0] = e.get(0).attr("src");
				System.out.println(result[0]);
				this.photoUrl = result[0];
				
			}
			return result;
		}
	}
	class RedUpload extends Link{
		String photoUrl;
		RedUpload(String url,Integer vote,String title){
			super(url,vote, title);
		}
		@Override
		String[] getPhoto(){
			
			this.photoUrl = url;
			String[] result = new String[1];
			result[0] = photoUrl;
			return result;
		}
	}
	class DeviantArt extends Link{
		String photoUrl;
		DeviantArt(String url,Integer vote,String title){
			super(url,vote, title);
		}
		@Override
		String[] getPhoto(){
			Elements e = doc.getElementsByAttribute("collect_rid");
			Elements al = doc.getElementsByAttribute("data-super-img");
			this.photoUrl = e.get(1).attr("src");
			//System.out.println(al.size);
			String[] result = new String[1 + al.size()];
			result[0] = photoUrl;
			
			int i = 1;
			for (Element ele : al){
				result[i++] = ele.attr("data-super-img");
			}
			//System.out.println("resultat" +result[0]);
			return result;
		}
	}
}

