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


// Resnik: computes the semantic relatedness of word senses using an information content based measure as described by Resnik (1995).
// (text from Perl version) -- Resnik (1995) uses the information content of concepts, computed from their frequency of occurrence in a large corpus,
// to determine the semantic relatedness of word senses.
// David Hope, 2008, University Of Sussex


public class Resnik
{
 	private IDictionary 			dict 				=	null;
 	private ICFinder 				icfinder 		=	null;
 	private String[]					editor			=	null;
	private NumberFormat		formatter		=	new DecimalFormat("0.0000");

	public Resnik(IDictionary dict, ICFinder icfinder)
	{
		System.out.println("... Resnik");
		this.dict 		= 	dict;
		this.icfinder 	= 	icfinder;
	}



// res(1)
// (text from Perl version) -- Computes the relatedness of two word senses using an information content scheme.
// The relatedness is equal to the information content of the least common subsumer of the input synsets.
// The relatedness value returned by the res measure is equal to the information content of the Least Common Subsumer (LCS) of the two input synsets.
// This means that the value will be greater-than or equal-to zero.
// The upper bound on the value is generally quite large and varies depending upon the information content file being used.
// To be precise, the upper bound is ln (N), where N is the sum of the frequencies of all the synsets in the information content files.

// The Resnick measure is sometimes considered a "coarse" measure.
// Since the relatedness of two synsets depends only upon the information content of their LCS, all pairs of synsets
// that have the same LCS will have exactly the same relatedness. For example, the pairs dog#n#1-monkey#n#1 and canine#n#1-primate#n#2.


// (Perl version) Returns: Unless a problem occurs, the return value is the relatedness score. If no path exists between the two word senses,
// then **a large negative number** is returned - ??? ugh!!!!

// (Java version) Returns: Unless a problem occurs, the return value is the relatedness score. If no path exists between the two word senses,
// then **0** is returned.
	public double res(String w1, int s1, String w2, int s2, String pos)
	{
		double 			res 		= 0.0;
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
// get <lcs>
		ISynset		lcs			=	getLCS(synset1, synset2, pos);
		double		ic3			=	0.0;
		if(lcs == null) // i.e. if there is no <LCS> for the 2 synsets
		{
			ic3	=	icfinder.getIC(null, pos); // not strictly necessary, but is here for transparency
		}
		else
		{
			ic3	=	icfinder.getIC(""+ lcs.getOffset(), pos);
		}
// ...........................................................................................................................................
		res	=	ic3;
// ...........................................................................................................................................
		return ( res );
	}

// res(2) all senses
	public TreeMap<String, Double> res(String w1, String w2, String pos)
	{
		// apple#pos#sense banana#pos#sense 	resscore
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
// get the res scores for the (sense pairs)
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
					double resscore = res(w1, sx, w2, sy, pos);
					map.put((w1 + "#" + pos + "#" + sx + "," + w2 + "#" + pos + "#" + sy), resscore);
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



// res(3) all senses of word 1 vs. a specific sense of word 2
	public TreeMap<String, Double> res(String w1, String w2, int s2, String pos)
	{
		// apple#pos#sense banana#pos#sense 	resscore
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
// get the res scores for the (sense pairs)
	 		List<IWordID> word1IDs = word1.getWordIDs(); // all senses of word 1
	 		int movingsense = 1;
	 		for(IWordID idX : word1IDs)
	 		{
				double resscore = res(w1, movingsense, w2, s2, pos);
				map.put((w1 + "#" + pos + "#" + movingsense + "," + w2 + "#" + pos + "#" + s2), resscore);
				movingsense++;
			}
		}
		else
		{
			return ( map );
		}
		return ( map );
	}

// res(4) a specific sense of word 1 vs. all senses of word 2
	public TreeMap<String, Double> res(String w1, int s1, String w2, String pos)
	{
		// (key)apple#pos#sense banana#pos#sense 	(value)resscore
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
// get the res scores for the (sense pairs)
	 		List<IWordID> word2IDs = word2.getWordIDs(); // all senses of word 2
	 		int movingsense = 1;
	 		for(IWordID idX : word2IDs)
	 		{
				double resscore = res(w1, s1, w2, movingsense, pos);
				map.put((w1 + "#" + pos + "#" + s1 + "," + w2 + "#" + pos + "#" + movingsense), resscore);
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
		TreeMap<String, Double> pairs = res(w1, w2, pos);
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
		Resnik res = new Resnik(dict, icfinder);
// ....................................................................................................................................................................


// Examples Of Use

// res(1) specific senses for both words
		double resscore = res.res("apple", 1, "banana", 2, "n"); // "word1", sense#, "word2", sense#, "POS"
		System.out.println("specific senses");
		if(resscore != 0) // 0 is an error code i.e it means that something isn't right e.g. words are not in WordNet, wrong POS etc
		{
			System.out.println("res:\t" + formatter.format(resscore));
		}
		System.out.println();

// res(2) all senses: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map = res.res("apple", "banana", "n"); // "word1", "word2", "POS"
		System.out.println("all senses");
		for(String pair : map.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map.get(pair)));
		}
		System.out.println();



// max value (i.e. highest score!) : get the highest score for 2 words
		double maxvalue = res.max("apple", "banana", "n"); // "word1", "word2", "POS"
		System.out.println("max value");
		System.out.println(formatter.format(maxvalue));
		System.out.println();







// res(3) all senses of word 1 vs. a specific sense of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map3 = res.res("apple", "banana", 2, "n"); // "word1", "word2", sense#, "POS"
		System.out.println("all senses of word 1 vs. fixed sense of word 2");
		for(String pair : map3.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map3.get(pair)));
		}
		System.out.println();

// res(4) a specific sense of word 1 vs. all senses of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map4 = res.res("apple", 1, "banana", "n"); // "word1", sense#, "word2", "POS"
		System.out.println("fixed sense of word 1 vs. all senses of word 2");
		for(String pair : map4.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map4.get(pair)));
		}
    }
}
