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
	public WindowScreen app;
	private ArrayList<Link> images;
	private RedditParser parser;
	private WebClient webClient;
	private ImagePanel panel;
	private HtmlPage page1;
	private int pagenr = 0;
	public String nextPage;
	private String redditPage;
	public String prevPage;
	private boolean stop = false;
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
	public void setPage(String reddit){
		this.redditPage = "https://www.reddit.com/r/" + reddit;
	}
	public void setAuthor(String author){
		this.redditPage = "https://www.reddit.com/user/" + author;
		System.out.println(author);
	}
	public void lookFor(){
		try {
			page1 = webClient
					.getPage("https://www.reddit.com/r/nsfw/"); //for +18 content <lenny_face>

			List<HtmlForm> forms = page1.getForms();
			HtmlForm form = forms.get(0);
			HtmlButton button = form.getButtonsByName("over18").get(1);
			page1 = button.click();
			page1 = webClient.getPage(this.redditPage);
			parser.changeDoc(page1.getWebResponse().getContentAsString(),false);
			images = parser.parseImages();
			nextPage = parser.nextPage();
			System.out.println(nextPage + " " + page1.getUrl());
			pagenr++; //test
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void lookForMore(){
		try {
			//parser.rest();
			pagenr++; //test
			this.prevPage = page1.getUrl().toString();
			page1 = webClient.getPage(this.nextPage);
			parser.changeDoc(page1.getWebResponse().getContentAsString(),false);
			images = parser.parseImages();
			try{
				nextPage = parser.nextPage();
			}catch(Exception e){
				stop = true;
			}
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
	public void parseImages(){

		try {
			try{
			System.out.println("s" + current + " " + images.size());
			}catch (NullPointerException e){
				stop = true;
			}
			if (this.images.size() == 0) lookForMore();
			for (int i = current; i < images.size(); i++) {
				
				System.out.println(images.get(i).title + 	"pars					" + pagenr	+ "   cur:" + current	+ " " + images.size()	);
			if (this.panel.getView().getChildren().size() <max) {
				String a = images.get(i).url;
				if (a.substring(a.length() - 8).contains(".jpg")
						|| (a.substring(a.length() - 8).contains(".gif") && !a.contains("giphy"))
						|| a.contains(".png")) {
	
					while(a.charAt(a.length()-1) != 'f' && a.charAt(a.length()-1) != 'g' && a.charAt(a.length()-1) != 'm'){
						a = a.substring(0, a.length()-1);
					}
					this.panel.addAlbum(a,images.get(i));
					current++;
				} else if (images.get(i) instanceof ImgurPhoto || images.get(i) instanceof ImgurAlbum || images.get(i) instanceof TumblrAlbum || images.get(i) instanceof DeviantArt) {

					parser.changeDoc(images.get(i).url,true);
					System.out.println("przed "+images.get(i).title);
					
					panel.addAlbum(images.get(i).getPhoto(),images.get(i));
					current++;
					
				}else if (images.get(i) instanceof GfycatGif){
			
		
					if(images.get(i).url.contains(".webm")){
						images.get(i).url = images.get(i).url.substring(0, images.get(i).url.length()-5);
					}
			
					parser.changeDoc(images.get(i).url,true);
					if(images.get(i).getPhoto()!= null)panel.addAlbum(images.get(i).getPhoto()[0],images.get(i));
					 
				 // }
				  current++;
				
				}else if (images.get(i) instanceof RedUpload){
					panel.addAlbum(images.get(i).getPhoto(),images.get(i));
					current++;
				}else if (images.get(i) instanceof Giphy){
						parser.changeDoc(images.get(i).url, true);
						panel.addAlbum(images.get(i).getPhoto(), images.get(i));
					}
			} 
			else{
				System.out.println("koniec!");
				break;
			}
			}

			if (current >= images.size()-1 && images.size() != 0){
				current = 0;
				parser.rest();
				reset();
			}
		}catch (Exception e) {
		

			e.printStackTrace();
			current++;
			if (!stop)parseImages();
		}
		
	}

	public void rest(){

		parser.reset();
		this.panel.reset();
	}
	public void reset(){
		images  = null;
		System.gc();
		this.lookForMore();
		if(!stop)this.parseImages();
	}
	public void resetPanel(){
		panel = new ImagePanel(this.app);
		System.gc();
	}
	public void deleteAll(){
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
        // I dont know why but github is crazy here with indentations
        webClient.getOptions().setAppletEnabled(false);
        webClient.getOptions().setActiveXNative(false);//if you don't need js
	
	}
	private void wypisz(String[] album){
		for (String a : album){
			System.out.println("      " + a);
		}
	}
	private void wypisz(String a){
		System.out.println("      " + a);
		
	}
	public void setPanel(ImagePanel panel){
		this.panel = panel;
	}
	public ImagePanel getPanel(){
		return this.panel;
	}
	
}
