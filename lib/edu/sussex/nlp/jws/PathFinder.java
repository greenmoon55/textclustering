//package edu.sussex.nlp.jws;



import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import java.net.*;
import edu.mit.jwi.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;
import java.util.TreeMap;



public class PathFinder
{

/**
 * PathFinder finds the shortest path in WordNet between two synsets (other methods to be implemented see: the equivalent Perl module).
 * In theory, you can have any combination of synsets / parts of speech as we are using all the Pointer types and thus, somewhere in the
 * space we should find a connection
 *
	David Hope, 2008, University Of Sussex

 */

 	private IDictionary dict = null;

	public PathFinder(IDictionary dict)
	{
		System.out.println("... PathFinder");

		this.dict = dict;
	}

	private double getShortestPath(ISynset a, ISynset z)
	{
		double sp 		=	Double.MAX_VALUE;;
		HashSet<ISynsetID>	A	= new HashSet<ISynsetID>();
		A.add(a.getID());
		TreeMap<Integer, HashSet<ISynsetID>>	AA	= new TreeMap<Integer, HashSet<ISynsetID>>();
		getHypernyms(0, A, AA);

		HashSet<ISynsetID>	Z	= new HashSet<ISynsetID>();
		Z.add(z.getID());
		TreeMap<Integer, HashSet<ISynsetID>>	ZZ	= new TreeMap<Integer, HashSet<ISynsetID>>();
		getHypernyms(0, Z, ZZ);

		for(Integer i : AA.keySet())
		{
			HashSet<ISynsetID> setA = AA.get(i);
			for(Integer j : ZZ.keySet())
			{
				HashSet<ISynsetID> setZ = ZZ.get(j);
				HashSet<ISynsetID>	join	=	new HashSet<ISynsetID>();
				join.addAll(setA);
				join.retainAll(setZ);
				if(!join.isEmpty())
				{
					if((i+j) < sp)
					{
						sp = (i+j);
					}
				}
			}
		}
		return ( sp + 1.0 );
	}

	private void getHypernyms(int pathlength, HashSet<ISynsetID> synsets, TreeMap<Integer, HashSet<ISynsetID>> paths)
	{
		pathlength++;
		HashSet<ISynsetID> 	hypernyms	=	new HashSet<ISynsetID>();
		for(ISynsetID s : synsets)
		{
			ISynset		synset 	= dict.getSynset(s);
			hypernyms.addAll(synset.getRelatedSynsets(Pointer.HYPERNYM)); 					// get the <hypernyms> if there are any
 			hypernyms.addAll(synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE));	// get the <hypernyms> (instances) if there are any
		}
		if(!hypernyms.isEmpty())
		{
			paths.put(pathlength, hypernyms);
			getHypernyms(pathlength, hypernyms, paths);
		}
		return;
	}


// test it
    public static void main(String[] args)
    {
// this would be all set up in the 'main' Class ........................................................................................................
		String wnhome 	= "C:/Program Files/WordNet/" + 3.0 + "/dict";
		String icfile		= "C:/Program Files/WordNet/" + 3.0 + "/WordNet-InfoContent-" + 3.0 + "/ic-semcor.dat";
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
// ....................................................................................................................................................................
		PathFinder pathfinder = new PathFinder(dict);
		ISynset	synset1		=	dict.getSynset(new SynsetID(7739125, POS.NOUN)); // apple s1
		ISynset	synset2		=	dict.getSynset(new SynsetID(7753592, POS.NOUN)); // banana s2
		//System.out.println("Get LCS(s)");
		//HashSet<ISynsetID>	lcs = pathfinder.getLCSbyPath(synset1, synset2, "n");
		//System.out.println("lcs(s):\t" + lcs);
		//System.out.println("\n\n\nshortest path (any POS)");
		double sp = pathfinder.getShortestPath(synset1, synset2);
		System.out.println("The shortest path length from:\n" + synset1 + "\n...to...\n" + synset2 + "\nis:\t" + sp);
    }
}
