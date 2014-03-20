import java.util.TreeMap;
import java.text.*;



public class TestExamples
{

// 'TestExamples': how to use Java WordNet::Similarity
// David Hope, 2008, University Of Sussex



 	public static void main(String[] args)
	{

// 1. SET UP:

//   Let's make it easy for the user. So, rather than set pointers in 'Environment Variables' etc. let's allow the user to define exactly where they have put WordNet(s)
		String dir = "C:/Program Files/WordNet";
//   That is, you may have version 3.0 sitting in the above directory e.g. C:/Program Files/WordNet/3.0/dict
//   The corresponding IC files folder should be in this same directory e.g. C:/Program Files/WordNet/3.0/WordNet-InfoContent-3.0

//   Option 1  (Perl default): specify the version of WordNet you want to use (assuming that you have a copy of it) and use the default IC file [ic-semcor.dat]
		JWS	ws = new JWS(dir, "3.0");
//   Option 2 : specify the version of WordNet you want to use and the particular IC file that you wish to apply
		//JWS ws = new JWS(dir, "3.0", "ic-bnc-resnik-add1.dat");





// 2. EXAMPLES OF USE:


// 2.1 [JIANG & CONRATH MEASURE]
		JiangAndConrath jcn = ws.getJiangAndConrath();
		System.out.println("Jiang & Conrath\n");
// all senses
		TreeMap<String, Double> 	scores1	=	jcn.jcn("apple", "banana", "n");			// all senses
		//TreeMap<String, Double> 	scores1	=	jcn.jcn("apple", 1, "banana", "n"); 	// fixed;all
		//TreeMap<String, Double> 	scores1	=	jcn.jcn("apple", "banana", 2, "n"); 	// all;fixed
		for(String s : scores1.keySet())
			System.out.println(s + "\t" + scores1.get(s));
// specific senses
		System.out.println("\nspecific pair\t=\t" + jcn.jcn("apple", 1, "banana", 1, "n") + "\n");
// max.
		System.out.println("\nhighest score\t=\t" + jcn.max("apple", "banana", "n") + "\n\n\n");



// 2.2 [LIN MEASURE]
		Lin lin = ws.getLin();
		System.out.println("Lin\n");
// all senses
		TreeMap<String, Double> 	scores2	=	lin.lin("apple", "banana", "n");			// all senses
		//TreeMap<String, Double> 	scores2	=	lin.lin("apple", 1, "banana", "n"); 	// fixed;all
		//TreeMap<String, Double> 	scores2	=	lin.lin("apple", "banana", 2, "n"); 	// all;fixed
		for(String s : scores2.keySet())
			System.out.println(s + "\t" + scores2.get(s));
// specific senses
		System.out.println("\nspecific pair\t=\t" + lin.lin("apple", 1, "banana", 1, "n") + "\n");
// max.
		System.out.println("\nhighest score\t=\t" + lin.max("apple", "banana", "n") + "\n\n\n");

// ... and so on for any other measure


// 2.3 [ADAPTED LESK MEASURE]
		AdaptedLesk 	lesk = ws.getAdaptedLesk();
							//lesk.useStopList(false);			// by default, the stop list and the lemmatiser
							//lesk.useLemmatiser(false);		// are used to check for 'non content' words in overlaps - here, you can turn these defaults off
		System.out.println("Adapted Lesk (Extended Gloss Overlaps)\n");
// all senses
		TreeMap<String, Double> 	scores3	=	lesk.lesk("apple", "banana", "n");	// all senses
		//TreeMap<String, Double> 	scores3	=	lesk.lesk("apple", 1, "banana", "n"); 		// fixed;all
		//TreeMap<String, Double> 	scores3	=	lesk.lesk("apple", "banana", 2, "n"); 		// all;fixed
		for(String s : scores3.keySet())
			System.out.println(s + "\t" + scores3.get(s));
// specific senses
		System.out.println("\nspecific pair\t=\t" + lesk.lesk("apple", 1, "banana", 1, "n") + "\n");
// max.
		System.out.println("\nhighest score\t=\t" + lesk.max("apple", "banana", "n") + "\n\n\n");


	}
} // eof
