package edu.illinois.cs.eval;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import edu.illinois.cs.index.Indexer;
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

	final static String _dataset = "npl";
	final static String _prefix = "data/";
	final static String _file = "npl.txt";

	//Please implement P@K, MRR and NDCG accordingly
	public static void main(String[] args) throws IOException {
		String method = "--ok";//specify the ranker you want to test

		Indexer.index(_indexPath, _prefix, _file);

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
		relDocs.remove("");
		int i = 1;
		double avgp = 0.0;
		double numRel = 0;
		System.out.println("\nQuery: " + query);
		for (ResultDoc rdoc : results) {
			if (relDocs.contains(rdoc.title())) {
				//how to accumulate average precision (avgp) when we encounter a relevant document
				numRel++;
				avgp=avgp+(numRel/i);
				if(i<11)
					System.out.print("  ");
			} else {
				//how to accumulate average precision (avgp) when we encounter an irrelevant document
				if(i<11)
					System.out.print("X ");
			}
			if(i<11)
				System.out.println(i + ". " + rdoc.title());
			i++;
		}

		//compute average precision here
		// avgp = ?
		if(numRel!=0)
			avgp=avgp/relDocs.size();
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
		relDocs.remove("");
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
		relDocs.remove("");
		System.out.println("\nQuery: " + query);
		for (ResultDoc rdoc : results) {
			if (relDocs.contains(rdoc.title())) {
				//how to accumulate average precision (avgp) when we encounter a relevant document
				if(foundflag==0) {
					location=i;
					foundflag=1;
					rr=1.0/location;
					return rr;
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
			rr=1.0/location;
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
		relDocs.remove("");
		System.out.println("\nQuery: " + query);
		for (ResultDoc rdoc : results) {
			if (relDocs.contains(rdoc.title())) {
				//how to accumulate average precision (avgp) when we encounter a relevant document
				ndcg=ndcg+(Math.pow(2,1)-1)/(Math.log(1+i)/Math.log(2));
				//idcg=idcg+(Math.pow(2,1)-1)/(Math.log(1+i)/Math.log(2));
				System.out.print("  ");
			} else {
				//how to accumulate average precision (avgp) when we encounter an irrelevant document
				//idcg=idcg+(Math.pow(2,1)-1)/(Math.log(1+i)/Math.log(2));
				System.out.print("X ");
			}
			System.out.println(i + ". " + rdoc.title());
			++i;
			if((i-1)==k)
				break;
		}

		for(i=1;i<=Math.min(k,relDocs.size());i++)
		{
			idcg=idcg+(Math.pow(2,1)-1)/(Math.log(1+i)/Math.log(2));
		}

		return ndcg/idcg;
	}
}

/*
BDP:
MAP: 0.2109362513480831
P@10: 0.2881720430107527
MRR: 0.5949318511875813
NDCG: 0.3428142885027481

MAP: 0.2255069041802961
P@10: 0.2881720430107527
MRR: 0.5949318511875813
NDCG: 0.3498102343135518


JM:
MAP: 0.2585402815365573
P@10: 0.34193548387096767
MRR: 0.6784570463143281
NDCG: 0.4109984662133125

OK BM25:
MAP: 0.21767134429226873
P@10: 0.30752688172043013
MRR: 0.5937579327977649
NDCG: 0.36021567513114183

OK Best:
MAP: 0.2593606437240002
P@10: 0.3473118279569893
MRR: 0.6809191551502591
NDCG: 0.41992821113007117

OK wo/ LengthNmz
MAP: 0.2598598819937819
P@10: 0.34946236559139787
MRR: 0.6825571629462367
NDCG: 0.4215108662332032

OK no filters
MAP: 0.13381689247328168
P@10: 0.18817204301075266
MRR: 0.4776819633662015
NDCG: 0.23484900173070408

OK STOP ONLY
MAP: 0.1938919040832655
P@10: 0.27204301075268816
MRR: 0.6207839210254522
NDCG: 0.3347817880228747

OK STEM ONLY
AP: 0.17472800940146416
P@10: 0.24516129032258066
MRR: 0.5663052707297423
NDCG: 0.2970884931472461

PL:
MAP: 0.15617084607145687
P@10: 0.23870967741935492
MRR: 0.4344164130213565
NDCG: 0.2672026673477571

TF-IDF:
MAP: 0.256122813271248
P@10: 0.35806451612903223
MRR: 0.6863789237900358
NDCG: 0.41584925215733465

MAP: 0.2742770531292716
P@10: 0.35806451612903223
MRR: 0.6863789237900358
NDCG: 0.42432380360103067


DP:
MAP: 0.1809053518768523
P@10: 0.2376344086021506
MRR: 0.5351006017760667
NDCG: 0.2841356996730146

DP Best:
MAP: 0.24470134486387862
P@10: 0.34838709677419366
MRR: 0.6534523384300047
NDCG: 0.3992661067095989

Q2
Best Param all left most?

*/