package edu.hkbu.comp4047;

public class Test {
	public static void main(String[] args) {
		
		InvertedIndex<String> i;
		i = InvertedIndex.deserialize("C:/Users/Aiken/Downloads/comp4047project-20171104T144013Z-001/comp4047project/src/main/java/edu/hkbu/comp4047/hashmap.ser");
//		i.add("a", 1, 6);
//		i.add("a", 1, 9);
//		i.add("a", 1, 12);
//		i.add("a", 1, 15);
//		i.add("a", 2, 5); 
//		i.add("a", 2, 2);
//		i.add("a", 3, 5); 
//		i.add("a", 3, 2);
//		i.add("a", 4, 6);
//		i.add("a", 5, 6);
//
//		i.add("dog", 1, 10);
//		i.add("dog", 1, 16);
//		i.add("dog", 2, 18);
//		i.add("dog", 2, 6);
//		i.add("dog", 3, 18);
//		i.add("dog", 3, 6);
//		i.add("dog", 4, 7);
//
//		i.add("cat", 1, 3);
//		i.add("cat", 1, 7);	
//		i.add("cat", 1, 13);
//		i.add("cat", 2, 3);	
//		i.add("cat", 3, 3);	
//		i.add("cat", 3, 8);	
//		i.add("cat", 5, 7);	
//
//		i.add("and", 1, 8);
//		i.add("and", 1, 14);
//		i.add("and", 2, 4);
//		i.add("and", 3, 4);
//
//		i.add("barks", 4, 8);

		String keywords="\"Undergraduate Admissions\"";
		//System.out.println(i.get("Undergraduate Admissions").toString());
		PhraseMatch p = new PhraseMatch(keywords);
		
		System.out.println();
		UrlTable u;
		u = UrlTable.deserialize("C:/Users/Aiken/Downloads/comp4047project-20171104T144013Z-001/comp4047project/src/main/java/edu/hkbu/comp4047/urltable.ser");
		
//		u.addNewUrl(0, "https://www.google.com", "ss", 20);
//		u.addNewUrl(1, "https://www.yahoo.com", "fdfdf", 25);
//		u.addNewUrl(2, "https://www.comp.hkbu.edu.hk", "fdfdf", 14);
//		u.addNewUrl(3, "https://www.hkbu.edu.hk", "34234", 12);
//		u.addNewUrl(4, "https://www.hku.edu.hk", "234343", 8);
//		u.addNewUrl(5, "https://www.youtube.com", "454y43", 4);
//		
		int urlLimit=10;
		String[][] url=p.phraseSearch(i, u,urlLimit);
		if(url!=null) {
		for(int l=0;l<url.length;l++) {
			if(url[l]!=null)
			System.out.print(url[l][0]+"   ");
			System.out.print(url[l][1]+"   ");
			System.out.println();
		}
		
		}else {
			System.out.println("Wrong input!Result not found!");
		}
		
		
		
		}
}
