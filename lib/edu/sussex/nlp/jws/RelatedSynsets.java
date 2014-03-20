//package edu.sussex.nlp.jws;


import edu.mit.jwi.item.*;
import edu.mit.jwi.data.*;
import java.io.*;
import java.net.*;
import edu.mit.jwi.*;
import edu.mit.jwi.data.compare.*;
import edu.mit.jwi.data.parse.*;
import edu.mit.jwi.morph.*;
import edu.mit.jwi.item.IIndexWord;
//import java.util.*;
import edu.mit.jwi.IDictionary;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;


// 'RelatedSynsets': JWI has an odd way of defining which other synsets are related to a synset, so we have to go round the houses a bit in order to get
// *all* the synsets that really are connected to a synset by looking at the  'related' synsets AND the Words in the synset.
// David Hope, 2008, University Of Sussex
public class RelatedSynsets
{
	private IDictionary 				dict		=	null;

	public RelatedSynsets(IDictionary dict)
	{
		this.dict		=	dict;
 	}

// 'Utility'
// 1. just get the *all* the synset (IDs) that are connected to a word/synset (i.e. no Pointer types, just the IDs)
// That is, we get both semantic *and*  lexical synsets ( we have to use the Word objects in the synset to get the lexical synsets)
// i.e. we sort of have to go round the back door
// vers. 1 for a "word"
	public HashSet<ISynsetID> getAllRelatedSynsetsNoTypes(String wordIN, int sense, String pos)
	{
		HashSet<ISynsetID> set = new HashSet<ISynsetID>();
		set.addAll((getAllRelatedSynsetsAndTheirTypes(wordIN, sense, pos)).keySet());
		return ( set );
	}

// vers. 2 for a {synset}
	public HashSet<ISynsetID> getAllRelatedSynsetsNoTypes(ISynset synset)
	{
		HashSet<ISynsetID> set = new HashSet<ISynsetID>();
		set.addAll((getAllRelatedSynsetsAndTheirTypes(synset)).keySet());
		return ( set );
	}
// ...........................................................................................................................................


// 2. get the synsets (IDs) and their Pointer type - gets *both* types of related synsets 1.  semantic e.g. <hypernyms> and 2. lexical e.g. antonyms
// vers. 1 for a "word"
	public Hashtable<ISynsetID, IPointer> getAllRelatedSynsetsAndTheirTypes(String wordIN, int sense, String pos)
	{
		Hashtable<ISynsetID, IPointer>		set		=	new Hashtable<ISynsetID, IPointer>();
		IIndexWord 				idxWord	= null;
		if(pos.equalsIgnoreCase("n")){idxWord	=	dict.getIndexWord(wordIN, POS.NOUN);}
		if(pos.equalsIgnoreCase("v")){idxWord	=	dict.getIndexWord(wordIN, POS.VERB);}
		if(pos.equalsIgnoreCase("a")){idxWord	=	dict.getIndexWord(wordIN, POS.ADJECTIVE);}
		if(pos.equalsIgnoreCase("r")){idxWord	=	dict.getIndexWord(wordIN, POS.ADVERB);}
		if(idxWord == null)
			return ( set );
// ............................................................................................................
		 IWordID 				wordID		=	idxWord.getWordIDs().get(sense-1);
		 IWord 					word 		=	dict.getWord(wordID);
		 ISynset 				synset 		=	word.getSynset();
		//EasyIn.pause(wordIN + "\t" + sense + "\t" + pos + "\t" + synset);

// 1. for {synsets}
		Map<IPointer,  List<ISynsetID>>	semantic_synset_pointers 		=	synset.getRelatedMap();
		for(IPointer p : semantic_synset_pointers.keySet())
		{
			List<ISynsetID> list = semantic_synset_pointers.get(p);
			for(ISynsetID i : list)
			{
 				set.put(i, p);
			}
		}
// 2. for Word objects (in the synset)
		Map<IPointer,  List<IWordID>>	semantic_word_pointers 		=	word.getRelatedMap();
		for(IPointer p : semantic_word_pointers.keySet())
		{
			List<IWordID> list = semantic_word_pointers.get(p);
			for(IWordID i : list)
			{
 				set.put(i.getSynsetID(), p);
			}
		}
		return ( set );
 	}
// vers. 2 for a {synset}
// gets *both* types of related synsets 1.  semantic e.g. <hypernyms> and 2. lexical e.g. antonyms
	public Hashtable<ISynsetID, IPointer> getAllRelatedSynsetsAndTheirTypes(ISynset synset)
	{
		Hashtable<ISynsetID, IPointer>		set		=	new Hashtable<ISynsetID, IPointer>();
// 1. for {synsets}
		Map<IPointer,  List<ISynsetID>>	semantic_synset_pointers 		=	synset.getRelatedMap();
		for(IPointer p : semantic_synset_pointers.keySet())
		{
			List<ISynsetID> list = semantic_synset_pointers.get(p);
			for(ISynsetID i : list)
			{
 				set.put(i, p);
			}
		}

 // 2. for Word objects (in the synset)
 		List<IWord> 	words = synset.getWords(); // get the Word objects in the 'synset'
 		for(IWord word : words)
 		{
			Map<IPointer,  List<IWordID>>	semantic_word_pointers 		=	word.getRelatedMap();
			for(IPointer p : semantic_word_pointers.keySet())
			{
				List<IWordID> list = semantic_word_pointers.get(p);
				for(IWordID i : list)
				{
 					set.put(i.getSynsetID(), p);
				}
			}
		}
		return ( set );
 	}


// test it
 public static void main(String[] args)
 {
		String	vers		=	"3.0";
		String wnhome	= "C:/Program Files/WordNet/" + vers + "/dict";
		URL 		url		=	null;
		try{url = new URL("file", null, wnhome);}
		catch(MalformedURLException e){e.printStackTrace();}
		if(url == null) return;
		IDictionary dict = new Dictionary(url);
		dict.open();
// ....................................................................................................................................................................
	 	RelatedSynsets ap = new RelatedSynsets(dict);
// build a dummy synset
		String	wordIN	=	"sweet";
		String	pos		=	"a";
		int		sense	=	1;
		IIndexWord 				idxWord	= null;
		if(pos.equalsIgnoreCase("n")){idxWord	=	dict.getIndexWord(wordIN, POS.NOUN);}
		if(pos.equalsIgnoreCase("v")){idxWord	=	dict.getIndexWord(wordIN, POS.VERB);}
		if(pos.equalsIgnoreCase("a")){idxWord	=	dict.getIndexWord(wordIN, POS.ADJECTIVE);}
		if(pos.equalsIgnoreCase("r")){idxWord	=	dict.getIndexWord(wordIN, POS.ADVERB);}
// ............................................................................................................
		 IWordID 				wordID		=	idxWord.getWordIDs().get(sense-1);
		 IWord 					word 		=	dict.getWord(wordID);
		 ISynset 				synset 		=	word.getSynset();
// 1. for a "word"
		//Hashtable<ISynsetID, IPointer>	set1 = ap.getAllRelatedSynsetsAndTheirTypes("sweet", 1, "a");
// 2. for a {synset}
		Hashtable<ISynsetID, IPointer>	set2 = ap.getAllRelatedSynsetsAndTheirTypes(synset);
 }
}
