package edu.hkbu.comp4047;
import java.util.ArrayList;
import java.util.stream.IntStream;

/* Here is the main logic to fulfill a keyword matching, the detailed illustration of the algorithm is written in the report.
*  @param prList            The reference of the list of pageRankScores stored in the UrlTable
*  @param index             The reference of the invertedIndex reconstructed by server.
*  @param urlTable          The reference of the UrlTable
*  @param frequencyTable    A matrix storing the required data only
*  @param nDoc              Number of documents in total
*  @param colLength         nDoc+1
*  @param queryLength       number of words in query
*  @param termInfo          detailed information of a specific word
*  @param normC             the overall number of words among the whole dataset
*  @param miu               a parameter in the algorithm
*  @param lengthList        a reference to store the length of each document
*  @param likelihood        to store the likelihood score of each url
*  @param lambda            a parameter in the algorithm
*  @param nResult           the desired number of result
*  @param result            the result about to be returned
*  @param sortedIndices     The index of the top rank urls
*/
public class KeyWordsMatchingAlgo {
	private float[] prList;
	private InvertedIndex<String> index;
	private UrlTable urlTable;
	private int[][] frequencyTable; 
	private int nDoc;
	private int colLength;
	private int queryLength;
	private InvertedIndex<String>.TermInfo termInfo;
	private float miu = 1500f;

	private int normC;
	private int[] lengthList;
	private float[] likelihood;
	private float lambda = 10;
	private int nResult;
	private ReturnedResult[] result;
	private int[] sortedIndices;
	
	//Constructor
	public KeyWordsMatchingAlgo(int nResult, UrlTable urlTable, InvertedIndex<String> invertedIndex) {
		this.index = invertedIndex;
		this.urlTable = urlTable;
		this.nDoc = urlTable.getLength();
		this.colLength = nDoc + 1;
		this.frequencyTable = new int[colLength][];
		this.lengthList = urlTable.getLengthList();
		this.likelihood = new float[nDoc];
		this.prList = urlTable.getPrList();
		this.nResult = nResult;
		for(int i: lengthList)
			normC += i; 
	}
	//The overall function that will be called to go through the ranking algorithm
	public ReturnedResult[] query(String[] terms) {
		if(!fetchData(terms))
			return null;
		calculateLikelihood();
		rankPage();
		return returnResult();
	}
	
	//rank the page by likelihood score
	private void rankPage() {
		sortedIndices = IntStream.range(0, this.likelihood.length)
                .boxed().sorted((i, j) -> ((Float)(-1 * (this.likelihood[i] + lambda * prList[i]))).compareTo((Float)(-1 * (lambda * prList[j] + this.likelihood[j]))))
                .mapToInt(ele -> ele).toArray();
		for(int i = 0; i < this.likelihood.length; i ++) {
			System.out.println(sortedIndices[i]);
			System.out.println(likelihood[sortedIndices[i]]);
		}
	}
	//calculate likelihood of each url
	private void calculateLikelihood() {
		int i,j = 0;
		double temp;
		for(i = 0; i < nDoc; i++){
			temp = 0;
			for(j = 0; j < queryLength; j ++) {
				temp = Math.log((this.frequencyTable[i][j] + this.miu * this.frequencyTable[this.nDoc][j] / this.normC) / (this.lengthList[i] + miu));
				System.out.println(temp);
			}
			this.likelihood[i] = (float)temp;
		}
	}
	//fetch desired data from invertedIndex
	private boolean fetchData(String[] terms) {
		queryLength = terms.length;
		int i,j = 0;
		for (i = 0; i < colLength; i ++) {
			this.frequencyTable[i] = new int[queryLength];
			for(j = 0; j < queryLength; j ++)
				this.frequencyTable[i][j] = 0;
		}
		for(i = 0; i < queryLength; i ++){
			this.termInfo = (InvertedIndex<String>.TermInfo) index.get(terms[i]);
			if(this.termInfo == null) return false;
			writeColumn(i, termInfo);
		}
		return true;
	}
	//reconstruct frequencyTable
	private void writeColumn(int i, InvertedIndex<String>.TermInfo termInfo){
		termInfo.forEach((a)->{
			frequencyTable[(int) a.get(0)][i] = a.size() - 1;});
		this.frequencyTable[nDoc][i] = termInfo.getOccurrence();
	}
	//wrap result
	public ReturnedResult[] returnResult() {
		if(nResult>lengthList.length)
			nResult=lengthList.length;
		ReturnedResult[] result = new ReturnedResult[nResult];
		for(int i = 0; i < nResult; i ++) {
			result[i] = new ReturnedResult(this.urlTable.getUrlList()[sortedIndices[i]], this.urlTable.getTitleList()[sortedIndices[i]]);
		}
		return result;
	}
}