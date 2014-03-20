import java.util.TreeMap;
import java.text.*;



public class TestJWSRandom
{

// 'TestJWSRandom' - how to use the JWSRandom class
// replacement for the 'TestRandom' class
// David Hope, 2008, University Of Sussex
 	public static void main(String[] args)
	{

// Set up Java WordNet::Similarity
		String	dir	=	"C:/Program Files/WordNet";
		JWS		ws	=	new JWS(dir, "3.0");


// Create a 'JWSRandom' instance: there are 3 constructors that one can use:

// C1.
		//JWSRandom random = new JWSRandom(dict); 							//  default, completely random

// C2.
		//JWSRandom random = new JWSRandom(dict, true); 					//	true = store the randomly generated numbers (default) for a sense pair
																										// false = exactly the same behaviour as C1.
// C3.
		JWSRandom random = new JWSRandom(ws.getDictionary(), true, 16.0); 	// set the upper limit on the scores

// Example Of Use:
// 1. all senses
		TreeMap<String,Double> map = random.random("apple", "banana", "n");
		for(String pair : map.keySet())
		{
			System.out.println(pair + "\t" + (map.get(pair)));
		}
// 2. max
		System.out.println("max\t\t\t" + random.max("apple", "banana", "n"));
	}
} // eof
