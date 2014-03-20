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


// 'AdaptedLeskTanimoto' -- !!!S-L-O-W!!!
//David Hope, 2008, University Of Sussex
public class AdaptedLeskTanimoto
{
 	private IDictionary 			dict 				=	null;	// WordNet
	private NumberFormat		formatter		=	new DecimalFormat("0.0000");// pretty up the numbers
	private Pattern					p					=	null; // word finding
	private Matcher					m					=	null;
	private WordnetStemmer	stemmer		=	null;
	private ArrayList<String>	stoplist			=	null;

// have the (Ted Pedersen) stop list here!)
	private String					list				= "a aboard about above across after against all along alongside although amid amidst among amongst an and another anti any anybody anyone anything around as astride at aught bar barring because before behind below beneath beside besides between beyond both but by circa concerning considering despite down during each either enough everybody everyone except excepting excluding few fewer following for from he her hers herself him himself his hisself i idem if ilk in including inside into it its itself like many me mine minus more most myself naught near neither nobody none nor nothing notwithstanding of off on oneself onto opposite or other otherwise our ourself ourselves outside over own past pending per plus regarding round save self several she since so some somebody someone something somewhat such suchlike sundry than that the thee theirs them themselves there they thine this thou though through throughout thyself till to tother toward towards twain under underneath unless unlike until up upon us various versus via vis-a-vis we what whatall whatever whatsoever when whereas wherewith wherewithal which whichever whichsoever while who whoever whom whomever whomso whomsoever whose whosoever with within without worth ye yet yon yonder you you-all yours yourself";



	public AdaptedLeskTanimoto(IDictionary dict)
	{
		System.out.println("... Adapted Lesk (1)");

		this.dict 		= 	dict;
		//System.out.println("WordNet " + dict.getVersion());
		p	=	Pattern.compile("[a-zA-Z-_]+"); // word finder, not perfect, but what is?!
		stemmer 		=	new WordnetStemmer(dict); // get 'base' form of a word
		stoplist			=	new ArrayList<String>();
		getStopWords();
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
// ...........................................................................................................................................
// get all the words in each 'Pointer' set, for each word(sense) -- **allow duplicates**
// these are checke against the stop list and also 'lemmatised'
		//ArrayList<String>	supergloss1	= getSuperGloss(set1);
		//ArrayList<String>	supergloss2	= getSuperGloss(set2);
		Hashtable<String, Integer>	supergloss1	= getSuperGloss(set1);
		Hashtable<String, Integer>	supergloss2	= getSuperGloss(set2);
// ...........................................................................................................................................
// create a 'basis' set for the vectors
		HashSet<String>	basis				=	new HashSet<String>();
		basis.addAll(supergloss1.keySet());
		basis.addAll(supergloss2.keySet());
// build {vectors} for each word(sense)
		Vector<Double>	v1				=	getVector(basis, supergloss1);
		Vector<Double>	v2				=	getVector(basis, supergloss2);
// get score
		lesk											=	jaccard_tanimoto(v1,v2);
		return ( lesk );
	}

	private Vector<Double> getVector(HashSet<String> basis, Hashtable<String, Integer> supergloss)
	{
		Vector<Double> vector = new Vector<Double>();
		for(String w : basis)
		{
			if(supergloss.containsKey(w))
			{
				vector.add((double)supergloss.get(w));
			}
			else
			{
				vector.add(0.0);
			}
		}
		return ( vector );
	}

// Jaccard Tanimoto methods .......................................................................................................................................
	private double dot_product(Vector<Double> v1, Vector<Double> v2)
	{
		double	dot			=	0.0;
		double	v1Value	= 	0.0;
		double	v2Value	=	0.0;

		for (int i = 0; i < v1.size(); i++)
		{
			v1Value	=	v1.get(i);
			v2Value	=	v2.get(i);
			if(v1Value> 0.0 && v2Value > 0.0)
				dot 			+=	( v1Value * v2Value );
		}
		return ( dot );
	}
	private double lengthOfVector(Vector<Double> v)
	{
		double 	length	=	0.0;
		for (int i = 0; i < v.size(); i++)
		{
			double value	=	v.get(i);
			if(value > 0.0)
				length	+= ( value * value );
		}
		if(length == 0.0)
			return ( 0.0 );
		return ( Math.sqrt(length) );
	}
	public double jaccard_tanimoto(Vector<Double> v1, Vector<Double> v2)
	{
		double	dot_product	=	dot_product(v1, v2);
		double	lengthV1		=	Math.pow(lengthOfVector(v1), 2d);
		double	lengthV2		=	Math.pow(lengthOfVector(v2), 2d);
		if(dot_product == 0.0)
			return ( 0.0 );
		if((lengthV1 + lengthV2 - ( dot_product )) == 0.0)
			return ( 0.0 );
		return ( dot_product / (lengthV1 + lengthV2 - ( dot_product )) );
	}
// Jaccard Tanimoto methods .......................................................................................................................................

// get *all* the words in *all* [glosses] related to a word(sense) -- allow duplicate words
// *no stop words*; lemmatise words to 'base' form
	private Hashtable<String, Integer> getSuperGloss(HashSet<ISynsetID> set)
	{
		Hashtable<String, Integer>	supergloss = new Hashtable<String, Integer>();
		for(ISynsetID i : set)
		{
			String gloss = dict.getSynset(i).getGloss();
			m = p.matcher(gloss);
			while(m.find())
			{
				String word = m.group().trim();
				if(!stoplist.contains(word)) // check the stoplist
				{
					List<String> baseforms = stemmer.findStems(word); // check the base forms; convert to base forms
					if(!baseforms.isEmpty())
					{
						if(baseforms.contains(word))
						{
							if(supergloss.containsKey(word))
							{
								int c = supergloss.get(word);
								c++;
								supergloss.put(word, c);
							}
							else
							{
								supergloss.put(word, 1);
							}
						}
						else
						{
							for(String bw : baseforms)
							{
								if(supergloss.containsKey(bw))
								{
										int c = supergloss.get(bw);
										c++;
										supergloss.put(bw, c);
								}
								else
								{
									supergloss.put(bw, 1);
								}
							}
						}
					}
				}
			}
		}
		return ( supergloss );
	}

// get *all* Pointers for a synset
// if a Pointer is of type: <hypernym> the get all the immediate <hyponyms> of that <hypernym>
// i.e. *all* the sub types
	private HashSet<ISynsetID> getPointers(ISynset synset)
	{
		HashSet<ISynsetID> 							pointers	=	new HashSet<ISynsetID>();
// 1. lexical
		pointers.addAll(synset.getRelatedSynsets());
// 2. semantic
		Map<IPointer, List<ISynsetID>>				map			=	synset.getRelatedMap(); // !!!
		for(IPointer p : map.keySet())
		{
// !!! get *all* the hypOnyms of a hypERnym !!! .............................................................................................
			if(p.equals(Pointer.HYPERNYM) || p.equals(Pointer.HYPERNYM_INSTANCE))
			{
				List<ISynsetID>	special	=	map.get(p);
				for(ISynsetID id : special)
				{
					pointers.addAll((dict.getSynset(id)).getRelatedSynsets(Pointer.HYPONYM));
					pointers.addAll((dict.getSynset(id)).getRelatedSynsets(Pointer.HYPONYM_INSTANCE));
				}
			}
// ..............................................................................................................................................................
			pointers.addAll(map.get(p));
		}
		return ( pointers );
	}





// get stop words ( Ted Pedersens's list)
	private void getStopWords()
	{
		String[] editor = list.split("\\s");
		for(int i = 0; i < editor.length; i++)
		{
			stoplist.add(editor[i]);
		}
 	}






// lesk(2) all senses
	public TreeMap<String, Double> lesk(String w1, String w2, String pos)
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
		if(word1 == null)
		{
			System.out.println(w1 + "(" + pos + ") not found in WordNet " + dict.getVersion());
			return(map);
		}
		if(word2 == null)
		{
			System.out.println(w2 + "(" + pos + ") not found in WordNet " + dict.getVersion());
			return(map);
		}

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
		if(word1 == null)
		{
			System.out.println(w1 + "(" + pos + ") not found in WordNet " + dict.getVersion());
			return(map);
		}
		if(word2 == null)
		{
			System.out.println(w2 + "(" + pos + ") not found in WordNet " + dict.getVersion());
			return(map);
		}
// get the lesk scores for the (sense pairs)
	 	List<IWordID> word1IDs = word1.getWordIDs(); // all senses of word 1
	 	int movingsense = 1;
	 	for(IWordID idX : word1IDs)
	 	{
			double leskscore = lesk(w1, movingsense, w2, s2, pos);
			map.put((w1 + "#" + pos + "#" + movingsense + "," + w2 + "#" + pos + "#" + s2), leskscore);
			movingsense++;
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
		if(word1 == null)
		{
			System.out.println(w1 + "(" + pos + ") not found in WordNet " + dict.getVersion());
			return(map);
		}
		if(word2 == null)
		{
			System.out.println(w2 + "(" + pos + ") not found in WordNet " + dict.getVersion());
			return(map);
		}
// get the lesk scores for the (sense pairs)
	 	List<IWordID> word2IDs = word2.getWordIDs(); // all senses of word 2
	 	int movingsense = 1;
	 	for(IWordID idX : word2IDs)
	 	{
			double leskscore = lesk(w1, s1, w2, movingsense, pos);
			map.put((w1 + "#" + pos + "#" + s1 + "," + w2 + "#" + pos + "#" + movingsense), leskscore);
			movingsense++;
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
		AdaptedLeskTanimoto lesk = new AdaptedLeskTanimoto(dict);
// ....................................................................................................................................................................
// Examples Of Use
// lesk(1) specific senses
		double leskscore = lesk.lesk("cat", 1, "dog", 2, "n"); // "word1", sense#, "word2", sense#, "POS"
		System.out.println("specific senses");
		if(leskscore != 0) // 0 is an error code i.e it means that something isn't right e.g. words are not in WordNet, wrong POS etc
		{
			System.out.println("lesk:\t" + formatter.format(leskscore));
		}
		System.out.println();

// lesk(2) all senses: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map = lesk.lesk("cat", "dog", "n"); // "word1", "word2", "POS"
		System.out.println("all senses");
		for(String pair : map.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map.get(pair)));
		}
		System.out.println();

// max value (i.e. highest score!) : get the highest score for 2 words
		double maxvalue = lesk.max("cat", "dog", "n"); // "word1", "word2", "POS"
		System.out.println("max value");
		System.out.println(formatter.format(maxvalue));
		System.out.println();

// lesk(3) all senses of word 1 vs. a specific sense of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map3 = lesk.lesk("cat", "dog", 2, "n"); // "word1", "word2", sense#, "POS"
		System.out.println("all senses of word 1 vs. fixed sense of word 2");
		for(String pair : map3.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map3.get(pair)));
		}
		System.out.println();

// lesk(4) a specific sense of word 1 vs. all senses of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map4 = lesk.lesk("cat", 1, "dog", "n"); // "word1", sense#, "word2", "POS"
		System.out.println("fixed sense of word 1 vs. all senses of word 2");
		for(String pair : map4.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map4.get(pair)));
		}

    }
}
