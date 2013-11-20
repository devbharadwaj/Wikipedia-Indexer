

/*-
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.util.HashMap;
import java.util.Properties;


/**
 * @author nikhillo
 * This class represents a subclass of a Dictionary class that is
 * shared by multiple threads. All methods in this class are
 * synchronized for the same reason.
 */
public class SharedDictionary extends Dictionary {

	/**
	 * Public default constructor
	 * @param props: The properties file
	 * @param field: The field being indexed by this dictionary
	 */

	private int counter;
	public SharedDictionary(Properties props, INDEXFIELD field) {
		super(props, field);
		// TODO Add more code here if needed
		tokenPostingForwardMap=new HashMap<String, Integer>(1000);
		tokenPostingReverseMap=new HashMap<Integer,String>(1000);
	}

	/**
	 * Method to lookup and possibly add a mapping for the given value
	 * in the dictionary. The class should first try and find the given
	 * value within its dictionary. If found, it should return its
	 * id (Or hash value). If not found, it should create an entry and
	 * return the newly created id.
	 * @param value: The value to be looked up
	 * @return The id as explained above.
	 */
	public synchronized int lookup(String value) {
		//TODO Implement this method
		//So this class will generate a docID	
		if(tokenPostingForwardMap.containsKey(value)){
			return tokenPostingForwardMap.get(value);
		}else{
			int uniqueIdentifier=getUniqueIdentifier(value);
			tokenPostingForwardMap.put(value, uniqueIdentifier);
			tokenPostingReverseMap.put(uniqueIdentifier,value);
			return tokenPostingForwardMap.get(value);
		}		
	}


	public int getUniqueIdentifier(String value){
		return ++counter;
	}
	

}
