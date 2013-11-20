package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

public abstract class Index {

	protected LocalDictionary localDictionary;

	protected SharedDictionary sharedDictionary;

	protected Map<Integer,LinkedList<Integer[]>> indexEntries;

	protected LinkedList<Integer[]>  topKList;

	protected Properties props;

	public int getTotalKeyTerms() {	
		return this.indexEntries.size();

	}
	public abstract int getTotalValueTerms();	

	public Map<String, Integer> getPostings(String key) {
		if(key!=null){
			int keyId=this.localDictionary.lookup(key);		
			if(this.indexEntries!=null && this.indexEntries.containsKey(keyId)){
				Map<String, Integer>  postingsMap=new HashMap<String, Integer>(this.indexEntries.size());
				LinkedList<Integer[]> postingList= this.indexEntries.get(keyId);
				Integer[] posting;
				for(int i=1;i<postingList.size();i++){
					posting=postingList.get(i);
					postingsMap.put(sharedDictionary.reverseLookUp(posting[0]), posting[1]);
				}
				return postingsMap;
			}
		}
		return null;
	}
	public  Collection<String> getTopK(int k){
		if(this.topKList.size()>0){
			Collections.sort(this.topKList,new Comparator<Integer[]>() {
				@Override
				public int compare(Integer[] o1, Integer[] o2) {
					// TODO Auto-generated method stub
					return Integer.compare(o2[1],o1[1]);
				}
			});	
			List<String> topKElementSet=new  LinkedList<String>();
			int totalKeyTerms=this.topKList.size();
			for(int i=0;(i<k && i<totalKeyTerms);i++){
				topKElementSet.add(this.localDictionary.reverseLookUp(topKList.get(i)[0]));
			}
			return topKElementSet;

		}
		return null;
	}
	
	public void addToIndex(int keyId, int valueId, int numOccurances) throws IndexerException{

	}
	public void addToIndex(String key, int valueId, int numOccurances) throws IndexerException {
		// TODO Auto-generated method stub

	}
	public void writeToDisk() throws IndexerException {
		// TODO Implement this method
		try{
			File directory=new File("IndexFiles");
			if(!directory.exists()){
				directory.mkdir();
			}
			File otherIndexFile = new File(directory,props.getProperty(this.getClass().getSimpleName()));
			if (!otherIndexFile.exists()) {
				otherIndexFile.createNewFile();
			}		
			FileWriter fw = new FileWriter(otherIndexFile.getAbsoluteFile());
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

			File otherIndexFile = new File("IndexFiles",props.getProperty(this.getClass().getSimpleName()));
			if(!otherIndexFile.exists()){
				throw new IndexerException("Index File could not be found");
			}
			RandomAccessFile randomAccessFile = new RandomAccessFile(otherIndexFile, "r");
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
	public abstract void cleanUp();


}
