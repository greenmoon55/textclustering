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

// 'JWSRandom': generates random numbers for (sense pairs), as per Perl version
// 1. You can set the 'max' score (upper limit) for the scores
// 2. You can store the scores such that each (sense pair) will always receive the same score upon further 'calls' for that (sense pair) value
// 3. The default is *no storing* of scores, with the score value being in the range [0...1] (as per Perl version)
// David Hope, 2008, University Of Sussex


public class JWSRandom
{
 	private IDictionary 						dict 				=	null;
 	private boolean								fixed				=	false;
	private java.util.Random					rand				=	null;
	private Hashtable<String,Double>	store				=	null;
	private String								key				=	"";
	private double								max				=	0.0;
// C1.
// default constructor
	public JWSRandom(IDictionary dict)
	{
		this.dict 		= 	dict;
		rand = new java.util.Random();
// !!! paranoid error check !!!
		fixed = false;
		max	=	0;
		System.out.println("... JWSRandom");
	}
// C2.
// if 'fixed' = true then we store the random number for a specificsense pair
	public JWSRandom(IDictionary dict, boolean fixed)
	{
		this.dict 		= 	dict;
		this.fixed 		=	fixed;
		rand = new java.util.Random();
		if(fixed)
			store = new Hashtable<String, Double>();
// !!! paranoid error check !!!
		max = 0;
		System.out.println("... JWSRandom");
	}
// C3.
// if 'fixed' = true then we store the random number for a specificsense pair
	public JWSRandom(IDictionary dict, boolean fixed, double max)
	{
		this.dict 		= 	dict;
		this.fixed 		=	fixed;
		this.max		=	max;
		rand = new java.util.Random();
		if(fixed)
			store = new Hashtable<String, Double>();
		System.out.println("... JWSRandom");
	}


// random(1) : compute random numbers for specific sense pairs -- all POS allowed here
	public double random(String w1, int s1, String w2, int s2, String pos)
	{
// .....................................................................................................................................................................................................
// first, let's check that the words (any POS), and their sense numbers are actually in WordNet
			IIndexWord	word1	=	null;
			IIndexWord 	word2	=	null;
// get the WordNet words
			if(pos.equalsIgnoreCase("n"))	{word1 = dict.getIndexWord(w1, POS.NOUN); word2 = dict.getIndexWord(w2, POS.NOUN);	}
			if(pos.equalsIgnoreCase("v"))	{word1 = dict.getIndexWord(w1, POS.VERB); word2 = dict.getIndexWord(w2, POS.VERB);	}
			if(pos.equalsIgnoreCase("a"))	{word1 = dict.getIndexWord(w1, POS.ADJECTIVE); word2 = dict.getIndexWord(w2, POS.ADJECTIVE);	}
			if(pos.equalsIgnoreCase("r"))	{word1 = dict.getIndexWord(w1, POS.ADVERB); word2 = dict.getIndexWord(w2, POS.ADVERB);	}
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
// .....................................................................................................................................................................................................
// second, if we got here, let's get a score
			double	random = 0.0;
			key		=	(w1 + "#" + pos + "#" + s1 + "," + w2 + "#" + pos + "#" + s2);
			if(fixed) // if we are storing scores
			{
				if(store.containsKey(key))
				{
					return (store.get(key));
				}
				else
				{
					random = rand.nextDouble();
					if(max > 0) // if we are setting an upper limit on the scores
					{
						random = ( max / (1.0 / random) );
					}
					store.put(key, random);
					return(random);
				}
			}
			if(max > 0) // if we are setting an upper limit on the scores, but we do not want to store the score
			{
				return ( max / (1.0 / rand.nextDouble()) );
			}
			return ( rand.nextDouble() );// standard (deafult) scoring
	}

// random(2) all senses
	public TreeMap<String, Double> random(String w1, String w2, String pos)
	{
		TreeMap<String, Double>	map	=	new TreeMap<String, Double>();
// first, let's check that the words (any POS), and their sense numbers are actually in WordNet
			IIndexWord	word1	=	null;
			IIndexWord 	word2	=	null;
// get the WordNet words
			if(pos.equalsIgnoreCase("n"))	{word1 = dict.getIndexWord(w1, POS.NOUN); word2 = dict.getIndexWord(w2, POS.NOUN);	}
			if(pos.equalsIgnoreCase("v"))	{word1 = dict.getIndexWord(w1, POS.VERB); word2 = dict.getIndexWord(w2, POS.VERB);	}
			if(pos.equalsIgnoreCase("a"))	{word1 = dict.getIndexWord(w1, POS.ADJECTIVE); word2 = dict.getIndexWord(w2, POS.ADJECTIVE);	}
			if(pos.equalsIgnoreCase("r"))	{word1 = dict.getIndexWord(w1, POS.ADVERB); word2 = dict.getIndexWord(w2, POS.ADVERB);	}
// [error check]: check the words exist in WordNet
		if(word1 != null && word2 != null)
		{
// get the random scores for the (sense pairs)
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
					double randomscore = random(w1, sx, w2, sy, pos);
					map.put((w1 + "#" + pos + "#" + sx + "," + w2 + "#" + pos + "#" + sy), randomscore);
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



// random(3) all senses of word 1 vs. a specific sense of word 2
	public TreeMap<String, Double> random(String w1, String w2, int s2, String pos)
	{
		// apple#pos#sense banana#pos#sense 	randomscore
		TreeMap<String, Double>	map	=	new TreeMap<String, Double>();

// first, let's check that the words (any POS), and their sense numbers are actually in WordNet
			IIndexWord	word1	=	null;
			IIndexWord 	word2	=	null;
// get the WordNet words
			if(pos.equalsIgnoreCase("n"))	{word1 = dict.getIndexWord(w1, POS.NOUN); word2 = dict.getIndexWord(w2, POS.NOUN);	}
			if(pos.equalsIgnoreCase("v"))	{word1 = dict.getIndexWord(w1, POS.VERB); word2 = dict.getIndexWord(w2, POS.VERB);	}
			if(pos.equalsIgnoreCase("a"))	{word1 = dict.getIndexWord(w1, POS.ADJECTIVE); word2 = dict.getIndexWord(w2, POS.ADJECTIVE);	}
			if(pos.equalsIgnoreCase("r"))	{word1 = dict.getIndexWord(w1, POS.ADVERB); word2 = dict.getIndexWord(w2, POS.ADVERB);	}
// [error check]: check the words exist in WordNet
		if(word1 != null && word2 != null)
		{
// get the random scores for the (sense pairs)
	 		List<IWordID> word1IDs = word1.getWordIDs(); // all senses of word 1
	 		int movingsense = 1;
	 		for(IWordID idX : word1IDs)
	 		{
				double randomscore = random(w1, movingsense, w2, s2, pos);
				map.put((w1 + "#" + pos + "#" + movingsense + "," + w2 + "#" + pos + "#" + s2), randomscore);
				movingsense++;
			}
		}
		else
		{
			return ( map );
		}
		return ( map );
	}

// random(4) a specific sense of word 1 vs. all senses of word 2
	public TreeMap<String, Double> random(String w1, int s1, String w2, String pos)
	{
		// (key)apple#pos#sense banana#pos#sense 	(value)randomscore
		TreeMap<String, Double>	map	=	new TreeMap<String, Double>();
// first, let's check that the words (any POS), and their sense numbers are actually in WordNet
			IIndexWord	word1	=	null;
			IIndexWord 	word2	=	null;
// get the WordNet words
			if(pos.equalsIgnoreCase("n"))	{word1 = dict.getIndexWord(w1, POS.NOUN); word2 = dict.getIndexWord(w2, POS.NOUN);	}
			if(pos.equalsIgnoreCase("v"))	{word1 = dict.getIndexWord(w1, POS.VERB); word2 = dict.getIndexWord(w2, POS.VERB);	}
			if(pos.equalsIgnoreCase("a"))	{word1 = dict.getIndexWord(w1, POS.ADJECTIVE); word2 = dict.getIndexWord(w2, POS.ADJECTIVE);	}
			if(pos.equalsIgnoreCase("r"))	{word1 = dict.getIndexWord(w1, POS.ADVERB); word2 = dict.getIndexWord(w2, POS.ADVERB);	}
// [error check]: check the words exist in WordNet
		if(word1 != null && word2 != null)
		{
// get the random scores for the (sense pairs)
	 		List<IWordID> word2IDs = word2.getWordIDs(); // all senses of word 2
	 		int movingsense = 1;
	 		for(IWordID idX : word2IDs)
	 		{
				double randomscore = random(w1, s1, w2, movingsense, pos);
				map.put((w1 + "#" + pos + "#" + s1 + "," + w2 + "#" + pos + "#" + movingsense), randomscore);
				movingsense++;
			}
		}
		else
		{
			return ( map );
		}
		return ( map );
	}



// get max score for all sense pairs
	public double max(String w1, String w2, String pos)
	{
		double max = 0.0;
		TreeMap<String, Double> pairs = random(w1, w2, pos);
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
		//JWSRandom random = new JWSRandom(dict); 					//  default, completely random
		//JWSRandom random = new JWSRandom(dict, true); 			//	true = store the randomly generated numbers (default) for a sense pair
		JWSRandom random = new JWSRandom(dict, true, 16.0); 	// set the 'max' score (upper limit)
// ....................................................................................................................................................................


// Examples Of Use

// random(1) specific senses
		double randomscore = random.random("apple", 1, "banana", 1, "n"); // "word1", sense#, "word2", sense#, "POS"
		System.out.println("specific senses");
		if(randomscore != 0) // 0 is an error code i.e it means that something isn't right e.g. words are not in WordNet, wrong POS etc
		{
			System.out.println("random:\t" + formatter.format(randomscore));
		}
		System.out.println();

// random(2) all senses: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map = random.random("apple", "banana", "n"); // "word1", "word2", "POS"
		System.out.println("all senses");
		for(String pair : map.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map.get(pair)));
		}
		System.out.println();

// max value (i.e. highest score!) : get the highest score for 2 words
		double maxvalue = random.max("apple", "banana", "n"); // "word1", "word2", "POS"
		System.out.println("max value");
		System.out.println(formatter.format(maxvalue));
		System.out.println();

// random(3) all senses of word 1 vs. a specific sense of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map3 = random.random("apple", "banana", 2, "n"); // "word1", "word2", sense#, "POS"
		System.out.println("all senses of word 1 vs. fixed sense of word 2");
		for(String pair : map3.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map3.get(pair)));
		}
		System.out.println();

// random(4) a specific sense of word 1 vs. all senses of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map4 = random.random("apple", 1, "banana", "n"); // "word1", sense#, "word2", "POS"
		System.out.println("fixed sense of word 1 vs. all senses of word 2");
		for(String pair : map4.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map4.get(pair)));
		}

    }
}
