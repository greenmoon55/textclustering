//package edu.sussex.nlp.jws;


import java.util.ArrayList;

/*
	'CompoundWords'

	What this class does:

		Returns a list of all possible variations of a compound word - that is, 'a word' that is made up
		of words separated by underscores _ , hyphens - , or spaces. For example, if we have, say,
		"apple-tree", we will get "apple-tree", "apple_tree", and "apple tree"

	Why do we have to do this?

		WordNet does not appear to have a standardised version of compound words. For example, it will
		accept "active-surface_agent" but will not accept "active-surface-agent", or "active_surface_agent" but will
		accept "new york city" and "newcastle-upon-tyne". And so, we have to try generate all combinations if we
		want to be exacting about 'looking up' compound words in WordNet.

	David Hope, 2008, University Of Sussex
*/
public class CompoundWords
{

	private String[] 					editor 		=	null;
	private ArrayList<String>	store			=	null;
	private ArrayList<String>	temp			=	null;
	private ArrayList<String>	separators	=	null;
	private String					word			=	"";
	public CompoundWords()
	{
		store 		=	new ArrayList<String>();
		temp 		=	new ArrayList<String>();
		separators	=	new ArrayList<String>();
		separators.add("-");
		separators.add("_");
		separators.add(" ");
	}

	public ArrayList<String> getCompounds(String word)
	{
		ArrayList<String> compounds = new ArrayList<String>();
		store.clear();
		editor = word.split("[-_\\s]");
		for(int i = 0; i < editor.length; i++)
		{
			word = editor[i];
			temp.clear();

			if(i == editor.length - 1)
			{
				for(String stored : store)
				{
					compounds.add(stored + word);
				}
			}
			else
			{
				for(String sep : separators)
				{
					if(!store.isEmpty())
					{
						for(String stored : store)
						{
							temp.add(stored + word + sep);
						}
					}
					else
					{
						temp.add(word + sep);
					}
				}
				if(store.isEmpty())
				{
					store.addAll(temp);
				}
				else
				{
					store.clear();
					store.addAll(temp);
				}
			}
		}
		return ( compounds );
	}



    public static void main(String[] args)
    {
		CompoundWords	compoundwords = new CompoundWords();
		ArrayList<String> compounds	=	 compoundwords.getCompounds("a_compound-word");
		for(String c : compounds)
			System.out.println(c);

    }
}