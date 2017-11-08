package edu.hkbu.comp4047;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
/*
 * This is a customized HashMap to store the invertedIndex.
 * The format of this is <String, ArrayList<ArrayList<Integer>>>. 
 * The values are wrapped in a new class called TermInfo.
 * User can retrieve the values as in normal hashmap.
 * When add values into hashmap, user need to call add(String term, int docID, int position) only,
 * The class will handle the rest of the process.
 * When retrieval from invertedIndex, user can access TermInfo as accessing arrayList
 */
public class InvertedIndex<String> extends HashMap implements Serializable{
	//constructor
	private int[] sortedIndices;
	public <String, TermInfo>InvertedIndex() {
		super();
	}
	//a static function to deserialize an instance of InvertedIndex
	public static InvertedIndex deserialize(java.lang.String fileName) {
		InvertedIndex temp = null;
		FileInputStream fileIn = null;
		ObjectInputStream in = null;
	      try {
	          fileIn = new FileInputStream(fileName);
	          in = new ObjectInputStream(fileIn);
	          temp = (InvertedIndex) in.readObject();
	       }catch(IOException i) {
	          i.printStackTrace();
	          return null;
	       }catch(ClassNotFoundException c) {
	          System.out.println("InvertedIndex class not found");
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
	
	//add values into InvertedIndex
	public void add(String term, int docID, int position) {
		if(!this.containsKey(term))
			this.put(term, new TermInfo(docID, position));
		else
			updateTerm(term, docID, position);
	}
	
	//private function to assist the add function
	private void updateTerm(String term, int docID, int position) {
		((TermInfo) this.get(term)).updateTerm(docID, position);
	}
	
	// to display the occurences of a specific term
	public String termInfoToString(String term) {
		if(this.containsKey(term))
			return (String) ("The occurences of " + term + " is:\n" + ((TermInfo) this.get(term)).toString());
		else
			return (String) "Term not found!";
	}
	
	
	/*A wrapper to encapsulate the information about occurrences.
	 *User will have no way to access the details of this class.
	 *
	 *@param pointer            The current arraylist being operated
	 *@param processing         This hashmap is aimed to find the array of desired doc in O(1) time. Only useful when there are multiple threads
	 *@param occurence          Literarily the numeber of appearances
	 *@param numOfDocContainIt  How many documents that contain this term. Useful for keyword matching
	 *
	 */
	public class TermInfo extends ArrayList<ArrayList> implements Serializable, Comparable<TermInfo>{
		private ArrayList<Integer> pointer;
		private HashMap<Integer,Integer> processing;
		private int occurrence = 1;
		private int numOfDocContainIt = 1;
		// the only constructor. The parameter is literarily the same as its name
		public <Integer>TermInfo(int docID, int position) {
			super();
			processing = new <Integer, Integer>HashMap();
			createNewDoc(docID, position);
		}
		//create a new arraylist to store the occurence of the desired docID
		private void createNewDoc(int docID, int position) {
			pointer = new ArrayList<Integer>();
			pointer.add(docID);
			pointer.add(position);
			this.add(pointer);
			processing.put(docID, this.size()-1);
			this.numOfDocContainIt++;
		}
		//update the arraylist of given docID
		public void updateTerm(int docID, int position) {
			occurrence ++;
			if(processing.containsKey(docID))
				this.get((int)processing.get(docID)).add(position);
			else
				this.createNewDoc(docID, position);
		}
		//get the occurrence, can only be called by the outer class
		public int getOccurrence() {
			return this.occurrence;
			//return 0;
		}
		//to String
		public java.lang.String toString(){
			StringBuilder sb = new StringBuilder();
			for (ArrayList list : this) {
				sb.append("[");
				for (Object i : list)
					sb.append(i.toString() + ",");
				sb.deleteCharAt(sb.length()-1);
				sb.append("]\n");
			}
			return sb.toString();
		}
        //compare to, for ranking
		public int compareTo(InvertedIndex<String>.TermInfo o) {
			if(this.occurrence < o.occurrence)
				return -1;
			else if(this.occurrence > o.occurrence)
				return 1;
			return 0;
		}
	}
}



