/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


/**
 * This class represents a stream of tokens as the name suggests.
 * It wraps the token stream and provides utility methods to manipulate it
 * @author nikhillo
 *
 */
public class TokenStream implements Iterator<String>{

	/**
	 * Default constructor
	 * @param bldr: THe stringbuilder to seed the stream
	 */
	public LinkedList<String> stream;
	private Map<String,Integer> tokenCountMap;
	private int cursor=0;

	public TokenStream(StringBuilder bldr) {
		//TODO: Implement this method
		if(bldr!=null){
			String string=bldr.toString();
			if(string!=""){
				stream=new LinkedList<String>();
				tokenCountMap= new HashMap<String,Integer>();
				addToken(string);
			}
		}
	}

	/**
	 * Overloaded constructor
	 * @param bldr: THe stringbuilder to seed the stream
	 */
	public TokenStream(String string) {
		//TODO: Implement this method
		if(string!=null && string!=""){
			stream=new LinkedList<String>();
			tokenCountMap= new HashMap<String,Integer>();
			addToken(string);
		}
	}

	/**
	 * Method to append tokens to the stream
	 * @param tokens: The tokens to be appended
	 */
	public void append(String... tokens) {
		//TODO: Implement this method		
		if(tokens!=null){
			for(int i=0;i<tokens.length;i++){
				if(tokens[i]!=null && !tokens[i].equals("")){
					addToken(tokens[i]);
				}
			}
		}
	}

	private void addToken(String newTokenString){	

		stream.add(newTokenString);
		addToMapOrIncrement(newTokenString);
	}
	private void addToken(String newTokenString,int position){	

		stream.add(position,newTokenString);
		addToMapOrIncrement(newTokenString);
	}

	/**
	 * Method to retrieve a map of token to count mapping
	 * This map should contain the unique set of tokens as keys
	 * The values should be the number of occurrences of the token in the given stream
	 * @return The map as described above, no restrictions on ordering applicable
	 */
	public Map<String, Integer> getTokenMap() {
		//TODO: Implement this method		
		if(tokenCountMap!=null && tokenCountMap.size()!=0){
			return tokenCountMap;
		}
		else{
			return null;
		}

	}

	/**
	 * Method to get the underlying token stream as a collection of tokens
	 * @return A collection containing the ordered tokens as wrapped by this stream
	 * Each token must be a separate element within the collection.
	 * Operations on the returned collection should NOT affect the token stream
	 */
	public Collection<String> getAllTokens() {
		//TODO: Implement this method

		if(stream!=null && tokenCountMap!=null)
		{	List<String> tokenList=new LinkedList<String>();

		for(int i=0;i<stream.size();i++){
			tokenList.add(stream.get(i));
		}
		return tokenList;
		}
		else 
			return null;
	}

	/**
	 * Method to query for the given token within the stream
	 * @param token: The toke n to be queried
	 * @return: THe number of times it occurs within the stream, 0 if not found
	 */
	public int query(String token) {
		//TODO: Implement this method	

		if(tokenCountMap!=null && tokenCountMap.containsKey(token)){
			return tokenCountMap.get(token);

		}else{
			return 0;
		}

	}

	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasNext() {
		// TODO: Implement this method	
		if(stream!=null){
			int size=stream.size();
			if(cursor==size ){
				return false;
			}else{
				//next element exist
				return true;
			}
		}return false;	
	}

	/**
	 * Iterator method: Method to check if the stream has any more tokens
	 * @return true if a token exists to iterate over, false otherwise
	 */
	public boolean hasPrevious() {
		//TODO: Implement this method
		if(stream!=null){
			if(cursor==-1 || cursor==0){
				//pointing to either last element or marker element 
				//No previous element.return false
				return false;
			}else{
				return true;

			}
		}return false;
	}

	/**
	 * Iterator method: Method to get the next token from the stream
	 * Callers must call the set method to modify the token, changing the value
	 * of the token returned by this method must not alter the stream
	 * @return The next token from the stream, null if at the end
	 */

	public String next() {
		// TODO: Implement this method	
		if(stream!=null){
			if(cursor<stream.size()){
				if(cursor<stream.size()){		
					String currentElement=stream.get(cursor);
					cursor=cursor+1;
					if(currentElement==""){
						return null;
					}else 
						return currentElement;						
				}			
			}		
		}
		return null;
	}

	/**
	 * Iterator method: Method to get the previous token from the stream
	 * Callers must call the set method to modify the token, changing the value
	 * of the token returned by this method must not alter the stream
	 * @return The next token from the stream, null if at the end
	 */
	public String previous() {
		//TODO: Implement this method		
		if(stream!=null){
			if(cursor>0){				
				if(cursor>0){
					cursor=cursor-1;
					String currentElement=stream.get(cursor);
					if(currentElement==""){
						return null;
					}else 
						return currentElement;						
				}			
			}		
		}
		return null;

		
	}

	/**
	 * Iterator method: Method to remove the current token from the stream
	 */
	public void remove() {
		// TODO: Implement this method	
		if(stream!=null){
			String currentElement=null;
			if(stream!=null &&	cursor<stream.size()){
				currentElement=stream.get(cursor);		
				stream.remove(cursor);
			}
			removeFromMapOrDecrement(currentElement);
		}
	}


	/**
	 * Method to merge the current token with the previous token, assumes whitespace
	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the previous one)
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithPrevious() {
		//TODO: Implement this method
		if(hasPrevious()&&stream.size()!=1){
			String currentElement=stream.get(cursor);
			remove();
			String prevElemString=previous();
			String mergedToken=prevElemString+" "+currentElement;
			stream.set(cursor, mergedToken);	
			removeFromMapOrDecrement(prevElemString);
			addToMapOrIncrement(mergedToken);
			return true;
		}
		return false;
	}

	private void addToMapOrIncrement(String token){
		int count=0;
		if(tokenCountMap.containsKey(token)){
			//	token exists
			count=tokenCountMap.get(token);
			tokenCountMap.put(token, count+1);
		}else{
			//token does not exist
			tokenCountMap.put(token,1);
		}
	}

	private void removeFromMapOrDecrement(String token){
		int count=0;
		if(tokenCountMap.containsKey(token)){
			//	token exists
			count=tokenCountMap.get(token);
			if(count==1){			
				tokenCountMap.remove(token);		
			}else{
				tokenCountMap.put(token, count-1);
			}
		}
	}

	/**
	 * Method to merge the current token with the next token, assumes whitespace
b	 * separator between tokens when merged. The token iterator should now point
	 * to the newly merged token (i.e. the current one)
	 * @return true if the merge succeeded, false otherwise
	 */
	public boolean mergeWithNext() {
		//TODO: Implement this method
		if(hasNext()&&stream.size()!=1){
			String currentElement=stream.get(cursor);
			remove();
			String nextElemString=stream.get(cursor);
			String mergedToken=currentElement+" "+nextElemString;
			stream.set(cursor, mergedToken);	
			removeFromMapOrDecrement(nextElemString);
			addToMapOrIncrement(mergedToken);
			return true;
		}
		return false;
	}

	/**
	 * Method to replace the current token with the given tokens
	 * The stream should be manipulated accordingly based upon the number of tokens set
	 * It is expected that remove will be called to delete a token instead of passing
	 * null or an empty string here.
	 * The iterator should point to the last set token, i.e, last token in the passed array.
	 * @param newValue: The array of new values with every new token as a separate element within the array
	 */
	public void set(String... newValue) {
		//TODO: Implement this method
		int count=cursor;
		boolean tokenAddedFlag=false;
		boolean isFirstElement=true;
		if(stream!=null	&&	newValue!=null){
			if(!(cursor==stream.size()||cursor<0)){
				for(int i=0;i<newValue.length;i++){
					if(newValue[i]!=null && !newValue[i].equals("")){
						if(isFirstElement){
							remove();
							isFirstElement=false;
						}
						addToken(newValue[i], count++);		
						tokenAddedFlag=true;
					}
				}
				if(tokenAddedFlag){
					cursor=count-1;
				}
			}
		}
	}

	/**
	 * Iterator method: Method to reset the iterator to the start of the stream
	 * next must be called to get a token
	 */

	public void reset() {
		//TODO: Implement this method
		cursor=0;
	}

	/**
	 * Iterator method: Method to set the iterator to beyond the last token in the stream
	 * previous must be called to get a token
	 */

	public void seekEnd() {
		if(stream!=null){
			cursor=stream.size();
		}
	}

	/**
	 * Method to merge this stream with another stream
	 * @param other: The stream to be merged
	 */
	public void merge(TokenStream other) {
		//TODO: Implement this method
		if(other!=null){	
			if(stream==null){
				stream=new LinkedList<String>();
				tokenCountMap= new HashMap<String,Integer>();				
			}
			while(other.hasNext()){
				this.append(other.next());
			}			
		}		
	}
}
