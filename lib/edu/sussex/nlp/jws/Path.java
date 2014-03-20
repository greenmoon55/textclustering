//package edu.sussex.nlp.jws;



import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import java.net.*;
import edu.mit.jwi.Dictionary;
import java.text.*;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.*;
import java.util.TreeMap;
import java.util.Collections;


/*
	'Path'

	Finds the shortest path between two synsets : 'joins' *not* 'meets', otherwise you get odd semantic simlarity e.g.

NOT OK

		1.<fruit>      2.<tree>
			      \        /
			      <apple>

OK
			       <fruit>
			    /             \
		1. <apple> 2. <banana>

Both path lengths = 3 as we are node counting not edge counting ( as per Perl version)

	-- 'fake'<root> is always used: this is the default for the Perl version
	-- no path tracing (do we really need it?)
	-- copes with compound words
	-- inf. loop patch applied for WordNet 3.0 (applied directly to the verb data file for WordNet 3.0)

	David Hope, 2008, University Of Sussex
*/


public class Path
{
 	private IDictionary 				dict 				=	null;
 	private ArrayList<ISynsetID>	roots				=	null;
	private CompoundWords		compounds	=	null;
 	private Pattern						cp					=	null;
 	private Matcher						cm				=	null;

	public Path(IDictionary dict, ArrayList<ISynsetID> roots)
	{
		System.out.println("... Path");
		this.dict 			=	dict;
		this.roots			=	roots;
// LOCAL VERSION OD <ROOTS>
		//System.out.println("... finding noun and verb <roots>");
		//roots = new ArrayList<ISynsetID>();
// get noun <roots>
		//getRoots(POS.NOUN);
// get verb <roots>
		//getRoots(POS.VERB);

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
// ..................................................................................................................................................................

// path(1) = 1 / length of shortest path between 2 synsets
	public double path(String w1, int s1, String w2, int s2, String pos)
	{
// [error check]: only nouns and verbs
		if(!pos.equalsIgnoreCase("n") && !pos.equalsIgnoreCase("v"))
		{
			System.out.println("error: Path is applicable to nouns (n) and verbs (v) only");
			return (0);
		}
		double 			path 		= 0.0;
// get the WordNet words
		IIndexWord	word1	=	getWordNetWord(w1,pos);
		IIndexWord 	word2	=	getWordNetWord(w2,pos);
// [error check]: check the words exist in WordNet
		if(word1 == null || word2 == null)
		{
			System.out.println("error: WordNet does not contain word(s):\t(" + w1 + " , " + w2 +") in POS:" + pos);
			return(0);
		}
// [error check]: check the sense numbers are not greater than the true number of senses in WordNet
		if(s1 >  word1.getWordIDs().size() || s2 > word2.getWordIDs().size())
		{
			System.out.println("error: WordNet does not contain sense(s):\t(" + s1 + " , " + s2 +")");
			return(0);
		}
// ...........................................................................................................................................
// get the {synsets}
 		IWordID	word1ID	=	word1.getWordIDs().get(s1 - 1); // get the right sense of word 1 (Java index offset i.e. 1 == 0)
 		ISynset		synset1		=	dict.getWord(word1ID).getSynset();

 		IWordID	word2ID	=	word2.getWordIDs().get(s2 - 1); // get the right sense of word 2
 		ISynset		synset2		=	dict.getWord(word2ID).getSynset();
// ...........................................................................................................................................
// get a score
// get the reciprocal of the length of the shortest path between the 2 synsets
		double geodesic = getShortestPath(synset1, synset2);
		path = 	( 1.0 / geodesic );

		return ( path );
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








// path(2) all senses
	public TreeMap<String, Double> path(String w1, String w2, String pos)
	{
		TreeMap<String, Double>	map	=	new TreeMap<String, Double>();
// get the WordNet words
		IIndexWord	word1	=	getWordNetWord(w1, pos);
		IIndexWord 	word2	=	getWordNetWord(w2, pos);
// [error check]: check the words exist in WordNet
		if(word1 != null && word2 != null)
		{
// get the path scores for the (sense pairs)
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
					double pathscore = path(w1, sx, w2, sy, pos);
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



// path(3) all senses of word 1 vs. a specific sense of word 2
	public TreeMap<String, Double> path(String w1, String w2, int s2, String pos)
	{
		// apple#pos#sense banana#pos#sense 	pathscore
		TreeMap<String, Double>	map	=	new TreeMap<String, Double>();
// get the WordNet words
		IIndexWord	word1	=	getWordNetWord(w1, pos);
		IIndexWord 	word2	=	getWordNetWord(w2, pos);
// [error check]: check the words exist in WordNet
		if(word1 != null && word2 != null)
		{
// get the path scores for the (sense pairs)
		 	List<IWordID> word1IDs = word1.getWordIDs(); // all senses of word 1
		 	int movingsense = 1;
		 	for(IWordID idX : word1IDs)
		 	{
				double pathscore = path(w1, movingsense, w2, s2, pos);
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



// path(4) a specific sense of word 1 vs. all senses of word 2
	public TreeMap<String, Double> path(String w1, int s1, String w2, String pos)
	{
		// (key)apple#pos#sense banana#pos#sense 	(value)pathscore
		TreeMap<String, Double>	map	=	new TreeMap<String, Double>();
// get the WordNet words
		IIndexWord	word1	=	getWordNetWord(w1, pos);
		IIndexWord 	word2	=	getWordNetWord(w2, pos);
// [error check]: check the words exist in WordNet
		if(word1 != null && word2 != null)
		{
// get the path scores for the (sense pairs)
	 		List<IWordID> word2IDs = word2.getWordIDs(); // all senses of word 2
	 		int movingsense = 1;
	 		for(IWordID idX : word2IDs)
	 		{
				double pathscore = path(w1, s1, w2, movingsense, pos);
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
		double max = 0;
		TreeMap<String, Double> pairs = path(w1, w2, pos);
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
// 1. setup
		String vers = "3.0";
		String wnhome 	= "C:/Program Files/WordNet/" + vers + "/dict";
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








		Path path = new Path(dict, roots);


// 2. use
/*
// path(1) specific senses
		double pathscore = path.path("apple", 1, "banana", 2, "n"); // "word1", sense#, "word2", sense#, "POS"
		System.out.println("specific senses");
		System.out.println("path:\t" + formatter.format(pathscore));
		System.out.println();
*/
// path(2) all senses: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map = path.path("cat", "dog", "n"); // "word1", "word2", "POS"
		System.out.println("all senses");
		for(String pair : map.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map.get(pair)));
		}
		System.out.println();
/*
// max value (i.e. highest score!) : get the highest score for 2 words
		double maxvalue = path.max("apple", "banana", "n"); // "word1", "word2", "POS"
		System.out.println("max value");
		System.out.println(formatter.format(maxvalue));
		System.out.println();


// path(3) all senses of word 1 vs. a specific sense of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map3 = path.path("apple", "banana", 2, "n"); // "word1", "word2", sense#, "POS"
		System.out.println("all senses of word 1 vs. fixed sense of word 2");
		for(String pair : map3.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map3.get(pair)));
		}
		System.out.println();

// path(4) a specific sense of word 1 vs. all senses of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map4 = path.path("apple", 1, "banana", "n"); // "word1", sense#, "word2", "POS"
		System.out.println("fixed sense of word 1 vs. all senses of word 2");
		for(String pair : map4.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map4.get(pair)));
		}
*/
    }
}
