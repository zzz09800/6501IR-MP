package edu.illinois.cs.index.similarities;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class OkapiBM25 extends SimilarityBase {
	/**
	 * Returns a score for a single term in the document.
	 *
	 * @param stats     Provides access to corpus-level statistics
	 * @param termFreq
	 * @param docLength
	 */
	@Override
	protected float score(BasicStats stats, float termFreq, float docLength) {
		float res;
		double k1, k2, b;
		double head, body, tail;
		double N, df, cwq, cwd, avgDocLength;

		k1 = 1.2;   //[1.2,2] 1.5    Max=1.2
		k2 = 750;   //(0,1000] 750   Max=>>>
		b = 0.75;    //[0.75,1.2] 1.0 Max=0.75

		N = stats.getNumberOfDocuments();
		df = stats.getDocFreq();
		cwd = termFreq;
		//avgDocLength = stats.getNumberOfFieldTokens() * stats.getAvgFieldLength();
		avgDocLength = stats.getAvgFieldLength();
		cwq = 1;

		head = Math.log((N - df + 0.5) / (df + 0.5));
		body = ((k1 + 1) * cwd) / (k1 * (1 - b + b * (docLength / avgDocLength)) + cwd);
		tail = ((k2 + 1) * cwq) / (k2 + cwq);

		res = (float) (head * body * tail);

		return res;
	}

	@Override
	public String toString() {
		return "Okapi BM25";
	}

}
