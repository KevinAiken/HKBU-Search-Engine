package edu.hkbu.comp4047;

public class Tester {
	public static void main(String[] args) {
		UrlTable u = new UrlTable(6);
		u.addNewUrl(0, "123", "ss", 20);
		u.addNewUrl(1, "2345", "fdfdf", 25);
		u.addNewUrl(2, "1234", "fdfdf", 14);
		u.addNewUrl(3, "2363", "34234", 12);
		u.addNewUrl(4, "6523", "234343", 8);
		u.addNewUrl(5, "25", "454y43", 4);
		u.getPrList()[0] = 1;
		u.getPrList()[1] = 1;
		u.getPrList()[2] = 1;
		u.getPrList()[3] = 1;
		u.getPrList()[4] = 1;
		u.getPrList()[5] = 1;
		u.assignPrScore(0, 1.3f);
		u.assignPrScore(1, 2f);
		System.out.println(u.urlInfo(0));
		int NResult = 5;
		
		InvertedIndex<String> i = new InvertedIndex<String>();
		i.add("word", 1, 3);
		i.add("b", 2, 4);
		i.add("word", 1, 4);
		i.add("b", 5, 3);
		i.add("word", 1, 6);
		
		System.out.println(i.termInfoToString("b"));
		System.out.println(i.termInfoToString("word"));
		
		String[] queryText = {"b", "word"};
		
		KeyWordsMatchingAlgo keyWordsMatching = new KeyWordsMatchingAlgo(NResult, u,i);
		ReturnedResult[] a = keyWordsMatching.query(queryText);
		for(ReturnedResult b : a) {
			System.out.println(b.toString());
		}
	}
}