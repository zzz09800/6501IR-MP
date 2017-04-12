package edu.illinois.cs.index.similarities;

import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.LMSimilarity;

public class JelinekMercer extends LMSimilarity {

	private LMSimilarity.DefaultCollectionModel model; // this would be your reference model
	private float queryLength = 0; // will be set at query time automatically

	public JelinekMercer() {
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

		double lamada,pswd,pml,pwc,alpha;

		lamada=0.1; //[0,1] 0.1
		alpha=lamada;

		pwc=model.computeProbability(stats);
		pml=termFreq/docLength;
		pswd=(1-lamada)*pml+lamada*pwc;

		res=(float)(Math.log10(pswd/(alpha*pwc))+Math.log10(alpha));
		//res=(float)(Math.log(pswd/(alpha*pwc))+queryLength*Math.log(alpha));

		return res;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getName() {
		return "Jelinek-Mercer Language Model";
	}

	public void setQueryLength(float length) {
		queryLength = length;
	}

}
