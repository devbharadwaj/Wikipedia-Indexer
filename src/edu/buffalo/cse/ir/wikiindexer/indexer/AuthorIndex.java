package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

public class AuthorIndex extends Index implements Writeable{
	private static AuthorIndex singletonInstance;
	private Properties props;



	private AuthorIndex(Properties props){

		this.localDictionary=new LocalDictionary(props, INDEXFIELD.AUTHOR);
		this.indexEntries=new HashMap<Integer,LinkedList<Integer[]>>(500);
		this.props=props;

	}
	public static AuthorIndex getAuthorInstance(Properties props){
		if(singletonInstance==null){
			singletonInstance=new AuthorIndex(props);
			singletonInstance.topKList=new LinkedList<Integer[]>();
		}
		return singletonInstance;		
	}

	public void addToIndex(String key, int docId, int numOccurances) throws IndexerException {		
		//here key is the author name, value is document Id and num of Occurances		
		int keyIntMappingNum=localDictionary.lookup(key);		
		LinkedList<Integer[]> postingList=null;
		if(this.indexEntries.containsKey(keyIntMappingNum)){
			postingList=this.indexEntries.get(keyIntMappingNum);
			//Update total Count
			Integer[] totalOccIntegers=postingList.get(0);
			totalOccIntegers[1]+=numOccurances;
			postingList.add(new Integer[]{docId,numOccurances});
			Collections.sort(postingList,new Comparator<Integer[]>() {
				@Override
				public int compare(Integer[] o1, Integer[] o2) {
					// TODO Auto-generated method stub
					return Integer.compare(o1[0],o2[0]);
				}
			});
		}else{
			postingList=new LinkedList<Integer[]>();
			postingList.add(0, new Integer[]{-99,numOccurances});
			postingList.add(new Integer[]{docId,numOccurances});
			this.indexEntries.put(keyIntMappingNum, postingList);	
		}								
	}

	public void writeToDisk() throws IndexerException {
		// TODO Implement this method
		try{
			File file = new File("authorIndexFile1.dat");		 
			if (!file.exists()) {
				file.createNewFile();
			}
 			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw,10000);
					
			Integer term;
			LinkedList<Integer[]> postingList;
			String postingListString;
			Integer[] docAndOccurranceMapping;
			for (Entry<Integer, LinkedList<Integer[]>> etr : this.indexEntries.entrySet()) {
				term = etr.getKey();
				postingList=etr.getValue();
				postingListString="*"+term+":";
				for(int i=0;i<postingList.size();i++){
					docAndOccurranceMapping=postingList.get(i);
					postingListString+=docAndOccurranceMapping[0]+","+docAndOccurranceMapping[1]+"#";

				}
				postingListString+="%";		
				bw.write(postingListString);
			}

			bw.write("<localDictionary>");
			Integer Id;
			String tokenString;
			String mapping;
			for (Entry<Integer, String> etr : this.localDictionary.getTokenPostingReverseMap().entrySet()) {
				Id=etr.getKey();
				tokenString=etr.getValue();
				mapping="<<"+Id+"&#"+tokenString+">>";
				bw.write(mapping);
			}
			bw.close();
		}catch(Exception e){
			e.printStackTrace(); 
		}
	}

	public void readFromDisk(SharedDictionary sharedDict) throws IndexerException{

		try{ 
			this.sharedDictionary=sharedDict;	
			this.indexEntries=new HashMap<Integer,LinkedList<Integer[]>>(1000);
			LinkedList<Integer[]> postingList;
			Integer[] docAndOccurranceMapping;
			RandomAccessFile randomAccessFile = new RandomAccessFile("authorIndexFile1.dat", "r");
			FileChannel inChannel = randomAccessFile.getChannel();
			MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
			Charset charset=Charset.forName("UTF-8");
			CharBuffer cb=charset.decode(buffer);
			String readString=cb.toString();
			
			StringBuffer sb=new StringBuffer(readString);
			readString=null;
			String temp;
			String[] temparr;
			int startIndex=sb.indexOf("*");
			while(startIndex!=-1){
				temp=sb.substring(startIndex+1, sb.indexOf("%",startIndex));
				temparr=temp.split("[:,#]");
				postingList=new LinkedList<Integer[]>();
				for(int i=1;i<temparr.length-1;i=i+2){
					docAndOccurranceMapping=new Integer[]{Integer.valueOf(temparr[i]),Integer.valueOf(temparr[i+1])};
					postingList.add(docAndOccurranceMapping);
				}
				int termID=Integer.valueOf(temparr[0]);
				this.indexEntries.put(termID,postingList);
				this.topKList.add(new Integer[]{termID,postingList.size()});
				startIndex=sb.indexOf("*",startIndex+1);
			}	
			
			startIndex=sb.indexOf("<localDictionary>");
			Map<Integer,String> localDictionaryReverseMap;
			Map<String,Integer> localDictionaryForwardMap;
			if(startIndex!=-1){
				localDictionaryReverseMap=new HashMap<Integer,String>(1000);
				localDictionaryForwardMap=new HashMap<String,Integer>(1000);
				startIndex=sb.indexOf("<<",startIndex+1);
			while(startIndex!=-1){
					temp=sb.substring(startIndex+2,sb.indexOf(">>",startIndex));
					temparr=temp.split("(&#)");
					localDictionaryReverseMap.put(Integer.valueOf(temparr[0]), temparr[1]);		
					localDictionaryForwardMap.put(temparr[1], Integer.valueOf(temparr[0]));
					startIndex=sb.indexOf("<<",startIndex+2);
					
				}
				this.localDictionary=new LocalDictionary(this.props, INDEXFIELD.AUTHOR);
				this.localDictionary.setTokenPostingReverseMap(localDictionaryReverseMap);
				this.localDictionary.setTokenPostingForwardMap(localDictionaryForwardMap);
			}			
			
			buffer.clear();
			buffer=null;	
			randomAccessFile.close();
			inChannel.close();

		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Implement this method
		singletonInstance=null;
		this.props=null;
		this.localDictionary=null;
		this.indexEntries=null;
	}

	@Override
	public int getTotalValueTerms() {
		// TODO Auto-generated method stub
		if(this.sharedDictionary!=null){
			return this.sharedDictionary.getTotalTerms();
		}else
			return -1;
	}

}
