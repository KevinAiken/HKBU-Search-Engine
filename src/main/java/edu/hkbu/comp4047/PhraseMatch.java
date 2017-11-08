package edu.hkbu.comp4047;
import java.util.ArrayList;

import java.util.HashMap;

import edu.hkbu.comp4047.InvertedIndex.TermInfo;
import javassist.compiler.ast.Keyword;
import java.util.regex.*;
/**
 * The phraseMatch class implements an algorithm for matching phrase 
 * with the corresponding document and ranked the output URL lists 
 * according to the frequency of the phrase.
 * 
 * @author LIU Leyu
 * @version 1.0
 * @since 2017-10-26
 */
public class PhraseMatch {

	private String[] keyword;
	private int urlSize;
	private int[][] info;
	private int[] index;
	private int[] freq;
	private int[][] urlID;
	private String[][] url;
	
/**
 * This is the constructor of the PhraseMatch class
 * @param input
 */
	public PhraseMatch(String input) {
		phraseSplit(input);

	}

	/**
	 * split the phrase to keywords
	 * @param input This is the searching query 
	 * @return String[] This returns an array "keyword" of the splitted phrase
	 */
	public  String[] phraseSplit(String input) {

		String str=input;
		
		if(input.contains("\"")) {
			str=input.substring(1,input.length()-1);
		}
		System.out.println(str);
		keyword=str.split(" ");
		
//		for(int i=0;i<keyword.length;i++)
//			keyword[i]=keyword[i].toLowerCase();
		return keyword;
	}
	/**
	 * Search for the document ID and positions related to the phrase to get the final URL list
	 * @param i This is the invertedIndex that is used to store the information of document ID and positions
	 * @return String[][] This returns an array "url" of the final URL list and the titles
	 */

	public String[][] phraseSearch(InvertedIndex i,UrlTable u,int urlLimit) {
	
		urlSize=u.getLength();
		
		if(!i.isEmpty()) {
			
			int j=0;
			int size=0;
			int k=0;
			int s1=0;
			int s2=0;
			int s=0;
			int num=0;
			//get the documentID and word positions from the data storage
			
			while(size<keyword.length) {
				//test
				System.out.println(keyword[size]);
			//System.out.println(i.termInfoToString("Baptist"));
			if(i.containsKey(keyword[size])) {
			s = s + ((ArrayList<ArrayList>)i.get(keyword[size])).size();
			size++;
			}else {
				System.out.println("InvertedIndex does not contain the keyword!");
				return null;
			}
			}
			
			info = new int[s][];
			while(j<keyword.length) {
				if(i.containsKey(keyword[j])) {
					s1=((ArrayList<ArrayList>)i.get(keyword[j])).size();
					for(int c=0; c<s1;c++) {
						s2=((ArrayList<ArrayList>)i.get(keyword[j])).get(c).size();

						info[num+c] = new int[s2];
						for(int b=0;b<s2;b++) {
							info[num+c][b]=-1;
							
						}
						for(int d=0; d<s2; d++) {

							info[num+c][d]=(int) (((ArrayList<ArrayList>) i.get(keyword[j])).get(c)).get(d);

						}
					}	
					num=num+s1;
				}
				j++;
			}
			int[] temp1 = getIndex(info);
			int[] temp2 = docMatch(temp1);
			int[][] temp3 = posMatch(info,temp1,temp2);
			String[][] URL = getURL(temp3,u,urlLimit);
			return URL;
		}else {
			System.out.println("InvertedIndex is empty!");
			return null;
		}



	}
	/**get the first appeared index of each word 
	 * @param info 
	 * @return int[] This returns a array "index" that stores the first index of each word 
	 */
	public int[] getIndex(int[][] info) {
		index = new int[keyword.length];
		for(int a=0;a<index.length;a++)
			index[a]=-1;

		index[0]=0;
		int c=1;
		for(int i=1;i<=info.length-1;i++) {
			if( info[i][0]>=0 && info[i][0]<=info[i-1][0]) {
				index[c]=i;
				c++;
				if(c>keyword.length-1 )
					break;

			}
		}
		return index;
	}
	/**find the document ID that contain the phrase 
	 * @param info This is the array that stores the document information
	 * @param index This is the array that stores the first index of each word
	 * @return int[] This returns an array "id" that stores the ID of documents that contain all the keywords
	 */
	public int[] docMatch(int[] index) {
		int sizeInfo=0;
		for(int a=0;a<info.length;a++)
			if(info[a][0]>=0)
				sizeInfo++;
		int[] id = new int[sizeInfo];
		for(int a=0;a<id.length;a++)
			id[a]=-1;

		int size=0;
		for(int a=0;a<index.length;a++)
			if(index[a]>=0)
				size++;

		int d=0;
		if(size>1) {
		for(int k=0;k<index[1];k++) {
			int count=0;
			int j=1;
			while(j<size-1) {

				for(int w=index[j];w<index[j+1];w++) {

					if(info[w][0]==info[k][0]) {
						count++;
					}
				}
				j++;
			}
			if(j==size-1) {
				for(int w=index[j];w<sizeInfo;w++) {

					if(info[w][0]==info[k][0]) {
						count++;
					}
				}
			}
			if(count==size-1) {
				id[d]=info[k][0];
				d++;
			}
		}
		return id;
		}else {
			System.out.println("Keywords are less than 2!");
			return null;
		}
		
	}

	/**compare the word positions for all the keywords appear in the same document
	 * @param info This is the array that stores the document information
	 * @param index This is the array that stores the first index of each word
	 * @param id This is an array that stores the ID of documents that contain all the keywords
	 * @return int[][] This returns a 2D array of URL IDs ranked by the frequency of the phrase
	 */
	public int[][] posMatch(int[][] info,int[] index, int[] id) {
		freq=new int[urlSize];
		urlID = new int[freq.length][urlSize];
		if(id!=null) {
		int size=0;
		for(int a=0;a<id.length;a++)
			if(id[a]>=0)
				size++;

		for(int u=0;u<urlID.length;u++) {
			for(int r=0;r<urlID.length;r++) {
				urlID[u][r]=-1;
			}
		}
		for(int fr=0;fr<urlID.length;fr++) {
			freq[fr]=-1;
		}
		int[][] pos= new int[keyword.length*size][];
		

		int indexSize=0;
		for(int a=0;a<index.length;a++)
			if(index[a]>=0)
				indexSize++;
		int sizeInfo=0;
		for(int a=0;a<info.length;a++)
			if(info[a][0]>=0)
				sizeInfo++;
		//get the keyword positions that are in the same document
		int p=0;
		int c=0;
		for(int k=0;k<size;k++) {
			c=index[0];
			while(c<sizeInfo) {

				if(info[c][0]==id[k]) {
					pos[p]=new int[info[c].length];
						for(int b=0;b<pos[p].length;b++) {
							pos[p][b]=-1;
						}
					pos[p]=info[c];
//					//test
//					for(int b=0;b<pos[p].length;b++) {
//					System.out.print(pos[p][b]+" ");
//					}
//					System.out.println();
//					//
					p++;
				}
				c++;
			}
		}
		//compare the positions to check whether the phrase is in the document and count the frequency
		int e=1;
		int d=0;
		int doc=0;
		int start=0;

		while( start<p && pos[start][0]==id[doc] ) {
			while(e<pos[start].length) {
				int x=1;
				while(d+1<start+indexSize && x<pos[d+1].length ) {
					if(pos[d+1][x]==pos[start][e]+(d-start)+1) {
						d++;
						x=1;

					}else {
						x++;
					}
					if(d==start+indexSize-1) {
						freq[id[doc]]++;
					}
				}
				d=start;
				e++;
			}
			e=1;
			start=start+indexSize;
			d=start;

			doc++;
		}
		//get the list of URL id with the frequency of the phrase 


		for(int f=0;f<freq.length;f++) {
			if(freq[f]>=0) {
				int s=0;
				while(urlID[freq[f]][s]>0) {
					s++;
				}
				urlID[freq[f]][s]=f;
			}
		}
		
		return urlID;
		}else {
		
			return null;
		}
		
	}

	/**get URL from URL table, display the URLs ranked by frequency
	 * @param urlID This is a 2D array of URL IDs ranked by the frequency of the phrase
	 * @param u This is the URL table that stores all the URL information
	 * @return String[] This returns the URL list ranked in order
	 */
	public String[][] getURL(int[][] urlID, UrlTable u,int urlLimit) {
		url = new String[urlSize][2];	
		//get URL list from URL table
		String[] urlList= u.getUrlList();
		String[] urlTitle=u.getTitleList();
		int num=0;
		if(urlID!=null) {
		for(int i=urlSize-1;i>=0;i--) {
			for(int j=urlSize-1;j>=0;j--) {
				for(int uID=0;uID<urlList.length;uID++) {
					if(urlID[i][j] >=0 && urlID[i][j]==uID) {
						url[num][0]=urlList[uID];
						url[num][1]=urlTitle[uID];
						num++;
					}
				}
			}
		}
		}else {
			url=null;
		}
		String[][] url2=new String[Math.min(urlLimit,num)][2];
		
//			//test
//		System.out.println(num);
		
		if(url!=null) {
			if(num>urlLimit) {
				for(int l=0;l<urlLimit;l++) {
					url2[l]=url[l];
				}
				
			}else {
				for(int l=0;l<num;l++) {
					url2[l]=url[l];
				}
				
			}
			return url2;
		}else {
			return null;
		}
	}

}



