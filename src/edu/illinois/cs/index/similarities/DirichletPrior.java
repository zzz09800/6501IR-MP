package edu.illinois.cs.index.similarities;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.LMSimilarity;

public class DirichletPrior extends LMSimilarity {

	private LMSimilarity.DefaultCollectionModel model; // this would be your reference model
	private float queryLength = 0; // will be set at query time automatically

	public DirichletPrior() {
		model = new LMSimilarity.DefaultCollectionModel();
	}

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

		double pwc,n,niu,pswd,alpha;
		niu=2800;   //[2000,3000] 2500
		n=docLength;

		pwc=model.computeProbability(stats);
		pswd=(termFreq+niu*pwc)/(n+niu);

		alpha=niu/(n+niu);

		//res=(float)(Math.log10(pswd/(alpha*pwc))+queryLength*Math.log(alpha));
		res=(float)(Math.log10(pswd/(alpha*pwc))+Math.log(alpha));

		return res;
	}

	@Override
	public String getName() {
		return "Dirichlet Prior";
	}

	@Override
	public String toString() {
		return getName();
	}

	public void setQueryLength(float length) {
		queryLength = length;
	}
}
