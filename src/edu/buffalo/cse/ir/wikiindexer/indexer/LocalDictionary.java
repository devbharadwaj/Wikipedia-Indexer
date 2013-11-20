/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author nikhillo
 * This class represents a subclass of a Dictionary class that is
 * local to a single thread. All methods in this class are
 * assumed thread safe for the same reason.
 */
public class LocalDictionary extends Dictionary {

	/**
	 * Public default constructor
	 * @param props: The properties file
	 * @param field: The field being indexed by this dictionary
	 */

	private int counter=1;

	public LocalDictionary(Properties props, INDEXFIELD field) {
		super(props, field);
		// TODO Auto-generated constructor stub
		if(field==INDEXFIELD.AUTHOR){
			tokenPostingForwardMap=new HashMap<String, Integer>(100);
			tokenPostingReverseMap=new HashMap<Integer,String>(100);
		}else if(field==INDEXFIELD.CATEGORY){
			tokenPostingForwardMap=new HashMap<String, Integer>(500);
			tokenPostingReverseMap=new HashMap<Integer,String>(500);
		}else if(field==INDEXFIELD.LINK||field==INDEXFIELD.TERM){
			tokenPostingForwardMap=new HashMap<String, Integer>(500);
			tokenPostingReverseMap=new HashMap<Integer,String>(500);
		}

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
	public int lookup(String value) {
		//TODO Implement this method

		if(tokenPostingForwardMap.containsKey(value)){
			return tokenPostingForwardMap.get(value);
		}else{
			int uniqueIdentifier=getUniqueIdentifier(value);
			tokenPostingForwardMap.put(value, uniqueIdentifier);
			tokenPostingReverseMap.put(uniqueIdentifier,value);
			return tokenPostingForwardMap.get(value);
		}			

	}

	private int getUniqueIdentifier(String value){
		return counter++;
	}

	public Map<String, Integer> getTokenPostingForwardMap() {
		return tokenPostingForwardMap;
	}

	public void setTokenPostingForwardMap(
			Map<String, Integer> tokenPostingForwardMap) {
		this.tokenPostingForwardMap = tokenPostingForwardMap;
	}

	public Map<Integer, String> getTokenPostingReverseMap() {
		return tokenPostingReverseMap;
	}

	public void setTokenPostingReverseMap(
			Map<Integer, String> tokenPostingReverseMap) {
		this.tokenPostingReverseMap = tokenPostingReverseMap;
	}

}
