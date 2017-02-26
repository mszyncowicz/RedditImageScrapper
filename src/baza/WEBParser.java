package baza;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;

public class WEBParser {

	public WEBParser() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		 try (final WebClient webClient = new WebClient()) {
			 webClient.getOptions().setCssEnabled(false);//if you don't need css
		        webClient.getOptions().setJavaScriptEnabled(false);
		        webClient.getOptions().setGeolocationEnabled(false);//if you don't need js
		        webClient.getOptions().setAppletEnabled(false);
		        webClient.getOptions().setActiveXNative(false);//if you don't need js
			 	Integer year = 2015;
		        // Get the first page
		        final HtmlPage page1 = webClient.getPage("https://www.ssa.gov/oact/babynames/");

		        // Get the form that we are dealing with and within that form, 
		        // find the submit button and the field that we want to change.
		        HtmlForm form = page1.getFormByName("popnames");

		        HtmlSubmitInput button = form.getInputByValue("  Go  ");
		        HtmlTextInput textField = form.getInputByName("year");
		        HtmlRadioButtonInput radioButton = (HtmlRadioButtonInput) page1.getElementById("number");
		        HtmlSelect select = (HtmlSelect) page1.getElementById("rank");
		        HtmlOption option = select.getOptionByValue("1000");
		        select.setSelectedAttribute(option, true);
		        radioButton.setChecked(true);
		        // Change the value of the text field
		        textField.setValueAttribute(year.toString());

		        // Now submit the form by clicking the button and get back the second page.
		        HtmlPage page2 = button.click();
		      //  System.out.println(page2.getUrl());
		       // System.out.println(page2.getWebResponse().getContentAsString());
		        URLParser parse = new URLParser(page2.getWebResponse().getContentAsString());
		        parse.parseTable();
		        
		        do{
		        	year -= 1;
		        	
		        	
		        	form = page2.getFormByName("popnames");
		        	textField = form.getInputByName("year");
		        	button = form.getInputByValue("   Go  ");
		        	textField.setValueAttribute(year.toString());
		        	page2 = button.click();
		        	String html = page2.getWebResponse().getContentAsString();
		        	parse.changeDoc(html);
		        	parse.parseTable();

		           // System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\\b\b\b\b\b");
		        	System.out.println(year);
		        }while(year>1956);
		        System.out.println(parse.mapa.size());
		        parse.toDB();
		    }catch(Exception e){
				e.printStackTrace();
			}

	}
}

