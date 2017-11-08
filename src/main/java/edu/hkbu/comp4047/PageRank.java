package edu.hkbu.comp4047;

import java.util.Arrays;

/*
 * Takes in a web matrix in the following format 
 * and returns the dominant Eigenvalues, representing page importance.
 * 
 * Based on Larry Page And Sergey Brins' PageRank Algorithm.
 * However does not make use of the power method to calculate the pageRank values
 * 
 * Example Format:
 * pageMatrix[][] =
 * 		{1, 0, 1, 0},
 * 		{1, 0, 0, 1},
 * 		{1, 0, 1, 1},
 * 		{0, 1, 1, 1},
 * 
 * pageMatrix[0][2] contains a 1, meaning the URL with the index 2
 * contains a link to the URL with the index 1. In the diagram this position
 * is the first row and third column.
 * 
 * 
 */
public class PageRank {
	public static double[] getPRArray(double[][] input) {		
		double[][] adjacentMatrix = input; // Adjacency matrix is input web matrix
		double[][] transitionMatrix = calcTransitionMatrix(adjacentMatrix); // represents transition likelihood between sites
		double [][] googleMatrix = calcGoogleMatrix(transitionMatrix); // Simplifies the final calculation
		double[] PageRanks = calcEigenvalues(googleMatrix);
		
		return PageRanks;
	}
	
	// Calculates transition matrix; the likelihood of the transition between each page
	// Similar to a Markov chain
	private static double[][] calcTransitionMatrix(double[][] input) {
		int i, j;
		double[][] adjacentMatrix = input.clone();
		double[][] transitionMatrix = new double[adjacentMatrix.length][adjacentMatrix[0].length];
		// iterate through columns
		double columnsum;
		for(j = 0; j < transitionMatrix[0].length; j++) {
			// get column sum then divide each non-zero entry by the column sum	
			columnsum = 0;
			
			for(i = 0; i < transitionMatrix.length; i++) {
				columnsum += adjacentMatrix[i][j];
			}
			
			for(i=0; i < transitionMatrix.length; i++) {
				if(columnsum != 0) {
					transitionMatrix[i][j] = (adjacentMatrix[i][j])/columnsum;
				}
				else {
					transitionMatrix[i][j] = 1.0/transitionMatrix.length;
				}
			}
		}
		return transitionMatrix;
	}
	
	// From the transition matrix this creates a matrix that makes 
	// calculating the dominant Eigenvalues easier.
	// Takes care of dangling nodes and provides a damping factor of .85
	private static double[][] calcGoogleMatrix(double[][] input){
		double[][] googleMatrix = input.clone();
		int i = 0;
		int j = 0;
		for(i = 0; i < googleMatrix.length; i++) {
			for(j=0; j<googleMatrix.length; j++) {
				googleMatrix[i][j] = (googleMatrix[i][j]*.85) + (.15*(1.0/googleMatrix.length));
			}
		}
		return googleMatrix;
	}
	
	// Uses matrix multiplication to find eigenvalues
	private static double[] calcEigenvalues(double[][] input) {
		int i = 0;
		int j = 0;
		
		double[][] S1 = input;
		double[] initialVector = new double[S1.length];
		double[] lastEV = new double[S1.length];
		double[] Eigenvalues = new double[S1.length];
		
		Arrays.fill(initialVector, (1.00/S1.length));
		
		boolean notConverged = true;
		
		// Repeat calculation until the eigenvalues converge
		while(notConverged) {
			lastEV = Eigenvalues.clone();
			Arrays.fill(Eigenvalues, 0.00);
			for(i = 0; i < S1.length; i++) {
				for(j = 0; j < S1.length; j++){
					Eigenvalues[i] += initialVector[j]*S1[i][j];
				}
			}
			
			
			for(i = 0; i < S1.length; i++) {
				if(Math.abs(lastEV[i] - Eigenvalues[i]) > .01) {
					notConverged = true;
					break;
				}
				else {
					notConverged = false;
				}
			}
			initialVector = Eigenvalues.clone();
		}
		return Eigenvalues;
	}
}
