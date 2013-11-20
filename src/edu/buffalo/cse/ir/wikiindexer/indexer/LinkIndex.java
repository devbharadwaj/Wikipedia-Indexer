
package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

public class LinkIndex extends Index implements Writeable{

	private Map<Integer,LinkedList<Integer[]>> linkIndexEntries;
	
	private static LinkIndex singletonInstance;
	private SharedDictionary sharedDictionary;
	private Properties props;

	private LinkIndex(Properties props){
		this.props=props;		
		//	this.localDictionary=new LocalDictionary(props,INDEXFIELD.LINK);
		this.linkIndexEntries=new HashMap<Integer,LinkedList<Integer[]>>();
		this.topKList=new LinkedList<Integer[]>();
	}

	public static LinkIndex getLinkInstance(Properties props){
		if(singletonInstance==null){
			singletonInstance=new LinkIndex(props);
		}
		return singletonInstance;		
	}

	public void addToIndex(int documentId, int linkId, int numOccurances) throws IndexerException {		
		//here key is the author name, value is document Id and num of Occurances		
		int keyIntMappingNum=documentId;
		LinkedList<Integer[]> postingList=null;	
		if(this.linkIndexEntries.containsKey(keyIntMappingNum)){
			postingList=this.linkIndexEntries.get(keyIntMappingNum);
			Integer[] totalOccIntegers=postingList.get(0);
			totalOccIntegers[1]+=numOccurances;
			postingList.add(new Integer[]{linkId,numOccurances});
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
			postingList.add(new Integer[]{linkId,numOccurances});
			this.linkIndexEntries.put(keyIntMappingNum, postingList);	
		}		
	}

	public void writeToDisk() throws IndexerException {
		// TODO Implement this method
		//write the given Map to disk
		try{			
			File file = new File("linkIndexFile.dat");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw,10000);				
			Integer term;
			LinkedList<Integer[]> postingList;
			String postingListString;
			Integer[] docAndOccurranceMapping;
			for (Entry<Integer, LinkedList<Integer[]>> etr : this.linkIndexEntries.entrySet()) {
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

			bw.close();
		}catch(Exception e){
			e.printStackTrace(); 
		}
	}

	public void readFromDisk(SharedDictionary sharedDict) throws IndexerException{

		try{ 
			this.sharedDictionary=sharedDict;		
			this.linkIndexEntries=new HashMap<Integer,LinkedList<Integer[]>>(1000);
			LinkedList<Integer[]> postingList;
			Integer[] docAndOccurranceMapping;
			RandomAccessFile randomAccessFile = new RandomAccessFile("linkIndexFile.dat", "r");
			FileChannel inChannel = randomAccessFile.getChannel();
			MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
			Charset charset=Charset.forName("UTF-8");
			CharBuffer cb=charset.decode(buffer);
			String readString=cb.toString();
			buffer.clear();
			buffer=null;		
			inChannel.close();
			randomAccessFile.close();

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
				this.linkIndexEntries.put(termID,postingList);
				this.topKList.add(new Integer[]{termID,postingList.size()});
				startIndex=sb.indexOf("*",startIndex+1);
			}	
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see edu.buffalo.cse.ir.wikiindexer.indexer.Writeable#cleanUp()
	 */
	public void cleanUp() {
		// TODO Implement this method
		this.linkIndexEntries=null;
		
		singletonInstance=null;
		this.sharedDictionary=null;
		this.props=null;

	}

	@Override
	public int getTotalKeyTerms() {
		// TODO Auto-generated method stub
		if(this.linkIndexEntries!=null){
			return this.linkIndexEntries.size();
		}else 
			return -1;
		 
	}

	@Override
	public int getTotalValueTerms() {
		// TODO Auto-generated method stub
		if(this.sharedDictionary!=null){
			return this.sharedDictionary.getTotalTerms();
		}else
			return -1;
	}

	@Override
	public Map<String, Integer> getPostings(String key) {
		//TODO: Implement this method
		int keyId=this.sharedDictionary.lookup(key);
		if(this.linkIndexEntries!=null && linkIndexEntries.containsKey(keyId)){
			Map<String, Integer> postingMap=new HashMap<String, Integer>();
			List<Integer[]> postingList= this.linkIndexEntries.get(keyId);
			Integer posting[];
			for(int i=1;i<postingList.size();i++){
				//reverse Lookup
				posting=postingList.get(i);
				postingMap.put(this.sharedDictionary.reverseLookUp(posting[0]), posting[1]);
			}
			return postingMap;
		}				
		return null;
	}

	@Override
	public Collection<String> getTopK(int k) {
		// TODO Auto-generated method stub

		if(this.topKList.size()>0){
			Collections.sort(this.topKList,new Comparator<Integer[]>() {

				@Override
				public int compare(Integer[] o1, Integer[] o2) {
					// TODO Auto-generated method stub
					return Integer.compare(o2[1],o1[1]);
				}
			});
			
			List<String> topKElementSet=new LinkedList<String>();
			int totalKeyTerms=this.topKList.size();
			for(int i=0;(i<k && i<totalKeyTerms);i++){
				topKElementSet.add(this.sharedDictionary.reverseLookUp(topKList.get(i)[0]));
			}
			return topKElementSet;
			
		}
		return null;
	}


	public void setSharedDic(SharedDictionary sharedDic) {
		this.sharedDictionary = sharedDic;
	}

	

}
