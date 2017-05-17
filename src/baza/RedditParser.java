package baza;

import java.util.ArrayList;
import java.util.HashMap;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class RedditParser extends URLParser{
	private ArrayList<Link> urls;

	public RedditParser(){
		urls = new ArrayList<>();
	}
	
	public String nextPage(){
		Element e = doc.getElementsByClass("next-button").get(0);
		Element a = e.select("a").get(0);
		return a.attr("href");
	}
	
	public ArrayList<Link> parseImages(){
		ArrayList<Link> result = this.urls;
		   try{

			   Elements allLinks = doc.getElementsByAttributeValue("data-type", "link");

			   for (int i = 0; i< allLinks.size();i++){
				   String a = allLinks.get(i).attr("data-url");
				   
				   String title = "title";
				   String author = null;
				   try{
			
					   author = allLinks.get(i).attr("data-author");
				   title = allLinks.get(i).select("a").get(1).text();
			
				   }catch (Exception e){
						  //continue;

				   }
	
				
				   Integer vote = 0;
				   try{
		
					   vote = Integer.parseInt(allLinks.get(i).getElementsByClass("score unvoted").get(0).text());

				   }catch (Exception e){
					  

				   }
				   //System.out.println(vote);
				   if (vote >= 0){
					   if (a.contains(".jpg") || a.contains(".png") || a.contains(".gif") || a.contains(".mp4")|| a.contains(".webm")){
					   //System.out.println(vote);
						   urls.add(new Link(a,vote, title,author));
					   } else if (a.contains("imgur.com/a/") || a.contains("imgur.com/gallery/")){
						   
						   urls.add(new ImgurAlbum(a,vote, title,author));
					   }else if (a.contains("imgur")){

						   urls.add(new ImgurPhoto(a,vote, title,author));
					   }else if (a.contains("tumblr")){
						   urls.add(new TumblrAlbum(a,vote, title,author));
					   } else if (a.contains("gfycat.com")){
		
						   urls.add(new GfycatGif(a,vote, title,author));
					   }else if(a.contains("reddit")){
						   urls.add(new RedUpload(a,vote, title,author));
					
					   }
					    else if (a.contains("deviantart")){
					    	urls.add(new DeviantArt(a,vote, title,author));
						   
					   }
				   }
				   
			   }
			  
			  // System.out.println(image + " " + this.urls.size());
		   }catch(Exception e){
			   e.printStackTrace();
		   }
		   return result;
	   }
	public void rest(){
		this.urls = new ArrayList<>();
	}
	public void reset(){
		this.mapa = new HashMap<>();
		
		
	}
	// Base for links support
	class Link{
		protected String url;
		protected String photoUrl;
		protected String author;
		protected Integer vote;
		protected String title;
		
		protected Link(String url,Integer vote,String title, String author){
			this.url = url;
			this.vote = vote;
			this.title = title;
			this.author = author;
			System.out.println("tworze " +this.title);
		}
		public String[] getPhoto(){
			String[] s = new String[1];
			s[0] = this.url;
			return s;
		}
	}
	class ImgurPhoto extends Link{
		private String photoUrl;
		ImgurPhoto(String url,Integer vote,String title, String author){
			super(url,vote, title, author);
		}
		@Override
		public String[] getPhoto(){
			String[] result = new String[1];
			if(photoUrl != null)result[0] = this.photoUrl;
			else{
				
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
		private String[] photoUrl;
		ImgurAlbum(String url,Integer vote,String title, String author){
			super(url,vote, title, author);
		}
		@Override
		public String[] getPhoto(){
			if(photoUrl != null)return this.photoUrl;
			else{

				Elements e = doc.getElementsByAttributeValue("itemtype", "http://schema.org/ImageObject");
				Elements es = doc.getElementsByAttributeValue("itemtype", "http://schema.org/VideoObject");
				String[] result = new String[e.size() + es.size()];
				for (int i = 0; i<e.size();i++){
					result[i] = "https://i.imgur.com/" + e.get(i).attr("id") + ".jpg";
				}
				for (int j = 0; j<es.size();j++){
					result[j + e.size()] = "https://i.imgur.com/" + es.get(j).attr("id") + ".mp4";
				}
				this.photoUrl = result;
				return result;
			}
		}
	}
	class TumblrAlbum extends Link{
		private String[] photoUrl;
		TumblrAlbum(String url,Integer vote,String title, String author){
			super(url,vote, title, author);
		}
		@Override
		public String[] getPhoto(){
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
		private String photoUrl;
		GfycatGif(String url,Integer vote,String title, String author){
			super(url,vote, title, author);
		}
		@Override
		public String[] getPhoto(){
			String[] result = new String[1];
			if(photoUrl != null)result[0] = this.photoUrl;
			else{
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
		private String photoUrl;
		RedUpload(String url,Integer vote,String title, String author){
			super(url,vote, title, author);
		}
		@Override
		public String[] getPhoto(){
			
			this.photoUrl = url;
			String[] result = new String[1];
			result[0] = photoUrl;
			return result;
		}
	}
	class DeviantArt extends Link{
		private String photoUrl;
		DeviantArt(String url,Integer vote,String title, String author){
			super(url,vote, title, author);
		}
		@Override
		public String[] getPhoto(){
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
	class Giphy extends Link{
		private String photoUrl;
		Giphy(String url,Integer vote,String title, String author){
			super(url,vote, title, author);
		}
		@Override
		public String[] getPhoto(){
			String[] result = new String[1];
			Elements e = doc.getElementsByClass("gif");
			result[0] = e.get(0).attr("src");
			result[0] = "http:" + result[0];
			result[0] = result[0].substring(0, result[0].length()-3);
			result[0] += "mp4";
			photoUrl = result[0];
			return result;
		}
	}
}
