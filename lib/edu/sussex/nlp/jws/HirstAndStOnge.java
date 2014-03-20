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
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.*;

// 'HirstAndStOnge': computes the semantic relatedness of word senses according to the method as described by Patwardhan et al. which adapts the original
// measure as described by Hirst & St Onge
// I have 'fixed' the POS to be the same for both words, - this is not strictly required here and I may change it in future
// David Hope, 2008

/*
Lexical Relations (as defined in St-Onge's thesis p. 9) that are in WordNet 3.0

Pointer type					St-Onge direction

ALSO_SEE						horizontal
ANTONYM						horizontal
ATTRIBUTE					horizontal
CAUSE							down
ENTAILMENT					down
HOLONYM_MEMBER		down
HOLONYM_PART				down
HOLONYM_SUBSTANCE 	down
HYPERNYM						up
HYPERNYM_INSTANCE	up
HYPONYM						down
HYPONYM_INSTANCE		down
MERONYM_MEMBER		up
MERONYM_PART			up
MERONYM_SUBSTANCE 	up
PERTAINYM					horizontal
SIMILAR_TO					horizontal

? for some reason Patwardhan et al. mention a CONTAINS relation ? (maybe it's in WordNet 1.7?)
? St-Onge also calls the PERTAINYM relation 'PERTAIN', I assume this is the same relation ?
........................................................................................

So, I 'm just going to follow St-Onge and *not* use these (WordNet 3.0) relations

DERIVATIONALLY_RELATED
DERIVED_FROM_ADJ
PARTICIPLE
REGION
REGION_MEMBER
TOPIC
TOPIC_MEMBER
USAGE
USAGE_MEMBER
VERB_GROUP
*/

public class HirstAndStOnge
{

// relations
	private ArrayList<IPointer>	up				=	null;
	private ArrayList<IPointer>	down			=	null;
	private ArrayList<IPointer>	horizontal		=	null;
	private ArrayList<IPointer>	all					=	null;

 	private IDictionary 				dict 				=	null;
 	private String[]						editor			=	null;
	private NumberFormat			formatter		=	new DecimalFormat("0.0000");
	private RelatedSynsets			pointers		=	null;

	private String[]						cut1				=	null;
	private String[]						cut2				=	null;




// 'Constants' used in the equation ......................................................................................................................................
	private final double	C									=	8.0; // as per Perl version
	private final double	k									=	1.0; // as per Perl version
// .....................................................................................................................................................................................


	public HirstAndStOnge(IDictionary dict)
	{
		System.out.println("... HirstAndStOnge");
		this.dict 		= 	dict;
		pointers 		= new RelatedSynsets(dict);



// up
		up				=	new ArrayList<IPointer>();
		up.add(Pointer.HYPERNYM);
		up.add(Pointer.HYPERNYM_INSTANCE);
		up.add(Pointer.MERONYM_MEMBER);
		up.add(Pointer.MERONYM_PART);
		up.add(Pointer.MERONYM_SUBSTANCE);
// down
		down			=	new ArrayList<IPointer>();
		down.add(Pointer.CAUSE);
		down.add(Pointer.ENTAILMENT);
		down.add(Pointer.HOLONYM_MEMBER);
		down.add(Pointer.HOLONYM_PART);
		down.add(Pointer.HOLONYM_SUBSTANCE);
		down.add(Pointer.HYPONYM);
		down.add(Pointer.HYPONYM_INSTANCE);
// horizontal
		horizontal		=	new ArrayList<IPointer>();
		horizontal.add(Pointer.ALSO_SEE);
		horizontal.add(Pointer.ANTONYM);
		horizontal.add(Pointer.ATTRIBUTE);
		horizontal.add(Pointer.PERTAINYM);
		horizontal.add(Pointer.SIMILAR_TO);
// all
		all				=	new ArrayList<IPointer>();
		all.addAll(up);
		all.addAll(down);
		all.addAll(horizontal);
	}





// Case 2.2 'Strong' relation: {s1} --- horizontal --- {s2}
	private boolean hasHorizontalRelation(ISynset x, ISynset y)
	{
		ISynsetID										tofind	=	y.getID();
		//Map<IPointer, List<ISynsetID>> 	map 		=	x.getRelatedMap();
		Hashtable<ISynsetID, IPointer> 		map		=	pointers.getAllRelatedSynsetsAndTheirTypes(x);
		//List<ISynsetID> 							list		=	null;
		for(ISynsetID sid : map.keySet())
		{
			if(sid.equals(tofind))
			{
				IPointer p = map.get(sid);
				if(horizontal.contains(p)) 	// only look for horizontal relations
				{
				//	list = map.get(p);
				//	if(list.contains(tofind))	// if we find {s2} amongst the horzontal relations for {s1}, then, OK, return true
				//	{
						return ( true );
				//	}
				}
			}
		}
		return ( false );
	}

// Case 2.3 'Strong', is word 1/word2 a word of the *compound* word2/word1? - if so, is there **any** type of relation
// between the 2 {synsets}?
	private boolean isCompound(IIndexWord w1, IIndexWord w2)
	{
		String lemma1 = 	w1.getLemma();
		String lemma2= 	w2.getLemma();
// look for _- lemma and lemma_-  as a substring
		if(lemma1.length() > lemma2.length())
		{
    		if((lemma1.indexOf("_" + lemma2) > 0) || (lemma1.indexOf(lemma2 + "_") > 0)) // underscore
    		{
				return ( true );
			}
    		if((lemma1.indexOf("-" + lemma2) > 0) || (lemma1.indexOf(lemma2 + "-") > 0)) // hyphen
    		{
				return ( true );
			}
    		if((lemma1.indexOf(" " + lemma2) > 0) || (lemma1.indexOf(lemma2 + " ") > 0)) // whitespace
    		{
				return ( true );
			}
		}
		if(lemma1.length() < lemma2.length())
		{
    		if((lemma2.indexOf("_" + lemma1) > 0) || (lemma2.indexOf(lemma1 + "_") > 0))
    		{
				return ( true );
			}
    		if((lemma2.indexOf("-" + lemma1) > 0) || (lemma2.indexOf(lemma1 + "-") > 0))
    		{
				return ( true );
			}
    		if((lemma2.indexOf(" " + lemma1) > 0) || (lemma2.indexOf(lemma1 + " ") > 0))
    		{
				return ( true );
			}
		}
		return ( false );
	}
	private boolean hasAnyRelation(ISynset x, ISynset y)
	{
		ISynsetID										tofind	=	y.getID();
		Hashtable<ISynsetID, IPointer> 		map		=	pointers.getAllRelatedSynsetsAndTheirTypes(x);
		for(ISynsetID sid : map.keySet())
		{
			if(sid.equals(tofind))
			{
				return ( true );
			}
		}
		return ( false );
	}

// hso(1) -- the workhorse method from which all else follows
	public double hso(String w1, int s1, String w2, int s2, String pos)
	{
		double 			hso 		= 0.0;
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
			//System.out.println(w1 + "(" + pos + ") not found in WordNet " + dict.getVersion());
			return(0); // 0 is an error code
		}
		if(word2 == null)
		{
			//System.out.println(w2 + "(" + pos + ") not found in WordNet " + dict.getVersion());
			return(0); // 0 is an error code
		}
// [error check]: check the sense numbers are not greater than the true number of senses in WordNet
 		List<IWordID> word1IDs = word1.getWordIDs();
 		List<IWordID> word2IDs = word2.getWordIDs();
		if(s1 >  word1IDs.size())
		{
			//System.out.println(w1 + " sense: " + s1 + " not found in WordNet " + dict.getVersion());
			return(0); // 0 is an error code
		}
		if(s2 > word2IDs.size())
		{
			//System.out.println(w2 + " sense: " + s2 + " not found in WordNet " + dict.getVersion());
			return(0); // 0 is an error code
		}


// if we got here, then we can do something
//System.out.println("\n\n\n.....................................................\n" + w1 + " " + s1 + " " + w2 + " " + s2 + "\n.....................................................\n");
// [Case 1.] 'Extra-Strong' Relation ('Identity')
// OK, first, let's check for the possibility of an 'EXTRA-STRONG' relation (which, for some reason, the Perl version does not do).
// This is the case of 'identity' : that is, where the two input words are exactly the same words in the same POS e.g. say, "apple"(n) and "apple"(n).
// In order to align with the Perl version's scoring mechanism, we will actually give a 'STRONG' score to an 'EXTRA-STRONG' relation, that is: 2*C
// (note that we must check that the 2 words(pos)+senses are in WordNet first (as done above) - as this is a measure over WordNet words, not just any words)

		if(word1.equals(word2)) // (note that this is a score for the *senses* of the words *not* the words themselves, which is as it should be)
		{
			//System.out.println(word1.getLemma() + " equals " + word2.getLemma() + " = full score of 16");
			return ( 2.0 * C );
		}
// else
// ...........................................................................................................................................
// get the {synsets}
 		IWordID	word1ID	=	word1.getWordIDs().get(s1 - 1); // get the right sense of word 1
 		ISynset		synset1		=	dict.getWord(word1ID).getSynset();
 		IWordID	word2ID	=	word2.getWordIDs().get(s2 - 1); // get the right sense of word 2
 		ISynset		synset2		=	dict.getWord(word2ID).getSynset();
// ...........................................................................................................................................
// [Case 2.] 'Strong' Relations
// 2.1 if the 2 words(pos) senses point to the same {synset}
		if(synset1.equals(synset2))
		{
			//System.out.println(synset1 + " equals " + synset2 + " = full score of 16");
			return ( 2.0 * C );
		}
// 2.2 if {synset1} --- horizontal --- {synset2} i.e. if there is a horizontal relation that connects the 2 {synsets}
		if(hasHorizontalRelation(synset1, synset2))
		{
			//System.out.println(synset1 + " has a horizontal relation with " + synset2 + " = full score of 16");
			return ( 2.0 * C );
		}
// 2.3 is one word a compound word, and the other a word of this compound word?
		if(isCompound(word1, word2))
		{
			//System.out.println(word1.getLemma() + " and " + word2.getLemma() + " have a compound relation");
			if(hasAnyRelation(synset1, synset2)) // is there *any* relation at all between the 2 {synsets}?
			{
				//System.out.println("... and  they also have a relation in common");
				return ( 2.0 * C );
			}
		}
// [Case 3.] 'Medium-Strong' relations
// if we get here, then we will have to work out the score using: C - pathlength - ( k * changesinpathdirection )
// and only get a score fof the 'allowable' paths (as per Hirst & St-Onge's heuristics)
		HashSet<ArrayList<Object>> 					oldpaths	=	new HashSet<ArrayList<Object>>();
		ArrayList<Object>										initialpath	=	new ArrayList<Object>();
																		initialpath.add(synset1.getID()); // !!! the root, starting point of all paths
																		oldpaths.add(initialpath);
// get paths of lengths (edges) 1 - 2 - 3 - 4 - 5 - 6 nodes
//                                             1   2    3   4   5   edges
		ISynsetID													tofind				=	synset2.getID();
		ArrayList<ArrayList<IPointer>>					candidatepaths	=	new ArrayList<ArrayList<IPointer>>();
// SAFE VERSION .....................................................................................................................................
// get the Candidate Paths -- up to 5
		HashSet<ArrayList<Object>> paths1	= oldpaths;
		for(ArrayList<Object> path : paths1)
		{
			if(path.contains(tofind))
				candidatepaths.add(getClean(path));
		}
		HashSet<ArrayList<Object>> paths2	= pathfinder(paths1);
		for(ArrayList<Object> path : paths2)
		{
			if(path.contains(tofind))
				candidatepaths.add(getClean(path));
		}
		HashSet<ArrayList<Object>> paths3	= pathfinder(paths2);
		for(ArrayList<Object> path : paths3)
		{
			if(path.contains(tofind))
				candidatepaths.add(getClean(path));
		}
		HashSet<ArrayList<Object>> paths4	= pathfinder(paths3);
		for(ArrayList<Object> path : paths4)
		{
			if(path.contains(tofind))
				candidatepaths.add(getClean(path));
		}
		HashSet<ArrayList<Object>> paths5	= pathfinder(paths4);
		for(ArrayList<Object> path : paths5)
		{
			if(path.contains(tofind))
				candidatepaths.add(getClean(path));
		}
		HashSet<ArrayList<Object>> paths6	= pathfinder(paths5);
		for(ArrayList<Object> path : paths6)
		{
			if(path.contains(tofind))
				candidatepaths.add(getClean(path));
		}
// .......................................................................................................................................................
// [Case 4.] 'Weak' (yep, that's what they call it in the paper) relation
		if(candidatepaths.isEmpty())
		{
			//System.out.println(word1.getLemma() + " " + s1 + " and " + word2.getLemma() + " " + s2 +  " have no path at all that is valid = score = 0.0");
			return ( 0.0 );
		}
		else
		{
			// [Case 3.] 'Medium Strong' (the best out of the lot)
			double max = 0.0;
			for(ArrayList<IPointer> candidate : candidatepaths)
			{
				double dir				=	allowable(candidate);
				//EasyIn.pause("path changes:\t" + dir);
				if(dir != -1) // i.e. if the path is 'allowable' (-1 is an error code to indicate a *non* allowable path)
				{
					double pathlength 	=	(candidate.size());
					double s 				=	C - pathlength - (k * dir);
					//EasyIn.pause("path length:\t" + pathlength);
					//System.out.println("score:\t" + s);
					if(s > max)
					{
						max = s;
					}
				}
			}
			hso = max;
		}
		return ( hso );
	}


// utility: get a path that consists of just Pointers (i.e. not inc. synsets)
	private ArrayList<IPointer> getClean(ArrayList<Object> path)
	{
		ArrayList<IPointer> clean = new ArrayList<IPointer>();
		for(int i = 1; i < path.size(); i+= 2)
		{
			clean.add((IPointer)path.get(i));
		}
		return ( clean );
	}



// is the path 'allowable' (acc. to Hirst & St-Onge's rules)?
	private double allowable(ArrayList<IPointer> candidate)
	{

//EasyIn.pause("--" + candidate);
		//String udh = "";

		IPointer x				=	null;
		IPointer y				=	null;
		IPointer z				=	null; // special case Rule 2 exception
		boolean	special		=	false;

		int		moves				=	0;

		for(int i = 0; i < candidate.size() - 1; i++)
		{
// get a Pointer pair
			x = (IPointer)candidate.get(i);
			y = (IPointer)candidate.get(i + 1);

// 1. first, check the Pointer types
			if(!all.contains(x) || !all.contains(y)) // if either of the Pointer types, 'x' and 'y', are *not* OK
			{
				//System.out.println("No: pointer types are not valid");
				return ( - 1 ); // then we return an error code (-1) i.e. the path is not 'allowable' as it contains types that are not on St-Onge's list of relations
			}
// 3. [Rule 1.] .............................................................................................
// no other direction may precede an (up) move
			if(!up.contains(x) && up.contains(y))
			{
				//System.out.println("No: other move precedes (up)");
				return ( -1 ); // *non* allowable path, the whole path is non allowable
			}






// 2. if x and y are exactly the same type e.g. [hypernym,hypernym] ...
			if(x.equals(y))
			{
				//if(up.contains(x))
				//	udh += ("[U]--[U] ");
				//if(down.contains(x))
				//	udh += ("[D]--[D] ");
				//if(horizontal.contains(x))
				//	udh += ("[H]--[H] ");
				continue; // ... then, keep truckin': we add nothing to the 'moves' count and go on to the next pair as we are moving in the same direction
			}


// 4. 'allowable' moves
// 4.1
			if(up.contains(x) && down.contains(y))
			{
				//udh += ("[U]--[D] ");
				moves++;
			}
// 4.2
			if(down.contains(x) && horizontal.contains(y))
			{
				//udh += ("[D]--[H] ");
				moves++;
			}
// 4.3
			if(horizontal.contains(x) && down.contains(y))
			{
				//udh += ("[H]--[D] ");
				moves++;
			}

// 4.4 **includes the special case**
			if(up.contains(x) && horizontal.contains(y))
			{
				// [up, horizontal, down] is the *special case* (it is the only sub path that is allowed to have > 1 change in direction)
				if((i+2) < candidate.size()) // if we can get a third Pointer type
				{
					z = (IPointer)candidate.get(i + 2);
					if(down.contains(z))
					{
						special = true;
						moves += 2;
						//udh += ("*[U]--[H]--[D]* ");
					}
				}
				else
				{
					//udh += ("[U]--[H] ");
					moves++;
				}
			}
		} // end of loop; end of path checking
		//EasyIn.pause(udh);
// OK paths .................................................................................................................................................................................
		if(moves == 0) // OK if we simply went in the same direction right the way through
		{
			//EasyIn.pause("--" + udh);
			return ( 0 );
		}
		if(special && moves == 2) // OK if we made just one legitimate special move and no others
		{
			return ( 2.0 );
		}
		if(!special && moves == 1) // OK if we made just one legitimate move and no others
		{
			return ( 1.0 );
		}
// NOT OK paths .......................................................................................................................................................................
		if(special && moves > 2) //  NOT OK if we made a special move and then others
		{
			return ( -1.0 );
		}
		if(!special && moves > 1) // NOT OK
		{
			return ( -1.0 );
		}
		return ( -1 ); // default *non* allowable path indicator
	}


// {s1}--{s2}--{s3}--{s4}--{s5}--{s6} as per Perl version i.e. 'edge counting' not 'node counting'
//        1        2       3       4        5
	private HashSet<ArrayList<Object>> pathfinder(HashSet<ArrayList<Object>> oldpaths)
	{
// resusable variables ....................................................................................
		ISynsetID 								end			=	null;
		ISynset 									synset 		=	null;
		Hashtable<ISynsetID, IPointer>	relations	=	null;
// ................................................................................................................
		HashSet<ArrayList<Object>> next = new HashSet<ArrayList<Object>>();

		for(ArrayList<Object> o : oldpaths)
		{
			end 			=	(ISynsetID)o.get(o.size() - 1); // get the last {synset} in a currently existing path
			synset 		=	dict.getSynset(end);

			relations	=	pointers.getAllRelatedSynsetsAndTheirTypes(synset); // get the Pointers for this {synset}

			for(ISynsetID candidate : relations.keySet())
			{
				IPointer p = relations.get(candidate);
				if(all.contains(p)) // if a Pointer type for this {synset}  is a valid type (acc. to St-Onge)
				{
					ArrayList<Object> n = new ArrayList<Object>(); //build a *new* path
												n.addAll(o); // add the old path
												n.add(p); // add the new Pointer type
												n.add(candidate); // add the new {synset}
					next.add(n);
				}
			}
		}
// !!! no recursion !!! (as we know the limit is 5 nodes)
		return ( next );
	}








// hso(2) all senses
	public TreeMap<String, Double> hso(String w1, String w2, String pos)
	{
		// apple#pos#sense banana#pos#sense 	hsoscore
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
		if(word1 != null && word2 != null)
		{
// get the hso scores for the (sense pairs)
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
					double hsoscore = hso(w1, sx, w2, sy, pos);
					map.put((w1 + "#" + pos + "#" + sx + "," + w2 + "#" + pos + "#" + sy), hsoscore);
					sy++;
				}
				sx++;
			}
		}
		else
		{
			//System.out.println(w1 + " and/or " + w2 + " in POS " + pos +  " do not exist in WordNet " + dict.getVersion());
			return ( map ); // i.e. return 'nothing' but *not* null
		}
		return ( map );
	}



// hso(3) all senses of word 1 vs. a specific sense of word 2
	public TreeMap<String, Double> hso(String w1, String w2, int s2, String pos)
	{
		// apple#pos#sense banana#pos#sense 	hsoscore
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
		if(word1 != null && word2 != null)
		{
// get the hso scores for the (sense pairs)
	 		List<IWordID> word1IDs = word1.getWordIDs(); // all senses of word 1
	 		int movingsense = 1;
	 		for(IWordID idX : word1IDs)
	 		{
				double hsoscore = hso(w1, movingsense, w2, s2, pos);
				map.put((w1 + "#" + pos + "#" + movingsense + "," + w2 + "#" + pos + "#" + s2), hsoscore);
				movingsense++;
			}
		}
		else
		{
			//System.out.println(w1 + " and/or " + w2 + " in POS " + pos +  " do not exist in WordNet " + dict.getVersion());
			return ( map );
		}
		return ( map );
	}

// hso(4) a specific sense of word 1 vs. all senses of word 2
	public TreeMap<String, Double> hso(String w1, int s1, String w2, String pos)
	{
		// (key)apple#pos#sense banana#pos#sense 	(value)hsoscore
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
		if(word1 != null && word2 != null)
		{
// get the hso scores for the (sense pairs)
		 	List<IWordID> word2IDs = word2.getWordIDs(); // all senses of word 2
		 	int movingsense = 1;
		 	for(IWordID idX : word2IDs)
		 	{
				double hsoscore = hso(w1, s1, w2, movingsense, pos);
				map.put((w1 + "#" + pos + "#" + s1 + "," + w2 + "#" + pos + "#" + movingsense), hsoscore);
				movingsense++;
			}
		}
		else
		{
			//System.out.println(w1 + " and/or " + w2 + " in POS " + pos +  " do not exist in WordNet " + dict.getVersion());
			return ( map );
		}
		return ( map );
	}



// get max score for all sense pairs
	public double max(String w1, String w2, String pos)
	{
		double max = 0.0;
		TreeMap<String, Double> pairs = hso(w1, w2, pos);
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
		HirstAndStOnge hso = new HirstAndStOnge(dict);
// ....................................................................................................................................................................


// Examples Of Use

// hso(2) all senses: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map = hso.hso("like", "cotton", "v"); // "word1", "word2", "POS"
		//TreeMap<String,Double> map = hso.hso("school", "private_school", "n"); // "word1", "word2", "POS"
		System.out.println("all senses");
		for(String pair : map.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map.get(pair)));
		}
		System.out.println();


/*
// max value (i.e. highest score!) : get the highest score for 2 words
		double maxvalue = hso.max("eat", "consume8", "v"); // "word1", "word2", "POS"
		System.out.println("max value");
		System.out.println(formatter.format(maxvalue));
		System.out.println();


// hso(3) all senses of word 1 vs. a specific sense of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map3 = hso.hso("eat", "consume8", 2, "v"); // "word1", "word2", sense#, "POS"
		System.out.println("all senses of word 1 vs. fixed sense of word 2");
		for(String pair : map3.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map3.get(pair)));
		}
		System.out.println();

// hso(4) a specific sense of word 1 vs. all senses of word 2: a value (score) of 0 is an error code for a pair
		TreeMap<String,Double> map4 = hso.hso("eat8", 1, "consume", "v"); // "word1", sense#, "word2", "POS"
		System.out.println("fixed sense of word 1 vs. all senses of word 2");
		for(String pair : map4.keySet())
		{
			System.out.println(pair + "\t" + formatter.format(map4.get(pair)));
		}
*/
    }
}
