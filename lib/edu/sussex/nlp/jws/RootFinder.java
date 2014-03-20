//package edu.sussex.nlp.jws;


import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;
import java.net.*;
import edu.mit.jwi.Dictionary;
import java.util.ArrayList;
import java.io.*;
import java.util.Iterator;
import java.util.List;

/*
	'RootFinder'

	Finds the <roots> in WordNet (version) and writes them to file

	David Hope, 2008, University Of Sussex
*/


public class RootFinder
{
 	private IDictionary 				dict 				=	null;
 	private ArrayList<String>		roots				=	null;
 	private String						wnversion		=	"";

	public RootFinder(IDictionary dict)
	{
		this.dict = dict;
		wnversion = "" + dict.getVersion();
		roots = new ArrayList<String>();
		read(POS.NOUN);
		read(POS.VERB);
		write();
	}

// Infinite loop problem for ( inhibit > <hypernym> < restrain ) ** done ** - I have applied the patch that Princeton provide: http://wordnet.princeton.edu/~ben/WN3.loop.patch
	private void read(POS pos)
	{
		ISynset							synset						=	null;
		Iterator<ISynset>			iterator						=	null;
		List<ISynsetID>			hypernyms				=	null;
		List<ISynsetID>			hypernym_instances	=	null;
		iterator = dict.getSynsetIterator(pos);
		while(iterator.hasNext())
		{
			synset = iterator.next();
 			hypernyms				=	synset.getRelatedSynsets(Pointer.HYPERNYM);
 			hypernym_instances	=	synset.getRelatedSynsets(Pointer.HYPERNYM_INSTANCE);
 			if(hypernyms.isEmpty() && hypernym_instances.isEmpty())
 			{
				roots.add("" + synset.getID()); // as String
			}
		}
	}

	private void write()
	{
    	try
    	{
        	BufferedWriter out = new BufferedWriter(new FileWriter(wnversion + ".roots.out"));
			for(String s : roots)
			{
				out.write(s + "\n");
			}
        out.close();
		}
    	catch (IOException e){e.printStackTrace();}
    	System.out.println("... " + wnversion + " done");

    }


// write <roots> for WordNet 1.6 ... 3.0
    public static void main(String[] args)
    {
		String[] versions = { "1.6", "1.7", "1.7.1", "2.0", "2.1", "3.0" };
		for(int i = 0; i < versions.length; i++)
		{
			String vers = versions[i];
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
			RootFinder path = new RootFinder(dict);
		}
    }
}
