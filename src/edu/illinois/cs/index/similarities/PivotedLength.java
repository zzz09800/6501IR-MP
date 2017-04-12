package edu.illinois.cs.index.similarities;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class PivotedLength extends SimilarityBase {
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
		double s, N, df, cwq, cwd, avgDocLength;
		double head, body, tail;

		N = stats.getNumberOfDocuments();
		df = stats.getDocFreq();
		cwd = termFreq;
		avgDocLength = stats.getAvgFieldLength();
		cwq = 1;

		s=0.75; //[0,1] 0.75

		head=(1+Math.log(1+Math.log(cwd)))/(1-s+s*(docLength/avgDocLength));
		body=cwq;
		tail=Math.log((N+1)/df);

		res=(float)(head*body*tail);

		return res;
	}

	@Override
	public String toString() {
		return "Pivoted Length Normalization";
	}

}
