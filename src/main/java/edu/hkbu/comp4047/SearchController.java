package edu.hkbu.comp4047;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.assertj.core.util.URLs;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import groovyjarjarantlr.collections.List;
/**
 * This is a controller class that will get the data from data storage
 * based on user's query and return the URL lists back to user.
 *@author 
 *@version 1.0
 *@since 
 */
@Controller
public class SearchController {
	@RequestMapping("/keyword")
	public String greeting(@RequestParam String keywords, Model model) {
		InvertedIndex<String> i;
		
		/*i = InvertedIndex.deserialize(
				"C:/Users/Aiken/Downloads/comp4047project-20171104T144013Z-001/comp4047project/src/main/java/edu/hkbu/comp4047/hashmap.ser");
		*/
		i = InvertedIndex.deserialize("C:/4047/hashmap.ser");
		
		UrlTable u;
		//u = UrlTable.deserialize("C:/Users/Aiken/Downloads/comp4047project-20171104T144013Z-001/comp4047project/src/main/java/edu/hkbu/comp4047/urltable.ser");
		u = UrlTable.deserialize("C:/4047/urltable.ser");
		int urlLimit=300;
		String[] input = keywords.split(" ");
		String[] url=new String[urlLimit];;
		String[] URL = new String[urlLimit];
		String[] title = new String[urlLimit];
		if(!keywords.contains("\"")) {
	
			KeyWordsMatchingAlgo keyWordsMatching = new KeyWordsMatchingAlgo(urlLimit,u,i);
			ReturnedResult[] returnedResult = keyWordsMatching.query(input);
			if(returnedResult != null) {
			for(int x = 0; x< returnedResult.length; x ++) { 
				URL[x] = returnedResult[x].getUrl();
				title[x]=returnedResult[x].getTitle();
				url[x] = URL[x];
			}
			}else {
				title[0] = "Result not found!";
			}
			
		}else if(input.length>1 && input[0].contains("\"")&&input[input.length-1].contains("\"")){
			PhraseMatch p = new PhraseMatch(keywords);
			String[][] urlList =p.phraseSearch(i, u,urlLimit);
			if(urlList != null) {
			for(int a=0;a<urlList.length;a++) {
			URL[a] = urlList[a][0];
			title[a] = urlList[a][1];
			url[a] = URL[a];
			System.out.println(url[a]);
			}
			}else {
				title[0] = "Result not found!";
				}
			if(url[0]==null) {
				title[0]="Result not found!";
			}
			
		}else {
			title[0] = "Unexpected input format, please try again.";
		}
		
		
		
		model.addAttribute("URLs",url);
		model.addAttribute("Titles",title);
		model.addAttribute("query", keywords);
		return "result";
	}
}
