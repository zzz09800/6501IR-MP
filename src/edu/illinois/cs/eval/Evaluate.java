package edu.illinois.cs.eval;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import edu.illinois.cs.index.ResultDoc;
import edu.illinois.cs.index.Runner;
import edu.illinois.cs.index.Searcher;

public class Evaluate {
	/**
	 * Format for judgements.txt is:
	 * <p>
	 * line 0: <query 1 text> line 1: <space-delimited list of relevant URLs>
	 * line 2: <query 2 text> line 3: <space-delimited list of relevant URLs>
	 * ...
	 * Please keep all these constants!
	 */

	private static final String _judgeFile = "npl-judgements.txt";
	final static String _indexPath = "lucene-npl-index";
	static Searcher _searcher = null;

	//Please implement P@K, MRR and NDCG accordingly
	public static void main(String[] args) throws IOException {
		String method = "--bdp";//specify the ranker you want to test

		_searcher = new Searcher(_indexPath);
		Runner.setSimilarity(_searcher, method);
		BufferedReader br = new BufferedReader(new FileReader(_judgeFile));
		String line = null, judgement = null;
		int k = 10;
		double meanAvgPrec = 0.0, p_k = 0.0, mRR = 0.0, nDCG = 0.0;
		double numQueries = 0.0;
		while ((line = br.readLine()) != null) {
			judgement = br.readLine();

			//compute corresponding AP
			meanAvgPrec += AvgPrec(line, judgement);
			//compute corresponding P@K
			p_k += Prec(line, judgement, k);
			//compute corresponding MRR
			mRR += RR(line, judgement);
			//compute corresponding NDCG
			nDCG += NDCG(line, judgement, k);

			++numQueries;
		}
		br.close();

		System.out.println("\nMAP: " + meanAvgPrec / numQueries);//this is the final MAP performance of your selected ranker
		System.out.println("\nP@" + k + ": " + p_k / numQueries);//this is the final P@K performance of your selected ranker
		System.out.println("\nMRR: " + mRR / numQueries);//this is the final MRR performance of your selected ranker
		System.out.println("\nNDCG: " + nDCG / numQueries); //this is the final NDCG performance of your selected ranker
	}

	private static double AvgPrec(String query, String docString) {
		ArrayList<ResultDoc> results = _searcher.search(query).getDocs();
		if (results.size() == 0)
			return 0; // no result returned

		HashSet<String> relDocs = new HashSet<String>(Arrays.asList(docString.split(" ")));
		int i = 1;
		double avgp = 0.0;
		double numRel = 0;
		System.out.println("\nQuery: " + query);
		for (ResultDoc rdoc : results) {
			if (relDocs.contains(rdoc.title())) {
				//how to accumulate average precision (avgp) when we encounter a relevant document
				numRel++;
				avgp=avgp+(numRel/i);
				System.out.print("  ");
			} else {
				//how to accumulate average precision (avgp) when we encounter an irrelevant document
				System.out.print("X ");
			}
			System.out.println(i + ". " + rdoc.title());
			i++;
		}

		//compute average precision here
		// avgp = ?
		if(numRel!=0)
			avgp=avgp/(i-1);
		else
			avgp=0;
		System.out.println("Average Precision: " + avgp);
		return avgp;
	}

	//precision at K
	private static double Prec(String query, String docString, int k) {
		double p_k = 0;
		//your code for computing precision at K here

		int i=1;
		double numRel = 0;
		ArrayList<ResultDoc> results = _searcher.search(query).getDocs();
		if (results.size() == 0)
			return 0; // no result returned

		HashSet<String> relDocs = new HashSet<String>(Arrays.asList(docString.split(" ")));
		System.out.println("\nQuery: " + query);
		for (ResultDoc rdoc : results) {
			if (relDocs.contains(rdoc.title())) {
				//how to accumulate average precision (avgp) when we encounter a relevant document
				numRel++;
				System.out.print("  ");
			} else {
				//how to accumulate average precision (avgp) when we encounter an irrelevant document
				System.out.print("X ");
			}
			System.out.println(i + ". " + rdoc.title());
			++i;
			if((i-1)==k)
				break;
		}

		p_k=numRel/(i-1);
		System.out.println("Precision@K: " + p_k);

		return p_k;
	}

	//Reciprocal Rank
	private static double RR(String query, String docString) {
		double rr = 0;
		//your code for computing Reciprocal Rank here

		int location=0;
		int i=1;
		int foundflag=0;
		ArrayList<ResultDoc> results = _searcher.search(query).getDocs();
		if (results.size() == 0)
			return 0; // no result returned

		HashSet<String> relDocs = new HashSet<String>(Arrays.asList(docString.split(" ")));
		System.out.println("\nQuery: " + query);
		for (ResultDoc rdoc : results) {
			if (relDocs.contains(rdoc.title())) {
				//how to accumulate average precision (avgp) when we encounter a relevant document
				if(foundflag==0) {
					location=i;
					foundflag=1;
				}
				System.out.print("  ");
			} else {
				//how to accumulate average precision (avgp) when we encounter an irrelevant document
				System.out.print("X ");
			}
			System.out.println(i + ". " + rdoc.title());
			++i;
		}

		if(location!=0)
			rr=1/location;
		else
			rr=0;

		return rr;
	}

	//Normalized Discounted Cumulative Gain
	private static double NDCG(String query, String docString, int k) {
		double ndcg = 0;
		double idcg = 0;
		//your code for computing Normalized Discounted Cumulative Gain here

		int i=1;
		ArrayList<ResultDoc> results = _searcher.search(query).getDocs();
		if (results.size() == 0)
			return 0; // no result returned

		HashSet<String> relDocs = new HashSet<String>(Arrays.asList(docString.split(" ")));
		System.out.println("\nQuery: " + query);
		for (ResultDoc rdoc : results) {
			if (relDocs.contains(rdoc.title())) {
				//how to accumulate average precision (avgp) when we encounter a relevant document
				ndcg=ndcg+(Math.pow(2,1)-1)/(Math.log(1+i)/Math.log(2));
				idcg=idcg+(Math.pow(2,1)-1)/(Math.log(1+i)/Math.log(2));
				System.out.print("  ");
			} else {
				//how to accumulate average precision (avgp) when we encounter an irrelevant document
				idcg=idcg+(Math.pow(2,1)-1)/(Math.log(1+i)/Math.log(2));
				System.out.print("X ");
			}
			System.out.println(i + ". " + rdoc.title());
			++i;
			if((i-1)==k)
				break;
		}


		return ndcg/idcg;
	}
}