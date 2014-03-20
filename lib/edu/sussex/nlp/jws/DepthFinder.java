//package edu.sussex.nlp.jws;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import java.net.*;
import edu.mit.jwi.Dictionary;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.Hashtable;
import java.util.List;
import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.Map;



/*
'DepthFinder'
David Hope, 2008, University Of Sussex
*/

public class DepthFinder
{

	private	IDictionary				dict 					=	null;
	private	String 						icfile 				=	"";
	private	ArrayList<Integer>	nounroots			=	null;
	private	ArrayList<Integer>	verbroots			=	null;


// (Perl)'initalise'
	public DepthFinder(IDictionary dict, String icfile)
	{
		System.out.println("... DepthFinder");

		this.dict 	=	dict;		// 'point' at the specific version of WordNet that we are using
		this.icfile 	=	icfile;	// 'point' at the specific Information Content file that we are applying
		nounroots	=	new ArrayList<Integer>();
		verbroots	=	new ArrayList<Integer>();
		getRoots();
	}

// Given two input synsets, finds the least common subsumer (LCS) of them.
// If there are multiple candidates for the LCS (due to multiple inheritance in WordNet), the LCS with the
// greatest depth is chosen (i.e., the candidate whose shortest path to the root is the longest).
	public HashSet<ISynsetID> getLCSbyDepth(ISynset synset1, ISynset synset2, String pos)
	{
		HashSet<ISynsetID> lcs = new HashSet<ISynsetID>();

		if(synset1.equals(synset2))
		{
			HashSet<ISynsetID> identity = new HashSet<ISynsetID>();
			identity.add(synset1.getID());
			return ( identity );
		}
// !!! could be <roots>, in which case there is no subsumer !!!
		double d1 = getSynsetDepth(synset1, pos);
		double d2 = getSynsetDepth(synset2, pos);
		if(d1 == 0.0 && d2 == 0.0)
		{
			return(lcs); // !!! return empty set !!!
		}
// !!! *1* of them could be a <root>, in which case there is no subsumer !!!
		//double d1 = getSynsetDepth(synset1, pos);
		//double d2 = getSynsetDepth(synset2, pos);
		if(d1 == 0.0 || d2 == 0.0)
		{
			if(d1 == 0.0)
			{
				lcs.add(synset1.getID());
			}
			if(d2 == 0.0)
			{
				lcs.add(synset2.getID());
			}
			return(lcs); // !!! return !!!
		}
		TreeMap<Integer, HashSet<ISynsetID>> map = new TreeMap<Integer, HashSet<ISynsetID>>();
// synset 1 <hypernyms>
		HashSet<ISynsetID> s1 = new HashSet<ISynsetID>(); s1.add(synset1.getID());
		HashSet<ISynsetID> h1 = new HashSet<ISynsetID>();
		getHypernyms(s1, h1); // i.e. fill 'h1' with <hypernyms> of synset1
// synset 2 <hypernyms>
		HashSet<ISynsetID> s2 = new HashSet<ISynsetID>(); s2.add(synset2.getID());
		HashSet<ISynsetID> h2 = new HashSet<ISynsetID>();
		getHypernyms(s2, h2); // i.e. fill 'h2' with <hypernyms> of synset2
		h1.retainAll(h2);
		//System.out.println(h1);
		for(ISynsetID h : h1)
		{
			//System.out.println(dict.getSynset(h));
			//System.out.println(h + "\t\t\t" + getSynsetDepth(h.getOffset(), pos));
			TreeMap<Integer, HashSet<ISynsetID>>  set = getSynsetDepth(h.getOffset(), pos);
			for(Integer i : set.keySet())
			{

				HashSet<ISynsetID> subset = set.get(i);
				//EasyIn.pause(h + "\t" + i + "\t<" + subset + ">");
				if(map.containsKey(i))
				{
					HashSet<ISynsetID> store = map.get(i);
					store.add(h);
					map.put(i, store);
				}
				else
				{
					HashSet<ISynsetID> store = new HashSet<ISynsetID>();
					store.add(h);
					map.put(i, store);
				}
			}
		}
		int key = map.lastKey();
		lcs = map.get(key);
		return (lcs);
	}


// getHypernyms for both synsets
// get joins -- i.e. the intersection of the hypernyms
// get the join(s) with greatest depth in WordNet using getSynsetDepth()
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


/**
 * Returns the maximum depth of a noun or verb <root> synset - as denoted by offset and pos.
 * The returned value is an int denoting the maximum depth of this 'is-a' hierarchy, starting at <root>
 *
 * @param offset  WordNet synset offset number
 * @param pos part of speech identifier for the synset
 * @return int the maximum depth of the <root> synset
 */
	public int getTaxonomyDepth(int offset, String pos)
	{
		int maxdepth = 0;
		ISynsetID					synset 				= null;
		ArrayList<Integer>	homehierarchies = null;
		if(pos.equalsIgnoreCase("n"))
		{
			homehierarchies = nounroots;
			synset					=	new SynsetID(offset, POS.NOUN);
		}
		if(pos.equalsIgnoreCase("v"))
		{
			homehierarchies = verbroots;
			synset					=	new SynsetID(offset, POS.VERB);
		}
		if(!homehierarchies.contains(offset))
		{
			return ( -1 ); // !!! -1 is an error code!!!
		}
// initial <root> starting point in an 'is-a' hierarchy
		HashSet<ISynsetID> root	=	new HashSet<ISynsetID>();
		root.add(synset);
		maxdepth = treediver(root);
		return ( maxdepth );
	}


	private int treediver(HashSet<ISynsetID> set)
	{
		int depth = 0;
		ArrayList<ISynsetID>	queue 	=	new ArrayList<ISynsetID>();
											queue.addAll(set);
		boolean search = true;
		while(search)
		{
			HashSet<ISynsetID> 	hyponyms	=	new HashSet<ISynsetID>();
			while(!queue.isEmpty())
			{
				ISynset		synset 	= dict.getSynset(queue.remove(0));
				hyponyms.addAll(synset.getRelatedSynsets(Pointer.HYPONYM)); 					// get the <hyponyms> if there are any
 				hyponyms.addAll(synset.getRelatedSynsets(Pointer.HYPONYM_INSTANCE));	// get the <hyponyms> (instances) if there are any
			}
			if(hyponyms.isEmpty())
				search = false;
			else
			{
				depth++;
				queue.addAll(hyponyms);
			}
		}
		return ( depth );
	}


	public double getSynsetDepth(String word, int senseno, String pos)
	{
		IIndexWord	word1	=	null;
// get the WordNet words in *any* POS
		ArrayList<Integer>	homehierarchies = null;

		if(pos.equalsIgnoreCase("n"))
		{
			word1 = dict.getIndexWord(word, POS.NOUN);
			homehierarchies	=	nounroots;

		}
		if(pos.equalsIgnoreCase("v"))
		{
 			word1 = dict.getIndexWord(word, POS.VERB);
			homehierarchies	=	verbroots;
		}
// ...........................................................................................................................................
 		IWordID	word1ID	=	word1.getWordIDs().get(senseno - 1); // get the right sense of word 1
 		ISynset		synset1		=	dict.getWord(word1ID).getSynset();
// ...........................................................................................................................................
// get a score
		TreeMap<Integer, HashSet<ISynsetID>> 	depths	=	new TreeMap<Integer, HashSet<ISynsetID>>();
		HashSet<ISynsetID> 								synsets	=	new HashSet<ISynsetID>();
		synsets.add(synset1.getID());
		treecreeper(0, synsets, depths, homehierarchies);
		if(depths.isEmpty())
		{
			return ( 0.0 ); // i.e. is <root>, nothing 'above' it
		}
		return ( (double)(depths.lastKey() + 2.0) ); // ??? node counting, so have to include start and end node
	}

	public double getSynsetDepth(ISynset synsetX, String pos)
	{
		//IIndexWord	word1	=	null;
// get the WordNet words in *any* POS
		ArrayList<Integer>	homehierarchies = null;

		if(pos.equalsIgnoreCase("n"))
		{
			homehierarchies	=	nounroots;

		}
		if(pos.equalsIgnoreCase("v"))
		{
			homehierarchies	=	verbroots;
		}
// ...........................................................................................................................................
 		//IWordID	word1ID	=	word1.getWordIDs().get(senseno - 1); // get the right sense of word 1
 		//ISynset		synset1		=	dict.getWord(word1ID).getSynset();
// ...........................................................................................................................................
// get a score
		TreeMap<Integer, HashSet<ISynsetID>> 	depths	=	new TreeMap<Integer, HashSet<ISynsetID>>();
		HashSet<ISynsetID> 								synsets	=	new HashSet<ISynsetID>();
		synsets.add(synsetX.getID());
		treecreeper(0, synsets, depths, homehierarchies);
		if(depths.isEmpty())
		{
			return ( 0.0 ); // i.e. is <root>, nothing 'above' it
		}
		return ( (double)(depths.lastKey() + 2.0) ); // ??? node counting, so have to include start and end node
	}


/**
 * Returns the depth(s) of the synset - as denoted by offset and pos.
 * The return value is a TreeMap with key = depth; value(s) =  {roots}
 *
 * @param offset  WordNet synset offset number
 * @param pos part of speech identifier for the synset
 * @return TreeMap<Integer, HashSet<ISynsetID>>
 */
	public TreeMap<Integer, HashSet<ISynsetID>> getSynsetDepth(int offset, String pos)
	{
		TreeMap<Integer, HashSet<ISynsetID>> 	depths	=	new TreeMap<Integer, HashSet<ISynsetID>>();
		ISynsetID					synset 				= null;
		ArrayList<Integer>	homehierarchies = null;
		if(pos.equalsIgnoreCase("n"))
		{
			synset					=	new SynsetID(offset, POS.NOUN);
			homehierarchies	=	nounroots;
		}
		if(pos.equalsIgnoreCase("v"))
		{
			synset					=	new SynsetID(offset, POS.VERB);
			homehierarchies	=	verbroots;
		}
// [error check]
		if(synset == null)
			return ( null );
// ..................................................................................................................................................
		int															depth	=	0;
		HashSet<ISynsetID> 								synsets	=	new HashSet<ISynsetID>();
		synsets.add(synset);
		treecreeper(depth, synsets, depths, homehierarchies);
		return ( depths );
	}

/**
 * Uses recurson to get the depth of the input synset (in 'getSynsetDepth()') from a <root>
 * A synset may have multiple parents, thus we returneach possible depth and 'home hierarchy' <root>
 * Thus, we may have the same <root> at different depths in the WordNet hierarchy
 */
	private void treecreeper(int depth, HashSet<ISynsetID> synsets, TreeMap<Integer, HashSet<ISynsetID>> depths, ArrayList<Integer> roots)
	{
		depth++;
		ISynset												synset 			=	null;
		HashSet<ISynsetID> 						hypernyms	=	new HashSet<ISynsetID>(); // next 'level'(inverse of 'depth')
 		for(ISynsetID s : synsets)
 		{
			synset = dict.getSynset(s);
 			hypernyms.addAll(synset.getRelatedSynsets(Pointer.HYPERNYM)); 					// get the <hypernyms>
 			hypernyms.addAll(synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE)); 	// get the <hypernyms> (instances)
		}

		if(!hypernyms.isEmpty())
		{
			for(ISynsetID h : hypernyms)
			{
				int offset = h.getOffset();
				if(roots.contains(offset))
				{
					if(depths.containsKey(depth))
					{
						HashSet<ISynsetID> deep = depths.get(depth);
						deep.add(h);
						depths.put(depth, deep);
					}
					else
					{
						HashSet<ISynsetID> deep = new HashSet<ISynsetID>();
						deep.add(h);
						depths.put(depth, deep);
					}
				}
			}
			treecreeper(depth, hypernyms, depths, roots);
		}
		return;
	}

// get the longest path from a <root> to the synset
// there may be more than 1 <root> with the same depth value, but we don't care about that as we just
// want the maximum value possible
	public int getSynsetMaximumDepth(int offset, String pos)
	{
		TreeMap<Integer, HashSet<ISynsetID>> 	depths	=	getSynsetDepth(offset, pos);
		return ( depths.lastKey() );
	}
// get the <root(s)> of the longest path(s) from a <root> to the synset
// there may be more than 1 <root> with the same depth value, so we return all of the <roots> -- typically, this
// will just be 1 <root>
	public HashSet<ISynsetID> getSynsetMaximumRoots(int offset, String pos)
	{
		TreeMap<Integer, HashSet<ISynsetID>> 	depths	=	getSynsetDepth(offset, pos);
		Map.Entry<Integer, HashSet<ISynsetID>> maxroot = depths.lastEntry();
		return ( maxroot.getValue() );
	}


/**
 * Get the noun and verb <roots> (i.e. 'the highest' synsets in WordNet) from the particular 'icfile' (Information Content) that you are applying.
 * Store a noun <root>: a synset offset number of type Integer in nounroots: an ArrayList<Integer> defined in the constructor of this class.
 * Store a verb <root>: a synset offset number of type Integer in verbroots: an ArrayList<Integer> defined in the constructor of this class.
 *
 * An example line in an 'icfile', showing a noun <root>: 1740n 128767 ROOT
 */
	private void getRoots()
	{
		Pattern	pn	=	Pattern.compile("[0-9]+n [0-9]+ ROOT");	// find noun <root>
		Pattern	pv	=	Pattern.compile("[0-9]+v [0-9]+ ROOT");	// find verb <root>
		Matcher	m		=	null;
		String	root	=	"";
    	try
    	{
        	BufferedReader in = new BufferedReader(new FileReader(icfile));
        	String line;
        	while ((line = in.readLine()) != null)
        	{
// nouns
				m = pn.matcher(line);
				if(m.matches())
				{
            		root 	= 	(line.split("\\s")[0]).split("n")[0]; // !!! double split !!!
            		nounroots.add(Integer.parseInt(root));
				}
// verbs
				m = pv.matcher(line);
				if(m.matches())
				{
            		root 	= 	(line.split("\\s")[0]).split("v")[0];	// !!! double split !!!
            		verbroots.add(Integer.parseInt(root));
            	}
        	}
        in.close();
    	}
    	catch (IOException e){e.printStackTrace();}
	}

// method: 'getTaxonomies'(offset, pos)' returns a list of the roots of the taxonomies to which the synset identified by offset and pos belongs.
	public HashSet<ISynsetID> getTaxonomies(int offset, String pos)
	{
		HashSet<ISynsetID>	taxonomies = new HashSet<ISynsetID>();
		TreeMap<Integer, HashSet<ISynsetID>> 	depths			= getSynsetDepth(offset, pos);
		for(Integer i : depths.keySet())
		{
			taxonomies.addAll(depths.get(i));
		}
		return ( taxonomies );
	}


// test
    public static void main(String[] args)
    {
		String vers = "3.0";
		String wnhome 	= "C:/Program Files/WordNet/" + vers + "/dict";
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
		ICFinder icfinder = new ICFinder(icfile);
		DepthFinder	depthfinder	=	new DepthFinder(dict, icfile);
		double 			depth1			= depthfinder.getSynsetDepth("apple", 2, "n");
		System.out.println(depth1);
	}
}
