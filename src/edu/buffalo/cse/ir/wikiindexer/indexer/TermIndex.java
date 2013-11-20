package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.Map.Entry;

public class TermIndex {

	static private Map<Integer,TermIndex> partitionMap=new HashMap<Integer,TermIndex>();
	public Map<String,LinkedList<Integer[]>> termIndexEntries;	
	protected LinkedList<String[]>  topKListPartitioned;
	static private int numOfPartitions=27;
	private int partitionNum;
	private static SharedDictionary sharedDic;

	static{
		for(int i=1;i<numOfPartitions;i++){
			partitionMap.put(i, new TermIndex(i));
		}
	}


	private TermIndex(int partitionNum){
		termIndexEntries= new HashMap<String,LinkedList<Integer[]>>(1000);
		this.partitionNum=partitionNum;
		this.topKListPartitioned=new LinkedList<String[]>();
	}

	static public TermIndex getPartitionIndex(String term){
		int partitionNumber=getPartitionNumber(term);	
		return partitionMap.get(partitionNumber);
	}
	static public int getPartitionNumber(String term){	

		if(term.matches("[Aa].*")){
			return 1;
		}else if(term.matches("[Bb].*")){
			return 2;
		}else if(term.matches("[Cc].*")){
			return 3;
		}else if(term.matches("[Dd].*")){
			return 4;
		}else if(term.matches("[Ee].*")){
			return 5;
		}else if(term.matches("[Ff].*")){
			return 6;
		}else if(term.matches("[Gg].*")){
			return 7;
		}else if(term.matches("[Hh].*")){
			return 8;
		}else if(term.matches("[Ii].*")){
			return 9;
		}else if(term.matches("[Jj].*")){
			return 10;
		}else if(term.matches("[Kk].*")){
			return 11;
		}else if(term.matches("[Ll].*")){
			return 12;
		}else if(term.matches("[Mm].*")){
			return 13;
		}else if(term.matches("[Nn].*")){
			return 14;
		}else if(term.matches("[Oo].*")){
			return 15;
		}else if(term.matches("[Pp].*")){
			return 16;
		}else if(term.matches("[Qq].*")){
			return 17;
		}else if(term.matches("[Rr].*")){
			return 18;
		}else if(term.matches("[Ss].*")){
			return 19;
		}else if(term.matches("[Tt].*")){
			return 20;
		}else if(term.matches("[Uu].*")){
			return 21;
		}else if(term.matches("[Vv].*")){
			return 22;
		}else if(term.matches("[Ww].*")){
			return 23;
		}else if(term.matches("[Xx].*")){
			return 24;
		}else if(term.matches("[Yy].*")){
			return 25;
		}else if(term.matches("[Zz].*")){
			return 26;
		}else {
			return 27;
		}

		/*if(term.matches("[aA-cC].*")){
			return 1;
		}else if(term.matches("[dD-fF].*")){
			return 2;
		}else if(term.matches("[gG-iI].*")){
			return 3;
		}else if(term.matches("[jJ-lL].*")){
			return 4;
		}else if(term.matches("[mM-oO].*")){
			return 5;
		}else if(term.matches("[pP-rR].*")){
			return 6;
		}else if(term.matches("[sS-uU].*")){
			return 7;
		}else if(term.matches("[vV-zZ].*")){
			return 8;
		}else {
			return 9;
		}*/
	}

	public static int getNumPartitions() {
		return numOfPartitions;
	}

	public void addToIndex(String term,int documentId,int numOfOccurrances ){
		LinkedList<Integer[]> postingList=null;
		if(this.termIndexEntries.containsKey(term)){
			postingList=this.termIndexEntries.get(term);
			Integer[] totalOccIntegers=postingList.get(0);
			totalOccIntegers[1]+=numOfOccurrances;
			postingList.add(new Integer[]{documentId,numOfOccurrances});			
			Collections.sort(postingList,new Comparator<Integer[]>() {

				@Override
				public int compare(Integer[] o1, Integer[] o2) {
					return Integer.compare(o1[0],o2[0]);
				}
			});
		}else{
			postingList=new LinkedList<Integer[]>();
			postingList.add(0, new Integer[]{-99,numOfOccurrances});
			postingList.add(new Integer[]{documentId,numOfOccurrances});
			this.termIndexEntries.put(term, postingList);	
		}
	}

	public void writeToDisk(){
		try {
			File directory=new File("IndexFiles");
			if(!directory.exists()){
				directory.mkdir();
			}
			File termPartitionedIndex = new File(directory,"TermIndexPartitionNum_"+partitionNum+".dat");
			if (!termPartitionedIndex.exists()) {
				termPartitionedIndex.createNewFile();
			}
			FileWriter fw = new FileWriter(termPartitionedIndex.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw,10000);	
			String term=null;
			String postingListString=null;
			List<Integer[]> postingList=null;
			Integer[] docAndOccurranceMapping=null;
			for (Entry<String, LinkedList<Integer[]>> etr : this.termIndexEntries.entrySet()) {
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
			bw.flush();
			bw.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void readFromDisk() throws IndexerException{
		try{ 
			File termPartitionedIndex = new File("IndexFiles","TermIndexPartitionNum_"+partitionNum+".dat");
			if(!termPartitionedIndex.exists()){
				throw new IndexerException("Index File could not be found");
			}
			this.termIndexEntries=new HashMap<String,LinkedList<Integer[]>>(1000);
			LinkedList<Integer[]> postingList;
			Integer[] docAndOccurranceMapping;
			RandomAccessFile randomAccessFile = new RandomAccessFile(termPartitionedIndex, "r");
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
				this.termIndexEntries.put(temparr[0],postingList);
				this.topKListPartitioned.add(new String[]{temparr[0],Integer.toString(postingList.size())});

				startIndex=sb.indexOf("*",startIndex+1);
			}	
			buffer.clear();
			buffer=null;	
			randomAccessFile.close();
			inChannel.close();

		}catch(Exception e){
		//	e.printStackTrace();
		}
	}
	static public int getTotalKeyTerms() {
		int totalKeyTerms=0;
		TermIndex partitionedIndex;
		for (Entry<Integer, TermIndex> etr : partitionMap.entrySet()) {
			partitionedIndex = etr.getValue();
			totalKeyTerms+=partitionedIndex.termIndexEntries.size();
		}
		return totalKeyTerms;
	}
	static public int getTotalValueTerms() {
		// TODO Auto-generated method stub
		if(sharedDic!=null){
			return sharedDic.getTotalTerms();
		}else
			return -1;
	}

	static public Map<String, Integer> getPostings(String key) {
		//TODO: Implement this method
		TermIndex partitionedIndex=TermIndex.getPartitionIndex(key);
		if(partitionedIndex!=null && partitionedIndex.termIndexEntries.containsKey(key)){
			Map<String, Integer> postingMap=new HashMap<String, Integer>();
			List<Integer[]> postingList= partitionedIndex.termIndexEntries.get(key);
			Integer[] postingtemp;
			for(int i=1;i<postingList.size();i++){
				postingtemp=postingList.get(i);
				postingMap.put(sharedDic.reverseLookUp(postingtemp[0]), postingtemp[1]);
			}
			return postingMap;
		}				
		return null;
	}
	public static void readpartitionedIndexersFromDisk(SharedDictionary sharedDictionary) throws IndexerException {
		// TODO Auto-generated method stub
		sharedDic=sharedDictionary;
		TermIndex partitionedTermIndex;
		for(int i=1;i<numOfPartitions;i++){
			partitionedTermIndex=partitionMap.get(i);
			partitionedTermIndex.readFromDisk();
		}		
	}
	public static Map<String, Integer> query(String[] terms) {
		// TODO Auto-generated method stub
		return null;
	}
	private List<String[]> getTopKNonStatic(int k){
		if(this.topKListPartitioned.size()>0){
			Collections.sort(this.topKListPartitioned,new Comparator<String[]>() {
				@Override
				public int compare(String[] o1, String[] o2) {
					// TODO Auto-generated method stub
					return Integer.compare(Integer.valueOf(o2[1]),Integer.valueOf(o1[1]));
				}
			});	
			List<String[]> topKElementSet=new  LinkedList<String[]>();
			int totalKeyTerms=this.topKListPartitioned.size();

			for(int i=0;(i<k && i<totalKeyTerms);i++){
				topKElementSet.add(topKListPartitioned.get(i));
			}
			return topKElementSet;

		}	
		return null;
	}
	public static Collection<String> getTopK(int k) {
		// TODO Auto-generated method stub
		TermIndex partitionedTermIndex;
		List<String[]> finalList=new LinkedList<String[]>();
		List<String> topKElementSet=new  LinkedList<String>();
		int totalNumber=getTotalKeyTerms();

		for(int i=1;i<numOfPartitions;i++){
			partitionedTermIndex=partitionMap.get(i);
			List<String[]> topKpostinglist=partitionedTermIndex.getTopKNonStatic(k);
			System.out.println("Num of partitions:"+i+" Null list:"+topKpostinglist);
			
			if(topKpostinglist!=null)
				finalList.addAll(topKpostinglist);
		}

		Collections.sort(finalList,new Comparator<String[]>() {
			@Override
			public int compare(String[] o1, String[] o2) {
				// TODO Auto-generated method stub
				return Integer.compare(Integer.valueOf(o2[1]),Integer.valueOf(o1[1]));
			}
		});	
		for(int i=0;(i<k && i<totalNumber);i++){
			topKElementSet.add(finalList.get(i)[0]);
		}
		return topKElementSet;
	}
	
	public void cleanUp() {
		// TODO Implement this method

		partitionMap=null;
		termIndexEntries=null;	
		topKListPartitioned=null;
		sharedDic=null;
	}
}




