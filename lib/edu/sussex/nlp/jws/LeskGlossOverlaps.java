//package edu.sussex.nlp.jws;


import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import java.net.*;
import edu.mit.jwi.Dictionary;
import java.util.List;
import java.util.ArrayList;
import edu.mit.jwi.morph.WordnetStemmer;
import java.util.regex.*;

/*
	'LeskGlossOverlaps'

		Takes in 2 glosses (2 Strings)
		Finds the overlaps
		Checks that an overlap contains content words
		Scores each overlap by squaring the length of the overlap (as per Perl version).
		Returns the score for all overlaps found.

		One can turn the stoplist on/off.
		One can turn the lemmatiser on/off

	David Hope, 2008, University Of Sussex
*/

public class LeskGlossOverlaps
{

	private boolean 					usestoplist 		=	true;	// default: 'on'
	private boolean 					uselemmatiser	=	true;	// default: 'on'
	private IDictionary				dict					=	null;
	private WordnetStemmer	stemmer			=	null;
	private String					list					= "a aboard about above across after against all along alongside although amid amidst among amongst an and another anti any anybody anyone anything around as astride at aught bar barring because before behind below beneath beside besides between beyond both but by circa concerning considering despite down during each either enough everybody everyone except excepting excluding few fewer following for from he her hers herself him himself his hisself i idem if ilk in including inside into it its itself like many me mine minus more most myself naught near neither nobody none nor nothing notwithstanding of off on oneself onto opposite or other otherwise our ourself ourselves outside over own past pending per plus regarding round save self several she since so some somebody someone something somewhat such suchlike sundry than that the thee theirs them themselves there they thine this thou though through throughout thyself till to tother toward towards twain under underneath unless unlike until up upon us various versus via vis-a-vis we what whatall whatever whatsoever when whereas wherewith wherewithal which whichever whichsoever while who whoever whom whomever whomso whomsoever whose whosoever with within without worth ye yet yon yonder you you-all yours yourself";
	private ArrayList<String>	stoplist				=	null;
	private Pattern					p						=	null;
	private Matcher					m						=	null;

	public LeskGlossOverlaps(IDictionary dict)
	{
		this.dict = dict;
		p					= Pattern.compile("\\b[a-zA-Z0-9-']+\\b"); // very simple 'non punctuation'
		stemmer 		=	new WordnetStemmer(dict);
		stoplist			=	new ArrayList<String>();
		getStopWords();
	}
// utility : turn stoplist off/on
	public void useStopList(boolean use)
	{
		usestoplist = use;
	}
// utility : turn lemmatiser off/on
	public void useLemmatiser(boolean use)
	{
		uselemmatiser = use;
	}
// get stop words (Ted Pedersens's list)
	private void getStopWords()
	{
		String[] editor = list.split("\\s");
		for(int i = 0; i < editor.length; i++)
			stoplist.add(editor[i]);
 	}
// tokenise the gloss i.e. get 'words' in a gloss
	private ArrayList<String> getWords(String gloss)
	{
		ArrayList<String> 	words 	=	new ArrayList<String>();
		String[]					editor	=	gloss.toLowerCase().split("\\s+");
		String					word		=	"";
		for(int i = 0; i < editor.length; i++)
		{
			word = editor[i].trim();
			m = p.matcher(word);
			if(m.find())
				words.add(m.group());
		}
		//EasyIn.pause("words:\t" + words);
		return ( words );
	}
// check/do not check for stop words and lemmas
	private boolean containsContentWords(List<String> overlap)
	{
// case 1.
		if(!usestoplist && !uselemmatiser) // i.e. no restrictions, so just return true
			return ( true);
// case 2.
		if(usestoplist && uselemmatiser) // both
		{
			for(String w : overlap)
			{
				List<String> lemma = stemmer.findStems(w);
				if(!lemma.isEmpty() && !stoplist.contains(w)) // is *not* on stop list and *is WordNet* word = OK, we have, at least, 1 content word
					return ( true );
			}
		}
// case 3.
		if(usestoplist && !uselemmatiser) // just stop list
		{
			for(String w : overlap)
			{
				if(!stoplist.contains(w))
					return ( true );
			}
		}
// case 4.
		if(!usestoplist && uselemmatiser) // just lemmatiser
		{
			for(String w : overlap)
			{
				List<String> lemma = stemmer.findStems(w);
				if(!lemma.isEmpty())
					return ( true );
			}
		}
		return ( false );
	}

// build candidate overlaps (largest to smallest)
	private ArrayList<List<String>> builder(ArrayList<String> words)
	{
		ArrayList<List<String>> build = new ArrayList<List<String>>();
		int k = 1;
		for(int i = 0; i < words.size(); i++)
		{
			for(int j = 0; j < words.size(); j++)
			{
				if(j+k <= words.size())
				{
					build.add(0, words.subList(j, (j+k))); // put the longest length candidates at the front of the list
				}
			}
			k++;
		}
		return ( build );
	}

// get the actual overlaps and score them
	public double overlap(String x, String y)
	{
		double totalscore = 0.0;
// initialise
		ArrayList<String> 			xlist		=	getWords(x);
		ArrayList<String> 			ylist		=	getWords(y);
// 1. if x gloss is the same as y gloss
		if(x.equals(y))
		{
			if(containsContentWords(xlist)) // check that the x gloss does have at least 1 content word
			{
				totalscore = (Math.pow((double)xlist.size(), 2.0));
				return ( totalscore );
			}
		}
// 2. else build the 'candidate' overlaps
		ArrayList<List<String>> xbuild 	= (builder(xlist));
		ArrayList<List<String>> ybuild 	= (builder(ylist));
		ArrayList<List<String>>	xy		=	new ArrayList<List<String>>();
											xy.addAll(xbuild);
											xy.retainAll(ybuild);
// 3. if there are no overlaps then retur 0
		if(xy.isEmpty())
			return ( 0.0 );
// 4. build useable, non 'tied into Lists' lists
		ArrayList<String> 			xxlist		=	getWords(x);
		ArrayList<String> 			yylist		=	getWords(y);
		ArrayList<List<String>>	store			=	new ArrayList<List<String>>();
// 5. find the overlaps (largest to smallest)
		while(!xy.isEmpty())
		{
				List<String> match =  xy.remove(0);
				ArrayList<String> matched = new ArrayList<String>(); // !!! match !!!
				matched.addAll(match);
				store.add(matched);
				for(String s : matched) // remove the match from the 2 input glosses
				{
					xxlist.remove(s);
					yylist.remove(s);
				}
				// rebuild
				xbuild 	= (builder(xxlist)); // and start again
				ybuild 	= (builder(yylist));
				// update xy
				xy.clear();
				xy.addAll(xbuild);
				xy.retainAll(ybuild);
		}
// 6. check that an overlap does haveat least 1 content word
//System.out.println(store);

		for(List<String> o : store)
		{
			if(containsContentWords(o)) // if content word
			{
				totalscore += (Math.pow((double)o.size(), 2.0)); // squre the length of the overlap and add to the total score
			}
		}
		return ( totalscore );
	}



	public static void main(String[] args)
	{
// Set up Java WordNet::Similarity ....................................................................................................................................
		String	dir	=	"C:/Program Files/WordNet";
		JWS		ws	=	new JWS(dir, "3.0");
// ...................................................................................................................................................................................
		String gloss1 = "aqua lung --all cats are grey";
		String gloss2 = "aqua-lung -all 0.22 cat's 54 are grey.";
		LeskGlossOverlaps lgo = new LeskGlossOverlaps(ws.getDictionary());
		// the default is to use the stop list (Ted Pedersen's stop list) and to use the WordNet lemmatiser in order to check for content words in overlaps
		// but you can turn them off here if you wish
									lgo.useStopList(false);
									lgo.useLemmatiser(false);
		// get the overlap score between 2 glosses, we use the [relations.dat] file to tell us which glosses we are allowed to compare (as per Perl version)
		double overlapscore = lgo.overlap(gloss1, gloss2);
		System.out.println(overlapscore);
	}
}
