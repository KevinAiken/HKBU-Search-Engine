package edu.hkbu.comp4047;

import javax.swing.JOptionPane;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Crawls the web based on user input and saves the results.
 * 
 * urlInfo contains the url of each page, the title of each page, the number of words in each page, 
 * and the PageRank of each page within the collection of crawled sites.
 * 
 * index contains every word within every document, which documents they are contained within, 
 * and their position within those documents. 
 * 
 */
public class Main {
	public static void main(String[] args) {

		String seed = JOptionPane.showInputDialog(null, "What website do you want to start with?");
		int urlPoolMax = Integer.parseInt(JOptionPane.showInputDialog(
				null, "What is the maximum number of urls you want to have in urlPool at one time"));
		int maxUrls = Integer.parseInt((JOptionPane.showInputDialog(null, "How many sites do you want to crawl?")));
		
		
		int docID = 0; 
		int i = 0;
		ArrayList<String> urlPool = new ArrayList<String>();
		ArrayList<String> processedUrlPool = new ArrayList<String>();
		UrlTable urlInfo = new UrlTable(maxUrls);
		InvertedIndex index = new InvertedIndex(); //Contains all words and their positions in documents
		
		if(seed.endsWith("/")) {
			seed = seed.substring(0, seed.length()-1);
		}
		urlPool.add(seed);
		
		double[][] pageMatrix = new double[maxUrls][maxUrls];
		ArrayList<ArrayList<String>> linkRelations = new ArrayList<ArrayList<String>>();
		ArrayList<String> linksList = new ArrayList<String>();
		List<String> wordList = new ArrayList<String>();
		
		// All crawling is done within this while loop
		while(urlPool.size() > 0 && processedUrlPool.size() < maxUrls) {
			
			// Removes empty links 
			while(urlPool.get(0).length() < 3) {
				urlPool.remove(0);
			}
			
			try {
				System.out.println("Attemping to connect to: " + urlPool.get(0));
				Document doc = Jsoup.connect(urlPool.get(0)).get();
				
				String documentText = doc.body().text();
				
				wordList = getWords(documentText);
				
				for(i = doc.title().length(); i<wordList.size(); i++) {
					index.add(wordList.get(i), docID, i);
				}
				
				if(doc.title().equals("")) {
					urlInfo.addNewUrl(docID, urlPool.get(0), urlPool.get(0), wordList.size());
				} else {
					urlInfo.addNewUrl(docID, urlPool.get(0), doc.title(), wordList.size());
				}
				
				Elements links = doc.select("a[href]");
				
				linksList.clear();
				for(i = 0; i < links.size(); i++) {
					
					String target = links.get(i).attr("abs:href").toString();
					
					while(target.endsWith("/") || target.endsWith("#")) {
						target = target.substring(0, target.length()-1);
					}
					
					linksList.add(target);
					if(!urlPool.contains(target) &&
							!processedUrlPool.contains(target) && 
							urlPool.size() < urlPoolMax) {
						
							urlPool.add(target);
						
					}
				}
				
				linkRelations.add(docID, (ArrayList<String>) linksList.clone());
				docID++;
				processedUrlPool.add(urlPool.get(0));
				urlPool.remove(0);
				
			} catch(IOException ioe) {
				System.out.println("Connection Error: " + ioe);
				urlPool.remove(0);
			}
			
		}

	    int j = 0;
	    for(i = 0; i < linkRelations.size(); i++) {
	    	//Repeat for each url's link array
	    	for(j = 0; j < linkRelations.get(i).size(); j++) {
	    		//Do for each link in the array
	    		if(processedUrlPool.contains(linkRelations.get(i).get(j))) {
	    			pageMatrix[processedUrlPool.indexOf(linkRelations.get(i).get(j))][i] = 1;
	    		}
	    	}
	    }
		
		// Prints the formatted pageMatrix
	    /*
		for ( i = 0; i < pageMatrix.length; i++) {
		    for (j = 0; j < pageMatrix[i].length; j++) {
		        System.out.print((int) pageMatrix[i][j] + " ");
		    }
		    System.out.println();
		}
		*/
		double[] pageRankScores = (edu.hkbu.comp4047.PageRank.getPRArray(pageMatrix));
		
		for(i = 0; i < pageRankScores.length; i++) {
			urlInfo.assignPrScore(i, (float) pageRankScores[i]);
		}
		
		writeToCSV("C:/4047/index.csv", index);
		writeToCSV("C:/4047/urlInfo.csv", urlInfo);
		
		if(new File("C:/4047").exists()) {
			serialize("C:/4047/hashmap.ser", index);
			serialize("C:/4047/urltable.ser", urlInfo);
		} else {
			boolean noValidPath = true;
			String savePath = null;
			while(noValidPath) {
				savePath = JOptionPane.showInputDialog(null, "Where should hashmap.ser and urtable.ser be saved?");
				if(new File(savePath).exists()) {
					noValidPath = false;
				}
			}
			serialize(savePath+"/hashmap.ser", index);
			serialize(savePath+"/urltable.ser", urlInfo);
		}
		
	}
	
	// Used to get individual words out of the web pages
	public static List<String> getWords(String text) {
	    List<String> words = new ArrayList<String>();
	    BreakIterator breakIterator = BreakIterator.getWordInstance();
	    breakIterator.setText(text);
	    int lastIndex = breakIterator.first();
	    while (BreakIterator.DONE != lastIndex) {
	        int firstIndex = lastIndex;
	        lastIndex = breakIterator.next();
	        if (lastIndex != BreakIterator.DONE && Character.isLetterOrDigit(text.charAt(firstIndex)) && !Character.isIdeographic(text.charAt(firstIndex))) {
	            words.add(text.substring(firstIndex, lastIndex));
	        }
	    }

	    return words;
	}
	
	public static void serialize(String path, Object data) {
		try
        {
			FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
            fos.close();
            System.out.println("Serialized " + data.getClass() +  " data is saved in " + path);
        } catch(IOException ioe)
         {
               ioe.printStackTrace();
         }
	}
	
	// Puts a urltable into an easily viewable format.
	public static void writeToCSV(String path, UrlTable data) {
		String eol = System.getProperty("line.separator");
		int i = 0;
		try (Writer writer = new FileWriter(path)) {
		  for (i = 0; i < data.getLength(); i++) {
		    writer.append(data.getUrlList()[i])
		          .append(',')
		          .append(data.getTitleList()[i])
		          .append(',')
		          .append((Float.toString(data.getPrList()[i])))
		          .append(',')
		          .append(Integer.toString(data.getLengthList()[i]))
		          .append(eol);
		  }
		} catch (IOException ex) {
		  ex.printStackTrace(System.err);
		}
	}
	
	// Puts an InvertedIndex into an easily viewable format.
	public static void writeToCSV(String path, InvertedIndex index) {
		String eol = System.getProperty("line.separator");
		int i = 0;
		int j = 0;
		
		Map<String, ArrayList<ArrayList<Integer>>> mymap = index;
		try(Writer writer = new FileWriter(path)) {
			for(Map.Entry<String, ArrayList<ArrayList<Integer>>> entry : mymap.entrySet()){
				writer.append(entry.getKey())
					  .append(',');
				for(i = 0; i < entry.getValue().size(); i++) {
					for(j = 0; j < entry.getValue().get(i).size(); j++) {
					  writer.append(Integer.toString(entry.getValue().get(i).get(j)))
					  		.append(',');
					}
					writer.append(" ")
					      .append(',');
				}
				writer.append(eol);
			}
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
	}
	
}
