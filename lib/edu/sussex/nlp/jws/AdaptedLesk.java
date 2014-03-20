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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;
import java.io.*;
import edu.mit.jwi.morph.WordnetStemmer;
import java.util.Vector;

/*
 'AdaptedLesk'

		Applies the same scoring sytem as in Banerjee & Pedersen's paper, where one squares the length of each overlap.
		However, here, I use all Pointer types, that is, all related synsets, not just those in the [relations.dat] file.

	 David Hope, 2008
*/
public class AdaptedLesk
{
 	private IDictionary 				dict 				=	null;
	private LeskGlossOverlaps 		lgo 				=	null;
	private RelatedSynsets	 		relations		=	null;


	public AdaptedLesk(IDictionary dict)
	{
		System.out.println("... Adapted Lesk : all relations");
		this.dict 		= 	dict;
		lgo				=	new LeskGlossOverlaps(dict); //  finds gloss overlap scores
		relations		=	new RelatedSynsets(dict); // get all the synsets which have a relation to 'a gloss'
	}

// lesk(1)
	public double lesk(String w1, int s1, String w2, int s2, String pos)
	{
		double 			lesk 		= 0.0;
		IIndexWord	word1	=	null;
		IIndexWord 	word2	=	null;
// get the WordNet words in the right part of speech
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
		if(pos.equalsIgnoreCase("a"))
		{
 			word1 = dict.getIndexWord(w1, POS.ADJECTIVE);
 			word2 = dict.getIndexWord(w2, POS.ADJECTIVE);
		}
		if(pos.equalsIgnoreCase("r"))
		{
 			word1 = dict.getIndexWord(w1, POS.ADVERB);
 			word2 = dict.getIndexWord(w2, POS.ADVERB);
		}
// [error check]: check that the words exist in WordNet
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

 		IWordID	word2ID	=	word2.getWordIDs().get(s2 - 1); // get the right sense of word 2
 		ISynset		synset2		=	dict.getWord(word2ID).getSynset();
// ...........................................................................................................................................
// get *all* the 'Pointers': both 'lexical' and 'semantic Pointers. These are the synsets that are
// related to a synset Include the synset itself in this set
// set 1.
		HashSet<ISynsetID>		set1	=	new HashSet<ISynsetID>();
		set1.add(synset1.getID());
		set1.addAll(getPointers(synset1));
// set 2
		HashSet<ISynsetID>		set2	=	new HashSet<ISynsetID>();
		set2.add(synset2.getID());
		set2.addAll(getPointers(synset2));
// get the overlap score
		for(ISynsetID id1 : set1)
		{
 			String gloss1		=	dict.getSynset(id1).getGloss();
			for(ISynsetID id2 : set2)
			{
 				String gloss2			=	dict.getSynset(id2).getGloss();
 				double g1g2Score	=	lgo.overlap(gloss1, gloss2); 	// get the overlap(s) score
 				lesk += g1g2Score;
			}
		}
		return ( lesk );
	}



// get *all* the Pointers for a synset
	private HashSet<ISynsetID> getPointers(ISynset synset)
	{
		HashSet<ISynsetID> pointers = relations.getAllRelatedSynsetsNoTypes(synset);
		return ( pointers );
	}

// lesk(2) all senses
	public TreeMap<String, Double> lesk(String w1, String w2, String pos)
	{
		// cat#pos#sense cat#pos#sense 	leskscore
		TreeMap<String, Double>	map	=	new TreeMap<String, Double>();

		IIndexWord	word1	=	null;
		IIndexWord 	word2	=	null;
// get the WordNet words in the right part of speech
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
		if(pos.equalsIgnoreCase("a"))
		{
 			word1 = dict.getIndexWord(w1, POS.ADJECTIVE);
 			word2 = dict.getIndexWord(w2, POS.ADJECTIVE);
		}
		if(pos.equalsIgnoreCase("r"))
		{
 			word1 = dict.getIndexWord(w1, POS.ADVERB);
 			word2 = dict.getIndexWord(w2, POS.ADVERB);
		}
// [error check]: check the words exist in WordNet
		if(word1 != null && word2 != null)
		{
// get the lesk scores for the (sense pairs)
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
					double leskscore = lesk(w1, sx, w2, sy, pos);
					map.put((w1 + "#" + pos + "#" + sx + "," + w2 + "#" + pos + "#" + sy), leskscore);
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


// lesk(3) all senses of word 1 vs. a specific sense of word 2
	public TreeMap<String, Double> lesk(String w1, String w2, int s2, String pos)
	{
		// cat#pos#sense cat#pos#sense 	leskscore
		TreeMap<String, Double>	map	=	new TreeMap<String, Double>();

		IIndexWord	word1	=	null;
		IIndexWord 	word2	=	null;
// get the WordNet words
// get the WordNet words in the right part of speech
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
		if(pos.equalsIgnoreCase("a"))
		{
 			word1 = dict.getIndexWord(w1, POS.ADJECTIVE);
 			word2 = dict.getIndexWord(w2, POS.ADJECTIVE);
		}
		if(pos.equalsIgnoreCase("r"))
		{
 			word1 = dict.getIndexWord(w1, POS.ADVERB);
 			word2 = dict.getIndexWord(w2, POS.ADVERB);
		}
// [error check]: check the words exist in WordNet
		if(word1 != null && word2 != null)
		{
// get the lesk scores for the (sense pairs)
	 		List<IWordID> word1IDs = word1.getWordIDs(); // all senses of word 1
	 		int movingsense = 1;
	 		for(IWordID idX : word1IDs)
	 		{
				double leskscore = lesk(w1, movingsense, w2, s2, pos);
				map.put((w1 + "#" + pos + "#" + movingsense + "," + w2 + "#" + pos + "#" + s2), leskscore);
				movingsense++;
			}
		}
		else
		{
			return ( map);
		}
		return ( map );
	}


// lesk(4) a specific sense of word 1 vs. all senses of word 2
	public TreeMap<String, Double> lesk(String w1, int s1, String w2, String pos)
	{
		// (key)cat#pos#sense cat#pos#sense 	(value)leskscore
		TreeMap<String, Double>	map	=	new TreeMap<String, Double>();
		IIndexWord	word1	=	null;
		IIndexWord 	word2	=	null;
// get the WordNet words
// get the WordNet words in the right part of speech
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
		if(pos.equalsIgnoreCase("a"))
		{
 			word1 = dict.getIndexWord(w1, POS.ADJECTIVE);
 			word2 = dict.getIndexWord(w2, POS.ADJECTIVE);
		}
		if(pos.equalsIgnoreCase("r"))
		{
 			word1 = dict.getIndexWord(w1, POS.ADVERB);
 			word2 = dict.getIndexWord(w2, POS.ADVERB);
		}
// [error check]: check the words exist in WordNet
		if(word1 != null && word2 != null)
		{
// get the lesk scores for the (sense pairs)
	 		List<IWordID> word2IDs = word2.getWordIDs(); // all senses of word 2
	 		int movingsense = 1;
	 		for(IWordID idX : word2IDs)
	 		{
				double leskscore = lesk(w1, s1, w2, movingsense, pos);
				map.put((w1 + "#" + pos + "#" + s1 + "," + w2 + "#" + pos + "#" + movingsense), leskscore);
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

// get max score for all sense pairs
	public double max(String w1, String w2, String pos)
	{
		double max = 0.0;
		TreeMap<String, Double> pairs = lesk(w1, w2, pos);
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
// turn the stop words list on/off
	public void useStopList(boolean use)
	{
		lgo.useStopList(use); // !!! actually turned on/off in the gloss overlaps class !!!
	}
// turn the lemmatiser on/off
	public void useLemmatiser(boolean use)
	{
		lgo.useLemmatiser(use); // !!! actually turned on/off in the gloss overlaps class !!!
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
		NumberFormat		formatter		=	new DecimalFormat("0.0000");
// ....................................................................................................................................................................
// uses the same scoring mchanism as in the Perl version, however, here, we use all related synset glosses not just the
// subset specified in the 'relations.dat' file that comes with the Perl version. That is, we use all the information available to us from WordNet
		AdaptedLesk 	lesk = new AdaptedLesk(dict);
							//lesk.useStopList(false);			// by default, the stop list (Ted Perdersen's stop list) and the WordNet lemmatiser
							//lesk.useLemmatiser(false);		// are used to check for 'non content' words in overlaps - here, you can turn these defaults off
// ....................................................................................................................................................................
// Examples Of Use
// lesk(1) specific senses
		double leskscore = lesk.lesk("sweet", 1, "sour", 1, "a"); // "word1", sense#, "word2", sense#, "POS"
		System.out.println("specific senses");
		System.out.println("lesk:\t" + formatter.format(leskscore));
		System.out.println();

// lesk(2) all senses: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map = lesk.lesk("sweet", "sour", "a"); // "word1", "word2", "POS"
		System.out.println("all senses");
		for(String pair : map.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map.get(pair)));
		}
		System.out.println();

// max value (i.e. highest score!) : get the highest score for 2 words
		double maxvalue = lesk.max("sweet", "sour", "a"); // "word1", "word2", "POS"
		System.out.println("max value");
		System.out.println(formatter.format(maxvalue));
		System.out.println();

// lesk(3) all senses of word 1 vs. a specific sense of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map3 = lesk.lesk("sweet", "sour", 2, "a"); // "word1", "word2", sense#, "POS"
		System.out.println("all senses of word 1 vs. fixed sense of word 2");
		for(String pair : map3.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map3.get(pair)));
		}
		System.out.println();

// lesk(4) a specific sense of word 1 vs. all senses of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map4 = lesk.lesk("sweet", 1, "sour", "a"); // "word1", sense#, "word2", "POS"
		System.out.println("fixed sense of word 1 vs. all senses of word 2");
		for(String pair : map4.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map4.get(pair)));
		}

    }
}
