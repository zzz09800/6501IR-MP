package edu.illinois.cs.index.similarities;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class TFIDFDotProduct extends SimilarityBase {
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
		double logcwd, logN_1_df;
		logN_1_df = Math.log10((stats.getNumberOfDocuments() + 1) / stats.getDocFreq());
		logcwd = Math.log10(termFreq);

		res = (float) ((1 + logcwd) * logN_1_df);

		return res;
	}

	@Override
	public String toString() {
		return "TF-IDF Dot Product";
	}
}
