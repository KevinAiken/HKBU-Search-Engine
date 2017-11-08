package edu.hkbu.comp4047;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/*
 * This table is used to store and retrieve information of urls, including page rank scores and lengths.
 * Each type of data are stored in a list separately, sharing the same index.
 * The index of each url is also used in the term info of the inverted index.
 * @param prList 		an array of float to store the page rank score of each url.
 * @param lenghList 	an array of short to store the length of text of each url;
 * @param urlList   	an array of String to store the url;
 * @param length 		an array of integer to store the number of words of each url.
 * @param titleList		an array of String to store the title.
 */
public class UrlTable implements java.io.Serializable {
	private String urlList[];
	private float prList[];
	private int lengthList[];
	private int length;
	private String titleList[];
	
	/* This is the only constructor of UrlTable
	*  @param length    the desired number of url
	*/
	public UrlTable(int length) {
		prList = new float[length];
		lengthList = new int[length];
		urlList = new String[length];
		titleList = new String[length];
		this.length = length;
	}
	//getter
	public float[] getPrList() {
		return prList;
	}
	//getter
	public int[] getLengthList() {
		return lengthList;
	}
	//getter
	public String[] getUrlList() {
		return urlList;
	}
	//getter
	public int getLength(){
		return length;
	}
	public String[] getTitleList() {
		return this.titleList;
	}
	//display the information of a specific url
	//@param i: the index of the desired url
	public String urlInfo(int i){
		return "Url: " + this.urlList[i] + "\nScore of PR:" + this.prList[i] + "\nLength: " + this.lengthList[i];
	}
	
	// store new urls in the table. Usually, the score of page ranking will be added later, 
	// so at first time, we only assign the value to the length, the address and the title
	// return false if fail to add a new url.
	public boolean addNewUrl(int index, String url, String title, int length) {
		if(index > this.length) 
			return false;
		this.urlList[index] = url;
		this.titleList[index] = title;
		this.lengthList[index] = length;
		return true;
	}
	//assign Page Ranke score to each url
	public boolean assignPrScore(int index, float score) {
		if(index > this.length) 
			return false;
		this.prList[index] = score;
		return true;
	}
	//fetch and reconstruct the URL object
	public static UrlTable deserialize(String fileName) {
		UrlTable temp = null;
		FileInputStream fileIn = null;
		ObjectInputStream in = null;
	      try {
	          fileIn = new FileInputStream(fileName);
	          in = new ObjectInputStream(fileIn);
	          temp = (UrlTable) in.readObject();
	       }catch(IOException i) {
	          i.printStackTrace();
	          return null;
	       }catch(ClassNotFoundException c) {
	          System.out.println("UrlTable class not found");
	          c.printStackTrace();
	          return null;
	       }
	      finally {
	          try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	          try {
				fileIn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      }
		
		return temp;
	}
}