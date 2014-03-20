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


// 'Lin' : computes the semantic relatedness of word senses using the information content based measure as described in Lin, 1998.
// David Hope, 2008, University Of Sussex


public class Lin
{
 	private IDictionary 			dict 				=	null;
 	private ICFinder 				icfinder 		=	null;
 	private String[]					editor			=	null;
	private NumberFormat		formatter		=	new DecimalFormat("0.0000");

	public Lin(IDictionary dict, ICFinder icfinder)
	{
		System.out.println("... Lin");
		this.dict 		= 	dict;
		this.icfinder 	= 	icfinder;
	}

// lin(1) -- THE FOUNDATION OF ALL THE VARIATIONS OF THE INPUT DATA --
// 'lin' Computes the relatedness of two word senses using an information content scheme.
// The relatedness is equal to twice the information content of the LCS divided by the sum of the information content of each input synset.
// Parameters: 2 word senses in "word#pos#sense" format
/*
Semantic Relatedness_Lin	=	2 * IC(lcs) / (IC(synset1) + IC(synset2)). Where IC(*) is the information content of *.
The retuned value will be in the range 0 ... 1

If IC(synset1) or IC(synset2) is zero, then zero is returned as the relatedness score, due to lack of data.
Ted's observation: Ideally, the information content of a synset would be zero only if that synset were the root node,
but when the frequency of a synset is zero, we use the value of zero as the information content because of a lack of better
alternatives.
*/
	public double lin(String w1, int s1, String w2, int s2, String pos)
	{
		double 			lin 		= 0.0;
		IIndexWord	word1	=	null;
		IIndexWord 	word2	=	null;
// get the WordNet words
		if(pos.equalsIgnoreCase("n"))
		{
 			word1 = dict.getIndexWord(w1, POS.NOUN);
 			word2 = dict.getIndexWord(w2, POS.NOUN);
		}
		if(pos.equalsIgnoreCase("v"))
		{
 			word1 = dict.getIndexWord(w1, POS.VERB);
 			word2 = dict.getIndexWord(w2, POS.VERB);
		}
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

		lin	=	(2.0 * ic3) / (ic1 + ic2);
// ...........................................................................................................................................

		return ( lin );
	}

// lin(2) all senses
	public TreeMap<String, Double> lin(String w1, String w2, String pos)
	{
		// apple#pos#sense banana#pos#sense 	linscore
		TreeMap<String, Double>	map	=	new TreeMap<String, Double>();

		IIndexWord	word1	=	null;
		IIndexWord 	word2	=	null;
// get the WordNet words
		if(pos.equalsIgnoreCase("n"))
		{
 			word1 = dict.getIndexWord(w1, POS.NOUN);
 			word2 = dict.getIndexWord(w2, POS.NOUN);
		}
		if(pos.equalsIgnoreCase("v"))
		{
 			word1 = dict.getIndexWord(w1, POS.VERB);
 			word2 = dict.getIndexWord(w2, POS.VERB);
		}
// [error check]: check the words exist in WordNet
// [error check]: check the words exist in WordNet
		if(word1 != null && word2 != null)
		{
// get the lin scores for the (sense pairs)
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
					double linscore = lin(w1, sx, w2, sy, pos);
					map.put((w1 + "#" + pos + "#" + sx + "," + w2 + "#" + pos + "#" + sy), linscore);
					sy++;
				}
				sx++;
			}
		}
		else
		{
			return ( map );
		}
		return ( map );
	}



// lin(3) all senses of word 1 vs. a specific sense of word 2
	public TreeMap<String, Double> lin(String w1, String w2, int s2, String pos)
	{
		// apple#pos#sense banana#pos#sense 	linscore
		TreeMap<String, Double>	map	=	new TreeMap<String, Double>();

		IIndexWord	word1	=	null;
		IIndexWord 	word2	=	null;
// get the WordNet words
		if(pos.equalsIgnoreCase("n"))
		{
 			word1 = dict.getIndexWord(w1, POS.NOUN);
 			word2 = dict.getIndexWord(w2, POS.NOUN);
		}
		if(pos.equalsIgnoreCase("v"))
		{
 			word1 = dict.getIndexWord(w1, POS.VERB);
 			word2 = dict.getIndexWord(w2, POS.VERB);
		}
// [error check]: check the words exist in WordNet
// [error check]: check the words exist in WordNet
		if(word1 != null && word2 != null)
		{
// get the lin scores for the (sense pairs)
	 		List<IWordID> word1IDs = word1.getWordIDs(); // all senses of word 1
	 		int movingsense = 1;
	 		for(IWordID idX : word1IDs)
	 		{
				double linscore = lin(w1, movingsense, w2, s2, pos);
				map.put((w1 + "#" + pos + "#" + movingsense + "," + w2 + "#" + pos + "#" + s2), linscore);
				movingsense++;
			}
		}
		else
		{
			return ( map );
		}
		return ( map );
	}

// lin(4) a specific sense of word 1 vs. all senses of word 2
	public TreeMap<String, Double> lin(String w1, int s1, String w2, String pos)
	{
		// (key)apple#pos#sense banana#pos#sense 	(value)linscore
		TreeMap<String, Double>	map	=	new TreeMap<String, Double>();
		IIndexWord	word1	=	null;
		IIndexWord 	word2	=	null;
// get the WordNet words
		if(pos.equalsIgnoreCase("n"))
		{
 			word1 = dict.getIndexWord(w1, POS.NOUN);
 			word2 = dict.getIndexWord(w2, POS.NOUN);
		}
		if(pos.equalsIgnoreCase("v"))
		{
 			word1 = dict.getIndexWord(w1, POS.VERB);
 			word2 = dict.getIndexWord(w2, POS.VERB);
		}
// [error check]: check the words exist in WordNet
		if(word1 != null && word2 != null)
		{
// get the lin scores for the (sense pairs)
	 		List<IWordID> word2IDs = word2.getWordIDs(); // all senses of word 2
	 		int movingsense = 1;
	 		for(IWordID idX : word2IDs)
	 		{
				double linscore = lin(w1, s1, w2, movingsense, pos);
				map.put((w1 + "#" + pos + "#" + s1 + "," + w2 + "#" + pos + "#" + movingsense), linscore);
				movingsense++;
			}
		}
		else
		{
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
		TreeMap<String, Double> pairs = lin(w1, w2, pos);
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
		Lin lin = new Lin(dict, icfinder);
// ....................................................................................................................................................................


// Examples Of Use
//abbreviate v 2 4 @ ~ $ + 2 0 00243900 00243749
//abdicate
// lin(1) specific senses for both words
		double linscore = lin.lin("apple", 1, "pear", 1, "n"); // "word1", sense#, "word2", sense#, "POS"
		System.out.println("specific senses");
		//if(linscore != 0) // 0 is an error code i.e it means that something isn't right e.g. words are not in WordNet, wrong POS etc
		//{
			System.out.println("lin:\t" + formatter.format(linscore));
		//}
		//System.out.println();
/*
// lin(2) all senses: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map = lin.lin("live", "banana", "n"); // "word1", "word2", "POS"
		System.out.println("all senses");
		for(String pair : map.keySet())
		{
			EasyIn.pause(pair + "\t" + formatter.format(map.get(pair)));
		}
		System.out.println();

// max value (i.e. highest score!) : get the highest score for 2 words
		double maxvalue = lin.max("live", "banana", "n"); // "word1", "word2", "POS"
		System.out.println("max value");
		System.out.println(formatter.format(maxvalue));
		System.out.println();

// lin(3) all senses of word 1 vs. a specific sense of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map3 = lin.lin("live", "banana", 2, "n"); // "word1", "word2", sense#, "POS"
		System.out.println("all senses of word 1 vs. fixed sense of word 2");
		for(String pair : map3.keySet())
		{
			EasyIn.pause(pair + "\t" + formatter.format(map3.get(pair)));
		}
		System.out.println();

// lin(4) a specific sense of word 1 vs. all senses of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map4 = lin.lin("live", 1, "banana", "n"); // "word1", sense#, "word2", "POS"
		System.out.println("fixed sense of word 1 vs. all senses of word 2");
		for(String pair : map4.keySet())
		{
			EasyIn.pause(pair + "\t" + formatter.format(map4.get(pair)));
		}
*/
    }
}
