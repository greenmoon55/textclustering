package edu.sussex.nlp.jws;



import edu.mit.jwi.item.*;
import edu.mit.jwi.data.*;

import java.io.*;
import java.net.*;

import edu.mit.jwi.*;
import edu.mit.jwi.*;
import edu.mit.jwi.data.*;
import edu.mit.jwi.data.compare.*;
import edu.mit.jwi.data.parse.*;
import edu.mit.jwi.item.*;
import edu.mit.jwi.morph.*;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.IDictionary;

import java.util.TreeMap;
import java.text.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// David Hope, 2008, University Of Sussex
public class JWS
{
	private String rootdir		=	"";
	private String wnhome 	= 	"";
	private String icfilename = 	"";
	private URL	url			=	null;
	private	IDictionary dict 	= 	null;
 	private ArrayList<ISynsetID>	roots				=	null;
/*
	WordNet 1.6 ... 3.0
*/

	private ICFinder 												icfinder			=	null;
	private DepthFinder 											depthfinder	=	null;
	private PathFinder 												pathfinder		=	null;
	//private LCSFinder												lcsfinder			=	null;
	private JiangAndConrath 									jc 				=	null;
	private Lin 														lin					=	null;
	private Resnik													res				=	null;
	private Path														path				=	null;
	private WuAndPalmer											wup				=	null;
	private AdaptedLesk											leskO			=	null; // original, square the overlaps; no all function words overlaps allowed
	private AdaptedLeskTanimoto								lesk1			=	null;
	private AdaptedLeskTanimotoNoHyponyms			lesk2			=	null;
	private LeacockAndChodorow								lch				=	null;
	private HirstAndStOnge										hso				=	null;



// constructor 1 - use user specified IC file
	public JWS(String dir, String wnvers, String icfile)
	{
		System.out.println("Loading modules");

		wnhome 	=	dir + "/" + wnvers + "/dict";
		icfilename	=	dir + "/" + wnvers + "/WordNet-InfoContent-" + wnvers+ "/" + icfile; // user defined IC file
		if(!exists(wnhome) || !exists(icfilename))
		{
			System.out.println("your directory paths are wrong:\n" + wnhome + "\n" + icfilename);
			System.exit(1);
		}
		System.out.println("set up:");
		initialiseWordNet();

// get <roots>
		System.out.println("... finding noun and verb <roots>");
		roots = new ArrayList<ISynsetID>();
// get noun <roots>
		getRoots(POS.NOUN);
// get verb <roots>
		getRoots(POS.VERB);

		icfinder			=	new ICFinder(icfilename);
		depthfinder	=	new DepthFinder(dict, icfilename);
		pathfinder		=	new PathFinder(dict);
		jc					=	new JiangAndConrath(dict, icfinder);
		lin					=	new Lin(dict, icfinder);
		res				=	new Resnik(dict, icfinder);
		path				=	new Path(dict, roots);
		wup				=	new WuAndPalmer(dict, roots);
		leskO			=	new AdaptedLesk(dict);
		lesk1			=	new AdaptedLeskTanimoto(dict);
		lesk2			=	new AdaptedLeskTanimotoNoHyponyms(dict);
		hso				=	new HirstAndStOnge(dict);
		lch				=	new LeacockAndChodorow(dict, roots);
		System.out.println("\n\nJava WordNet::Similarity using WordNet " + dict.getVersion() + " : loaded\n\n\n");
	}

// constructor 2 - use default SemCor IC file
	public JWS(String dir, String wnvers)
	{
		System.out.println("Loading modules");

		wnhome 	=	 dir + "/dict";
		icfilename	=	 dir + "/" + "WordNet-InfoContent-" + wnvers+ "/ic-semcor.dat"; // default [ic-semcor.dat] IC file
		if(!exists(wnhome) || !exists(icfilename))
		{
			System.out.println("your directory paths are wrong:\n" + wnhome + "\n" + icfilename);
			System.exit(1);
		}
		System.out.println("set up:");
		initialiseWordNet();

// get <roots>
		System.out.println("... finding noun and verb <roots>");
		roots = new ArrayList<ISynsetID>();
// get noun <roots>
		getRoots(POS.NOUN);
// get verb <roots>
		getRoots(POS.VERB);

		icfinder			=	new ICFinder(icfilename);
		depthfinder	=	new DepthFinder(dict, icfilename);
		pathfinder		=	new PathFinder(dict);
		jc					=	new JiangAndConrath(dict, icfinder);
		lin					=	new Lin(dict, icfinder);
		res				=	new Resnik(dict, icfinder);
		path				=	new Path(dict, roots);
		wup				=	new WuAndPalmer(dict, roots);
		leskO			=	new AdaptedLesk(dict);
		lesk1			=	new AdaptedLeskTanimoto(dict);
		lesk2			=	new AdaptedLeskTanimotoNoHyponyms(dict);
		hso				=	new HirstAndStOnge(dict);
		lch				=	new LeacockAndChodorow(dict, roots);
		System.out.println("\n\nJava WordNet::Similarity using WordNet " + dict.getVersion() + " : loaded\n\n\n");
	}

// !!! put in JWS !!!
	private void getRoots(POS pos)
	{
		ISynset							synset						=	null;
		Iterator<ISynset>			iterator						=	null;
		List<ISynsetID>			hypernyms				=	null;
		List<ISynsetID>			hypernym_instances	=	null;
		iterator = dict.getSynsetIterator(pos);
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
	}



	private boolean exists(String dir)
	{
    	return (new File(dir)).exists();
	}

	private void initialiseWordNet()
	{
		try
		{
			url = new URL("file", null, wnhome);
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		if(url == null) return;
		dict = new Dictionary(url);
		try {
			dict.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

// get measure Objects .......................................................................................................................
	public JiangAndConrath getJiangAndConrath()
	{
		return ( jc );
	}
	public Lin getLin()
	{
		return ( lin );
	}
	public Resnik getResnik()
	{
		return ( res );
	}
	public Path getPath()
	{
		return ( path );
	}
	public WuAndPalmer getWuAndPalmer()
	{
		return ( wup );
	}
	public AdaptedLesk getAdaptedLesk()
	{
		return ( leskO );
	}
	public AdaptedLeskTanimoto getAdaptedLeskTanimoto()
	{
		return ( lesk1 );
	}
	public AdaptedLeskTanimotoNoHyponyms getAdaptedLeskTanimotoNoHyponyms()
	{
		return ( lesk2 );
	}
	public LeacockAndChodorow getLeacockAndChodorow()
	{
		return ( lch );
	}

	public HirstAndStOnge getHirstAndStOnge()
	{
		return ( hso );
	}
// ......................................................................................................................................................
	public IDictionary getDictionary()
	{
		return ( dict );
	}
// ......................................................................................................................................................
	//public SemCorFinder getSemCorFinder()
	//{
	//	return ( semcor );
	//}

}
