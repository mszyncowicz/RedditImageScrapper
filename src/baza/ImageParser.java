package baza;

import java.util.ArrayList;
import java.util.List;

import baza.RedditParser.DeviantArt;
import baza.RedditParser.GfycatGif;
import baza.RedditParser.ImgurAlbum;
import baza.RedditParser.ImgurPhoto;
import baza.RedditParser.Link;
import baza.RedditParser.RedUpload;
import baza.RedditParser.TumblrAlbum;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


public class ImageParser{
	WindowScreen app;
	ArrayList<Link> images;
	RedditParser parser;
	WebClient webClient;
	ImagePanel panel;
	HtmlPage page1;
	int pagenr = 0;
	String nextPage;
	String redditPage;
	String prevPage;
	final int max = 5;
	int current = 0;
	ImageParser(WindowScreen app){
		this.app = app;
		// TODO Auto-generated method stub
		 try {
			 	//this.app = app;
			 	
			 	
			 	this.webClient = new WebClient();
			 	webClient.getOptions().setCssEnabled(false);//if you don't need css
		        webClient.getOptions().setJavaScriptEnabled(false);
		        webClient.getOptions().setGeolocationEnabled(false);//if you don't need js
		        webClient.getOptions().setAppletEnabled(false);
		        webClient.getOptions().setActiveXNative(false);//if you don't need js
			 	parser = new RedditParser();
		        // Get the first page
		        		 
		       		        		        
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	}
	void setPage(String reddit){
		this.redditPage = "https://www.reddit.com/r/" + reddit;
	}
	void lookFor(){
		try {
			page1 = webClient
					.getPage("https://www.reddit.com/r/porn/"); //for +18 content <lenny_face>

			List<HtmlForm> forms = page1.getForms();
			HtmlForm form = forms.get(0);
			HtmlButton button = form.getButtonsByName("over18").get(1);
			page1 = button.click();
			page1 = webClient.getPage(this.redditPage);
			parser.changeDoc(page1.getWebResponse().getContentAsString());
			images = parser.parseImages();
			nextPage = parser.nextPage();
			System.out.println(nextPage + " " + page1.getUrl());
			pagenr++; //test
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	void lookForMore(){
		try {
			//parser.rest();
			pagenr++; //test
			this.prevPage = page1.getUrl().toString();
			page1 = webClient.getPage(this.nextPage);
			parser.changeDoc(page1.getWebResponse().getContentAsString());
			images = parser.parseImages();
			nextPage = parser.nextPage();
			System.out.println(nextPage + " " +current);
		} catch(NullPointerException x){}
		catch (Exception e) {
			if (prevPage.equals(nextPage)){
				
			}else{
				this.panel.reset();
				e.printStackTrace();
			}
		
			//lookForMore();
		}
	}
	void parseImages(){

		try {
			System.out.println("s" + current + " " + images.size());
			if (this.images.size() == 0) lookForMore();
			for (int i = current; i < images.size(); i++) {
				
				System.out.println(images.get(i).title + 	"pars					" + pagenr	+ "   cur:" + current	+ " " + images.size()	);
			if (this.panel.getView().getChildren().size() <max) {
						//System.out.println(images.get(i).url);
				String a = images.get(i).url;
				if (a.substring(a.length() - 8).contains(".jpg")
						|| a.substring(a.length() - 8).contains(".gif")
						|| a.contains(".png")) {
					//images.get(i).photoUrl = a;
					while(a.charAt(a.length()-1) != 'f' && a.charAt(a.length()-1) != 'g'){
						a = a.substring(0, a.length()-1);
						//current++;
						//continue;
					}
					this.panel.addAlbum(a,images.get(i));
					current++;
				} else if (images.get(i) instanceof ImgurPhoto || images.get(i) instanceof ImgurAlbum || images.get(i) instanceof TumblrAlbum || images.get(i) instanceof DeviantArt) {
					page1 = webClient.getPage(images.get(i).url);
					parser.changeDoc(page1.getWebResponse()
							.getContentAsString());
					System.out.println("przed "+images.get(i).title);
					
					panel.addAlbum(images.get(i).getPhoto(),images.get(i));
					current++;
					/*
					new Thread(new Runnable(){
						public void run(){
							
						}
					}).start();
					*/
				}else if (images.get(i) instanceof GfycatGif){
			
				
				//  Pattern gfycat = Pattern.compile("http[s]*:\\/\\/gfycat.com\\/([A-Za-z]+)");
				//  System.out.println(gfycat.matcher(images.get(i).url).groupCount());
				//  Matcher m = gfycat.matcher(images.get(i).url);
				  //if(m.find()){
//					  String photourl = "https://thumbs.gfycat.com/" +m.group(1) + "-size_restricted.gif";
					  //String photourl = "https://giant.gfycat.com/" +m.group(1) + ".gif";
					
					  //System.out.println(photourl);
					if(images.get(i).url.contains(".webm")){
						images.get(i).url = images.get(i).url.substring(0, images.get(i).url.length()-5);
					}
				  page1 = webClient.getPage(images.get(i).url);
					parser.changeDoc(page1.getWebResponse()
							.getContentAsString());
				 // images.get(i).getPhoto();
					if(images.get(i).getPhoto()!= null)panel.addAlbum(images.get(i).getPhoto()[0],images.get(i));
					 
				 // }
				  current++;
				
				}else if (images.get(i) instanceof RedUpload){
					panel.addAlbum(images.get(i).getPhoto(),images.get(i));
					current++;
				}
			} 
			else{
				System.out.println("koniec!");
				break;
			}
			}
			//current =0;
			if (current >= images.size()-1 && images.size() != 0){
				current = 0;
				parser.rest();
				reset();
			}
		}catch (Exception e) {
		
			//parseImages(h+1);
			e.printStackTrace();
			current++;
			parseImages();
		}
		
	}

	void rest(){
		//current = 0;
		parser.reset();
		this.panel.reset();
	}
	void reset(){
		images  = null;
		System.gc();
		this.lookForMore();
		this.parseImages();
	}
	void resetPanel(){
	//	this.panel.reset();
	//	this.panel = null;
		panel = new ImagePanel(this.app);
		System.gc();
	}
	void deleteAll(){
		this.resetPanel();
		parser.reset();
		parser = new RedditParser();
		images = new ArrayList<>();
		current = 0;
		nextPage = null;
		redditPage = null;
		pagenr = 0;
		this.webClient = new WebClient();
	 	webClient.getOptions().setCssEnabled(false);//if you don't need css
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setGeolocationEnabled(false);//if you don't need js
        webClient.getOptions().setAppletEnabled(false);
        webClient.getOptions().setActiveXNative(false);//if you don't need js
	
	}
	void wypisz(String[] album){
		for (String a : album){
			System.out.println("      " + a);
		}
	}
	void wypisz(String a){
		System.out.println("      " + a);
		
	}
	void setPanel(ImagePanel panel){
		this.panel = panel;
	}
	ImagePanel getPanel(){
		return this.panel;
	}
	
}
