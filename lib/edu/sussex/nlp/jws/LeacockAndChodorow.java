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
import java.util.Collections;
import java.util.Iterator;


// 'LeacockAndChodorow': <root> version
// David Hope, 2008, University Of Sussex

// run each version of WordNet
// get the (max) <root> depths and simply write them down here!!!


public class LeacockAndChodorow
{
// depths for various versions of WordNet
// 3.0
	private final double			n30				= 19.0; // ??? add 1 or 2 to these to tally with 'node counting' in Perl version. ???
	private final double			v30				= 12.0;
// 2.1
	private final double			n21				= 18.0; // ??? add 1 or 2 to these to tally with 'node counting' in Perl version. ???
	private final double			v21				= 12.0;
// 2.0
	private final double			n20				= 17.0; // ??? add 1 or 2 to these to tally with 'node counting' in Perl version. ???
	private final double			v20				= 12.0;
// 1.7.1
	private final double			n171				= 17.0; // ??? add 1 or 2 to these to tally with 'node counting' in Perl version. ???
	private final double			v171				= 11.0;
// 1.7
	private final double			n17				= 15.0; // ??? add 1 or 2 to these to tally with 'node counting' in Perl version. ???
	private final double			v17				= 11.0;
// 1.6
	private final double			n16				= 15.0; // ??? add 1 or 2 to these to tally with 'node counting' in Perl version. ???
	private final double			v16				= 11.0;

 	private IDictionary 			dict 				=	null;

	private double					noundepth		=	0.0;
	private double					verbdepth		=	0.0;


 	private ArrayList<ISynsetID>	roots				=	null;


	public LeacockAndChodorow(IDictionary dict, ArrayList<ISynsetID> roots)
	{
		System.out.println("... LeacockAndChodorow");
		System.out.println("... calculating depths of <roots> ...");
		this.dict 		=	dict;
		this.roots		=	roots;

// get the <root>depths
		String				ver	=	dict.getVersion().toString();
		if(ver.equals("3.0"))
		{
			noundepth = n30;
			verbdepth	= v30;
		}
		if(ver.equals("2.1"))
		{
			noundepth = n21;
			verbdepth	= v21;
		}
		if(ver.equals("2.0"))
		{
			noundepth = n20;
			verbdepth	= v20;
		}
		if(ver.equals("1.7.1"))
		{
			noundepth = n171;
			verbdepth	= v171;
		}
		if(ver.equals("1.7"))
		{
			noundepth = n17;
			verbdepth	= v17;
		}
		if(ver.equals("1.6"))
		{
			noundepth = n16;
			verbdepth	= v16;
		}
// ******************************************************************************
		noundepth += 1; // **in order to align with the Perl 'node counting' way of counting depths
		verbdepth	+= 2; // !!! could be 2 -- yes it is 2 !!!
// ******************************************************************************
	}


// lch(1) = -log(length / (2 * D))
	public double lch(String w1, int s1, String w2, int s2, String pos)
	{
		double 			lch 		= 0.0;
		IIndexWord	word1	=	null;
		IIndexWord 	word2	=	null;
// get the WordNet words in *any* POS
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

 		IWordID	word2ID	=	word2.getWordIDs().get(s2 - 1); // get the right sense of word 2
 		ISynset		synset2		=	dict.getWord(word2ID).getSynset();
// ...........................................................................................................................................
// get a score

// 1. get 'length'
		double length = 0.0;
		if(synset1.equals(synset2))
		{
			length = 1.0;
		}
		else
		{
			length = getShortestPath(synset1, synset2);
		}
// 2. get the 'D' (depth) of the taxonomy
		double D = 0.0;
		if(pos.equalsIgnoreCase("n"))
		{
			D = 	noundepth;
		}
		if(pos.equalsIgnoreCase("v"))
		{
			D = 	verbdepth;
		}
		//System.out.println("length:\t" + length);
		//EasyIn.pause();
		lch = -(Math.log(length / (2.0 * D)));
		return ( lch );
	}




//++
//++
	private double getShortestPath(ISynset synsetStart, ISynset synsetEnd)
	{
		double 						shortestpath 	=	0.0;
		ArrayList<Double> 	lengths		=	new ArrayList<Double>();

		ISynsetID	start	=	synsetStart.getID();
		ISynsetID	end	=	synsetEnd.getID();

// if the synsets are the same
		if(start.equals(end))
		{
			return ( 1.0 );
		}
		else
		{
			HashSet<ISynsetID>	startset	= new HashSet<ISynsetID>();
											startset.add(start);
			TreeMap<Double, HashSet<ISynsetID>>	startpaths	= new TreeMap<Double, HashSet<ISynsetID>>();
																			startpaths.put(1.0, startset);
			getHypernyms(1.0, end, startset, startpaths);
			// .......................................................................................................................................................
			HashSet<ISynsetID>	endset	= new HashSet<ISynsetID>();
											endset.add(end);
			TreeMap<Double, HashSet<ISynsetID>>	endpaths	= new TreeMap<Double, HashSet<ISynsetID>>();
																			endpaths.put(1.0, endset);
			getHypernyms(1.0, start, endset, endpaths);


// now, get the shortest path (this needs improving - it is inefficient)
			for(Double pl_s : startpaths.keySet())
			{
					HashSet<ISynsetID> pathset_S	=	new HashSet<ISynsetID>();
													pathset_S.addAll(startpaths.get(pl_s));
					if(pathset_S.contains(end))
					{
						lengths.add(pl_s);
					}
						for(Double pl_e : endpaths.keySet())
						{
							HashSet<ISynsetID> pathset_E	=	new HashSet<ISynsetID>();
															pathset_E.addAll(endpaths.get(pl_e));

							if(pathset_E.contains(start))
							{
								lengths.add(pl_e);
							}
							pathset_E.retainAll(pathset_S);
							if(!pathset_E.isEmpty())
							{
								lengths.add((pl_s + pl_e) - 1.0); // joins
							}
						}
			} // end of for


// get the actual value that you are going to return
// [Case 1.] No path was found, thus, we have to *imply* a 'fake<root>' **but** we also want the shortest route to the 'fake'<root> too for *both*
// synsets (as they may have more than one <root>)
			if(lengths.isEmpty()) // !!! then we found *no* connection at all !!!
			{
				double last_S	=	getShortestRoot(startpaths);
				double last_E	=	getShortestRoot(endpaths);
				shortestpath =  ( last_S + last_E + 1.0 ); // + 1.0 for fake root to *artificially* join the 2, separate, hierarchies that synset 1 and synset 2 are in
			}
			else
			{
// [Case 2.] simply get the lowest value - the shortest path -. W are *not* bothered about ties here as we are not returning a trace of the path(s)
				Collections.sort(lengths);
				shortestpath = lengths.get(0);
			}
		} //  end of else

		return ( shortestpath );
	}


//++
//++
// get the <root> that has the shortest path
	private double getShortestRoot(TreeMap<Double, HashSet<ISynsetID>> paths)
	{
		double shortestroot = 0.0;

		for(Double d : paths.keySet())
		{
			HashSet<ISynsetID> set = paths.get(d);
			for(ISynsetID sid : set)
			{
				if(roots.contains(sid))
				{
					return ( d );
				}
			}
		}
		return ( shortestroot );
	}


//++
//++
	private void getHypernyms(double pathlength, ISynsetID find, HashSet<ISynsetID> current, TreeMap<Double, HashSet<ISynsetID>> paths)
	{
		pathlength++;
		HashSet<ISynsetID> 	next	=	new HashSet<ISynsetID>();	// HashSet used as we are getting both hypernyms and hypernym instances
		for(ISynsetID sid : current)
		{
			ISynset		synset 	= dict.getSynset(sid);
			next.addAll(synset.getRelatedSynsets(Pointer.HYPERNYM));
 			next.addAll(synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE));
		}
		if(!next.isEmpty())
		{
			if(next.contains(find))
			{
				paths.put(pathlength, next);
				return;
			}
			else
			{
				paths.put(pathlength, next);
				getHypernyms(pathlength, find, next, paths);
			}
		}
		return;
	}







// lch(2) all senses
	public TreeMap<String, Double> lch(String w1, String w2, String pos)
	{
		// apple#pos#sense banana#pos#sense 	pathscore
		TreeMap<String, Double>	map	=	new TreeMap<String, Double>();

		IIndexWord	word1	=	null;
		IIndexWord 	word2	=	null;
// get the WordNet words in *any* POS
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
// get the lch scores for the (sense pairs)
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
					double pathscore = lch(w1, sx, w2, sy, pos);
					map.put((w1 + "#" + pos + "#" + sx + "," + w2 + "#" + pos + "#" + sy), pathscore);
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



// lch(3) all senses of word 1 vs. a specific sense of word 2
	public TreeMap<String, Double> lch(String w1, String w2, int s2, String pos)
	{
		// apple#pos#sense banana#pos#sense 	pathscore
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
// get the lch scores for the (sense pairs)
	 		List<IWordID> word1IDs = word1.getWordIDs(); // all senses of word 1
	 		int movingsense = 1;
	 		for(IWordID idX : word1IDs)
	 		{
				double pathscore = lch(w1, movingsense, w2, s2, pos);
				map.put((w1 + "#" + pos + "#" + movingsense + "," + w2 + "#" + pos + "#" + s2), pathscore);
				movingsense++;
			}
		}
		else
		{
			return ( map );
		}
		return ( map );
	}



// lch(4) a specific sense of word 1 vs. all senses of word 2
	public TreeMap<String, Double> lch(String w1, int s1, String w2, String pos)
	{
		// (key)apple#pos#sense banana#pos#sense 	(value)pathscore
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
// get the lch scores for the (sense pairs)
	 		List<IWordID> word2IDs = word2.getWordIDs(); // all senses of word 2
	 		int movingsense = 1;
	 		for(IWordID idX : word2IDs)
	 		{
				double pathscore = lch(w1, s1, w2, movingsense, pos);
				map.put((w1 + "#" + pos + "#" + s1 + "," + w2 + "#" + pos + "#" + movingsense), pathscore);
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
		TreeMap<String, Double> pairs = lch(w1, w2, pos);
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

// <roots>
		ArrayList<ISynsetID>	roots = new ArrayList<ISynsetID>();
		ISynset							synset						=	null;
		Iterator<ISynset>			iterator						=	null;
		List<ISynsetID>			hypernyms				=	null;
		List<ISynsetID>			hypernym_instances	=	null;
		iterator = dict.getSynsetIterator(POS.NOUN);
		while(iterator.hasNext())
		{
			synset = iterator.next();
 			hypernyms				=	synset.getRelatedSynsets(Pointer.HYPERNYM);					// !!! if any of these point back (up) to synset then we have an inf. loop !!!
 			hypernym_instances	=	synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE);
 			if(hypernyms.isEmpty() && hypernym_instances.isEmpty())
 			{
				roots.add(synset.getID());
			}
		}
		iterator = dict.getSynsetIterator(POS.VERB);
		while(iterator.hasNext())
		{
			synset = iterator.next();
 			hypernyms				=	synset.getRelatedSynsets(Pointer.HYPERNYM);					// !!! if any of these point back (up) to synset then we have an inf. loop !!!
 			hypernym_instances	=	synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE);
 			if(hypernyms.isEmpty() && hypernym_instances.isEmpty())
 			{
				roots.add(synset.getID());
			}
		}

// ....................................................................................................................................................................
		LeacockAndChodorow lch = new LeacockAndChodorow(dict, roots);
// ....................................................................................................................................................................

// Examples Of Use
		NumberFormat		formatter		=	new DecimalFormat("0.0000");

/*
// lch(1) specific senses
		double pathscore = lch.lch("apple", 1, "banana", 2, "n"); // "word1", sense#, "word2", sense#, "POS"
		System.out.println("specific senses");
		if(pathscore != 0) // 0 is an error code i.e it means that something isn't right e.g. words are not in WordNet, wrong POS etc
		{
			System.out.println("lch:\t" + formatter.format(pathscore));
		}
		System.out.println();
*/

// lch(2) all senses: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map = lch.lch("adore", "like", "v"); // "word1", "word2", "POS"
		System.out.println("all senses");
		for(String pair : map.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map.get(pair)));
		}
		System.out.println();

/*
// max value (i.e. highest score!) : get the highest score for 2 words
		double maxvalue = lch.max("apple", "banana", "n"); // "word1", "word2", "POS"
		System.out.println("max value");
		System.out.println(formatter.format(maxvalue));
		System.out.println();


// lch(3) all senses of word 1 vs. a specific sense of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map3 = lch.lch("apple", "banana", 2, "n"); // "word1", "word2", sense#, "POS"
		System.out.println("all senses of word 1 vs. fixed sense of word 2");
		for(String pair : map3.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map3.get(pair)));
		}
		System.out.println();

// lch(4) a specific sense of word 1 vs. all senses of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map4 = lch.lch("apple", 1, "banana", "n"); // "word1", sense#, "word2", "POS"
		System.out.println("fixed sense of word 1 vs. all senses of word 2");
		for(String pair : map4.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map4.get(pair)));
		}
*/
    }
}
