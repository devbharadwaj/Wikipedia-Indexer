/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

/**
 * @author nikhillo
 * This class is used to introspect a given index
 * The expectation is the class should be able to read the index
 * and all associated dictionaries.
 */
public class IndexReader {
	/**
	 * Constructor to create an instance 
	 * @param props: The properties file
	 * @param field: The index field whose index is to be read
	 */
	private Index indexer;
	private INDEXFIELD field;


	public IndexReader(Properties props, INDEXFIELD field) {
		//TODO: Implement this method
		//Read from properties.But need to have a data structure
		//shall we store the corresponding data structure(B-Tree structure) mapping here too? 
		this.field=field;
		if(field==INDEXFIELD.AUTHOR){
			indexer= AuthorIndex.getAuthorInstance(props);;
		}else if(field==INDEXFIELD.CATEGORY){
			indexer=CategoryIndex.getInstance(props);
		}else if(field==INDEXFIELD.LINK){
			indexer=LinkIndex.getLinkInstance(props);
			
		}
		SharedDictionary sharedDict=new SharedDictionary(props,INDEXFIELD.LINK);
		sharedDict.readFromDisk();
		readFromDisk(sharedDict);

	}

	/**
	 * Method to get the total number of terms in the key dictionary
	 * @return The total number of terms as above
	 */
	public int getTotalKeyTerms() {
		//TODO: Implement this method		
		if(this.field==INDEXFIELD.TERM){
			return TermIndex.getTotalKeyTerms();
		}else {
			return indexer.getTotalKeyTerms();
		}
	}

	/**
	 * Method to get the total number of terms in the value dictionary
	 * @return The total number of terms as above
	 */
	public int getTotalValueTerms() {
		//TODO: Implement this method
		if(this.field==INDEXFIELD.TERM){
			return TermIndex.getTotalValueTerms();
		}else {
			return indexer.getTotalValueTerms();
		}

	}

	/**
	 * Method to retrieve the postings list for a given dictionary term
	 * @param key: The dictionary term to be queried
	 * @return The postings list with the value term as the key and the
	 * number of occurrences as value. An ordering is not expected on the map
	 */
	public Map<String, Integer> getPostings(String key) {
		//TODO: Implement this method
		if(this.field==INDEXFIELD.TERM){
			return TermIndex.getPostings(key);
		}else {
			return indexer.getPostings(key);
		}
	}

	/**
	 * Method to get the top k key terms from the given index
	 * The top here refers to the largest size of postings.
	 * @param k: The number of postings list requested
	 * @return An ordered collection of dictionary terms that satisfy the requirement
	 * If k is more than the total size of the index, return the full index and don't 
	 * pad the collection. Return null in case of an error or invalid inputs
	 */
	public Collection<String> getTopK(int k) {
		//TODO: Implement this method
		if(this.field==INDEXFIELD.TERM){
			return TermIndex.getTopK(k);
		}else {
			return indexer.getTopK(k);
		}
	
	}

	/**
	 * Method to execute a boolean AND query on the index
	 * @param terms The terms to be queried on
	 * @return An ordered map containing the results of the query
	 * The key is the value field of the dictionary and the value
	 * is the sum of occurrences across the different postings.
	 * The value with the highest cumulative count should be the
	 * first entry in the map.
	 */
	public Map<String, Integer> query(String... terms) {
		//TODO: Implement this method (FOR A BONUS)
		List<Map<String,Integer>> postingMapList=new LinkedList<Map<String,Integer>>();
		
		for(int i=0;i<terms.length;i++){			
			if(this.field==INDEXFIELD.TERM){
				postingMapList.add(TermIndex.getPostings(terms[i]));			
			}else {
				postingMapList.add(indexer.getPostings(terms[i]));	
			}			
		}
		
		//sort in ascending order
		Collections.sort(postingMapList,new Comparator<Map<String,Integer>>() {

			@Override
			public int compare(Map<String, Integer> o1,
					Map<String, Integer> o2) {
				// TODO Auto-generated method stub
				if(o1.size()>o2.size()){
					return -1;
				}else if(o1.size()<o2.size())
					return 1;
				else
					return 0;
			}
		});
		Map<String,Integer> resultMap1;
		Map<String,Integer> resultMap2;
		Map<String,Integer> finalResultMap=new HashMap<String,Integer>();
		resultMap1=postingMapList.get(0);
		String key=null;
		int value=0;
		boolean matchfound=false;
		
		for(int i=1;i<postingMapList.size();i++){
			for (Entry<String, Integer> etr1 : resultMap1.entrySet()) {
				key=etr1.getKey();
				value=etr1.getValue();
				for (Entry<String, Integer> etr2 : postingMapList.get(i).entrySet()) {
					if(key.equals(etr2.getKey())){
						//there is a match
						//add to final list
						finalResultMap.put(key, value+etr2.getValue());
						matchfound=true;
					}
				}
			}
			resultMap1=finalResultMap;
			if(matchfound==false){
				break;
			}
			
			
		}
		return resultMap1;
		
		
		
		
		
		
		

	}

	public void readFromDisk(SharedDictionary sharedDic){
		try{
			if(this.field==INDEXFIELD.TERM){
				TermIndex.readpartitionedIndexersFromDisk(sharedDic);
			}else {
				indexer.readFromDisk(sharedDic);
			}
		}catch(Exception e){

		}
	}


}