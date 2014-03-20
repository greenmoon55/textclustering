//package edu.sussex.nlp.jws;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import java.net.*;
import edu.mit.jwi.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Hashtable;
import java.util.TreeMap;
import java.text.*;
import java.util.regex.*;
import java.util.ArrayList;


// 'JiangAndConrath': computes the semantic relatedness of word senses according to the method as described by Jiang and Conrath (1997).
//David Hope, 2008, University Of Sussex


public class JiangAndConrath
{
 	private IDictionary 			dict 				=	null;
 	private ICFinder 				icfinder 		=	null;
 	private String[]					editor			=	null;
	private NumberFormat		formatter		=	new DecimalFormat("0.0000");

	private CompoundWords		compounds	=	null;
 	private Pattern						cp					=	null;
 	private Matcher						cm				=	null;



	public JiangAndConrath(IDictionary dict, ICFinder icfinder)
	{
		System.out.println("... JiangAndConrath");
		this.dict 		= 	dict;
		this.icfinder 	= 	icfinder;
// check compound words
		compounds	=	new CompoundWords();
		cp = Pattern.compile("[-_\\s]");


	}


// see if WordNet actually contains the word (pos)
	private IIndexWord getWordNetWord(String word, String pos)
	{
		IIndexWord	indexword	=	null;
		POS 				setPOS 		=	null;
		if(pos.equalsIgnoreCase("n"))
			setPOS = POS.NOUN;
		if(pos.equalsIgnoreCase("v"))
			setPOS = POS.VERB;
// check if compound word
		cm	=	cp.matcher(word);
		if(cm.find()) // if compound word
		{
			ArrayList<String> compoundwords = compounds.getCompounds(word); // get all possible combinations: (- _ space) e.g. beta-blocker_agent
			for(String cw : compoundwords)
			{
				indexword = dict.getIndexWord(cw, setPOS);
				if(indexword != null) // !!! if WordNet accepts one of the compound combination types .. take it !!!
				{
					return ( indexword );
				}
			}
		}
		else // is non-compound word
		{
			indexword = dict.getIndexWord(word, setPOS);
		}
		return ( indexword );
	}





// jcn(1) -- THE MAIN METHOD FOR COMPUTING Jiang & Conrath
/*
(cribbed from Ted's site)
This module computes the semantic relatedness of word senses according to the method described by Jiang and Conrath (1997).
This measure is based on a combination of using edge counts in the WordNet 'is-a' hierarchy and using the information content values of the WordNet concepts,
as described in the paper by Jiang and Conrath. Their measure, however, computes values that indicate the semantic distance between words (as opposed to their
semantic relatedness). In this implementation of the measure we invert the value so as to obtain a measure of semantic relatedness.
Other issues that arise due to this inversion (such as handling of zero values in the denominator) have been taken care of as special cases.

The relatedness value returned by the jcn measure is equal to 1 / jcn_distance, where jcn_distance is equal to IC(synset1) + IC(synset2) - 2 * IC(lcs).
The original metric proposed by Jiang and Conrath was this distance measure. By taking the multiplicative inverse of it, we have converted it to a
measure of similarity, but by so doing, we have shifted the distribution of scores.

*/

	public double jcn(String w1, int s1, String w2, int s2, String pos)
	{
		double 			jcn 		= 0.0;
// get the WordNet words
		IIndexWord	word1	=	getWordNetWord(w1, pos);
		IIndexWord 	word2	=	getWordNetWord(w2, pos);

// [error check]: check the words exist in WordNet
		if(word1 == null)
		{
			System.out.println(w1 + "(" + pos + ") not found in WordNet " + dict.getVersion());
			return(0); // 0 is an error code
		}
		if(word2 == null)
		{
			System.out.println(w2 + "(" + pos + ") not found in WordNet " + dict.getVersion());
			return(0); // 0 is an error code
		}
// [error check]: check the sense numbers are not greater than the true number of senses in WordNet
 		List<IWordID> word1IDs = word1.getWordIDs();
 		List<IWordID> word2IDs = word2.getWordIDs();
		if(s1 >  word1IDs.size())
		{
			System.out.println(w1 + " sense: " + s1 + " not found in WordNet " + dict.getVersion());
			return(0); // 0 is an error code
		}
		if(s2 > word2IDs.size())
		{
			System.out.println(w2 + " sense: " + s2 + " not found in WordNet " + dict.getVersion());
			return(0); // 0 is an error code
		}
// ...........................................................................................................................................
// get the {synsets}
 		IWordID	word1ID	=	word1.getWordIDs().get(s1 - 1); // get the right sense of word 1
 		ISynset		synset1		=	dict.getWord(word1ID).getSynset();
 		//System.out.println(synset1);

 		IWordID	word2ID	=	word2.getWordIDs().get(s2 - 1); // get the right sense of word 2
 		ISynset		synset2		=	dict.getWord(word2ID).getSynset();
 		//System.out.println(synset2);
// ...........................................................................................................................................

// {synset} 1 IC
		double ic1	=	icfinder.getIC(""+ synset1.getOffset(), pos);
		//System.out.println(ic1);
// {synset} 2 IC
		double ic2	=	icfinder.getIC(""+ synset2.getOffset(), pos);
		//System.out.println(ic2);
// [error check] If IC(synset1) or IC(synset2) is zero, then zero is returned as the relatedness score, due to lack of data.
		if(ic1 == 0.0 || ic2 == 0.0)
		{
			return ( 0.0 );
		}
// <lcs> IC
// get <lcs>
		ISynset		lcs			=	getLCS(synset1, synset2, pos);
		double		ic3			=	0.0;
		if(lcs == null) // i.e. if there is no <LCS> for the 2 synsets
		{
			ic3	=	icfinder.getIC(null, pos); // not strictly necessary, but is here for transparency
			// !!! this might cause errors as you will get o.o returned here !!!
		}
		else
		{
			ic3	=	icfinder.getIC(""+ lcs.getOffset(), pos);
		}
		//System.out.println(ic3);
// ...........................................................................................................................................
// first, check the two special cases before we return a value
// 1.
		if(ic1 == 0.0 && ic2 == 0.0 && ic3 == 0.0)
		{
			return ( 0.0 ); // !!!as per Perl version !!!
		}
// 2.
		if((synset1.equals(synset2) && synset2.equals(lcs)) || ((ic1 + ic2) == (2.0 * ic3)))
		{
			double rootsum = icfinder.getRootSum(pos);
			jcn 	=	( 1.0 / (-Math.log((rootsum - 0.01)/ rootsum)) ); // !!! as per Perl version... weird-a-rama!!!
			return ( jcn );
		}
// 'normal' case


		jcn	=	1.0 / ((ic1 + ic2) - (2.0 * ic3));
// ...........................................................................................................................................
/*
//There are two special cases that need to be handled carefully when computing relatedness; both of these involve the case when jcn_distance is zero.
//In the first case, we have ic(synset1) = ic(synset2) = ic(lcs) = 0. In an ideal world, this would only happen when all three concepts, viz. synset1, synset2, and lcs, are the
//root node. However, when a synset has a frequency count of zero, we use the value 0 for the information content. In this first case, we return 0 due to lack of data.

//In the second case, we have ic(synset1) + ic(synset2) = 2 * ic(lics). This is almost always found when synset1 = synset2 = lcs (i.e., the two input synsets are the same).
//Intuitively this is the case of maximum relatedness, which would be infinity, but it is impossible to return infinity. Insteady we find the smallest possible distance greater than
//zero and return the multiplicative inverse of that distance.
*/
		return ( jcn );
	}




// jcn(2) all senses
	public TreeMap<String, Double> jcn(String w1, String w2, String pos)
	{
		// apple#pos#sense banana#pos#sense 	jcnscore
		TreeMap<String, Double>	map	=	new TreeMap<String, Double>();

// get the WordNet words
		IIndexWord	word1	=	getWordNetWord(w1, pos);
		IIndexWord 	word2	=	getWordNetWord(w2, pos);

		if(word1 != null && word2 != null)
		{
// get the jcn scores for the (sense pairs)
	 		List<IWordID> word1IDs = word1.getWordIDs(); // all senses of word 1
	 		List<IWordID> word2IDs = word2.getWordIDs(); // all senses of word 2
	 		int sx = 1;
	 		ISynset synset1 = null;
	 		ISynset synset2 = null;
	 		for(IWordID idX : word1IDs)
	 		{
	 			int sy = 1;
				for(IWordID idY : word2IDs)
				{
					double jcnscore = jcn(w1, sx, w2, sy, pos);
					map.put((word1.getLemma() + "#" + pos + "#" + sx + "," + word2.getLemma() + "#" + pos + "#" + sy), jcnscore);
					sy++;
				}
				sx++;
			}
		}
		else
		{
			System.out.println(w1 + " and/or " + w2 + " in POS " + pos +  " do not exist in WordNet " + dict.getVersion());
			return ( map ); // i.e. return 'nothing' but *not* null
		}
		return ( map );
	}



// jcn(3) all senses of word 1 vs. a specific sense of word 2
	public TreeMap<String, Double> jcn(String w1, String w2, int s2, String pos)
	{
		// apple#pos#sense banana#pos#sense 	jcnscore
		TreeMap<String, Double>	map	=	new TreeMap<String, Double>();

// get the WordNet words
		IIndexWord	word1	=	getWordNetWord(w1, pos);
		IIndexWord 	word2	=	getWordNetWord(w2, pos);

		if(word1 != null && word2 != null)
		{
// get the jcn scores for the (sense pairs)
	 		List<IWordID> word1IDs = word1.getWordIDs(); // all senses of word 1
	 		int movingsense = 1;
	 		for(IWordID idX : word1IDs)
	 		{
				double jcnscore = jcn(w1, movingsense, w2, s2, pos);
				map.put((w1 + "#" + pos + "#" + movingsense + "," + w2 + "#" + pos + "#" + s2), jcnscore);
				movingsense++;
			}
		}
		else
		{
			System.out.println(word1.getLemma() + " and/or " + word2.getLemma() + " in POS " + pos +  " do not exist in WordNet " + dict.getVersion());
			return ( map );
		}
		return ( map );
	}

// jcn(4) a specific sense of word 1 vs. all senses of word 2
	public TreeMap<String, Double> jcn(String w1, int s1, String w2, String pos)
	{
		// (key)apple#pos#sense banana#pos#sense 	(value)jcnscore
		TreeMap<String, Double>	map	=	new TreeMap<String, Double>();
// get the WordNet words
		IIndexWord	word1	=	getWordNetWord(w1, pos);
		IIndexWord 	word2	=	getWordNetWord(w2, pos);

		if(word1 != null && word2 != null)
		{
// get the jcn scores for the (sense pairs)
		 	List<IWordID> word2IDs = word2.getWordIDs(); // all senses of word 2
		 	int movingsense = 1;
		 	for(IWordID idX : word2IDs)
		 	{
				double jcnscore = jcn(w1, s1, w2, movingsense, pos);
				map.put((word1.getLemma() + "#" + pos + "#" + s1 + "," + word2.getLemma() + "#" + pos + "#" + movingsense), jcnscore);
				movingsense++;
			}
		}
		else
		{
			System.out.println(w1 + " and/or " + w2 + " in POS " + pos +  " do not exist in WordNet " + dict.getVersion());
			return ( map );
		}
		return ( map );
	}

// Utilities _________________________________________________________________________

// 1. GET <LCS> WITH HIGHEST IC
// getLCS -  get the LCS (least common subsumer) <hypernym> or, indeed, {synset} itself!
// the <hypernym> | {synset} with the highest Information Content (if there is more than one
// with the highest IC, we just get any of the 'tied' synsets as we are onlu using the value
	public ISynset getLCS(ISynset synset1, ISynset synset2, String pos)
	{
// synset1
		HashSet<ISynsetID> s1 = new HashSet<ISynsetID>(); s1.add(synset1.getID());
		HashSet<ISynsetID> h1 = new HashSet<ISynsetID>();
		getHypernyms(s1,h1);
// !!! important !!! we must add the original {synset} back in, as the 2 {synsets}(senses) we are comparing may be equivalent i.e. bthe same {synset}!
		h1.add(synset1.getID());

// synset2
		HashSet<ISynsetID> s2 = new HashSet<ISynsetID>(); s2.add(synset2.getID());
		HashSet<ISynsetID> h2 = new HashSet<ISynsetID>();
		getHypernyms(s2,h2);
		h2.add(synset2.getID()); // ??? don't really need this ???

// get the candidate <lcs>s i.e. the intersection of all <hypernyms> | {synsets} which subsume the 2 {synsets}
		h1.retainAll(h2);
		if(h1.isEmpty())
		{
			return (null); // i.e. there is *no* <LCS> for the 2 synsets
		}

// get *a* <lcs> with the highest Information Content
		double 		max 		= -Double.MAX_VALUE;
		ISynsetID	maxlcs	=	null;
		for(ISynsetID h : h1)
		{
			double ic = icfinder.getIC("" + h.getOffset(), pos); // use ICfinder to get the Information Content value
			if(ic > max)
			{
				max 		=	ic;
				maxlcs	=	h;
			}
		}
		return (dict.getSynset(maxlcs)); // return the <synset} with *a* highest IC value
	}
// 1.1 GET <HYPERNYMS>
	private void getHypernyms(HashSet<ISynsetID> synsets, HashSet<ISynsetID> allhypernms)
	{
		HashSet<ISynsetID> 	hypernyms	=	new HashSet<ISynsetID>();
		for(ISynsetID s : synsets)
		{
			ISynset		synset 	= dict.getSynset(s);
			hypernyms.addAll(synset.getRelatedSynsets(Pointer.HYPERNYM)); 					// get the <hypernyms> if there are any
 			hypernyms.addAll(synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE));	// get the <hypernyms> (instances) if there are any
		}
		if(!hypernyms.isEmpty())
		{
			allhypernms.addAll(hypernyms);
			getHypernyms(hypernyms, allhypernms);
		}
		return;
	}
// Utilities _________________________________________________________________________


// get max score for all sense pairs
	public double max(String w1, String w2, String pos)
	{
		double max = 0.0;
		TreeMap<String, Double> pairs = jcn(w1, w2, pos);
		for(String p : pairs.keySet())
		{
			double current = pairs.get(p);
			if(current > max)
			{
				max = current;
			}
		}
		return ( max );
	}




// test
    public static void main(String[] args)
    {
// dummy WordNet setup - allows one to run the examples from this Class  ...........................................................
// WordNet vers. that you want to use (assumes that you have downloaded the version in question)
		String vers = "3.0";
// *your* WordNet(vers.) is here ...
		String wnhome 	= "C:/Program Files/WordNet/" + vers + "/dict";
// *your* IC files are here ... (assumes that you have downloaded the IC files which correspond to the WordNet(vers.)
		String icfile		= "C:/Program Files/WordNet/" + vers + "/WordNet-InfoContent-" + vers + "/ic-semcor.dat";
		URL url = null;
		try
		{
			url = new URL("file", null, wnhome);
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		if(url == null) return;
		IDictionary dict = new Dictionary(url);
		dict.open();
		ICFinder 			icfinder 			=	new ICFinder(icfile);
		NumberFormat		formatter		=	new DecimalFormat("0.0000");
// ....................................................................................................................................................................
		JiangAndConrath jcn = new JiangAndConrath(dict, icfinder);
// ....................................................................................................................................................................


// Examples Of Use


// jcn(1) specific senses
// contrition#n#1, compunction#n#1: 0.299568090603061for both words

/*
		double jcnscore = jcn.jcn("contrition8", 1, "compunction", 1, "n"); // "word1", sense#, "word2", sense#, "POS"
		System.out.println("specific senses");
		if(jcnscore != 0) // 0 is an error code i.e it means that something isn't right e.g. words are not in WordNet, wrong POS etc
		{
			System.out.println("jcn:\t" + formatter.format(jcnscore));
		}
		System.out.println();
*/

// jcn(2) all senses: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map = jcn.jcn("cat", "dog", "n"); // "word1", "word2", "POS"
		System.out.println("all senses");
		for(String pair : map.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map.get(pair)));
		}
		System.out.println();


/*
// max value (i.e. highest score!) : get the highest score for 2 words
		double maxvalue = jcn.max("eat", "consume8", "v"); // "word1", "word2", "POS"
		System.out.println("max value");
		System.out.println(formatter.format(maxvalue));
		System.out.println();


// jcn(3) all senses of word 1 vs. a specific sense of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map3 = jcn.jcn("eat", "consume8", 2, "v"); // "word1", "word2", sense#, "POS"
		System.out.println("all senses of word 1 vs. fixed sense of word 2");
		for(String pair : map3.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map3.get(pair)));
		}
		System.out.println();

// jcn(4) a specific sense of word 1 vs. all senses of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map4 = jcn.jcn("eat8", 1, "consume", "v"); // "word1", sense#, "word2", "POS"
		System.out.println("fixed sense of word 1 vs. all senses of word 2");
		for(String pair : map4.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map4.get(pair)));
		}
*/
    }
}
