package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikiAdditionalParser extends Thread {

	private LinkedList<ArrayList<Object>> mapperList= new LinkedList<ArrayList<Object>>();
	
	
	private Collection<WikipediaDocument> queue;
	public LinkedList<ArrayList<Object>> getMapperList() {
		return mapperList;
	}

	public static int counter=0;
	public boolean stopFlag=false;

	public WikiAdditionalParser(Collection<WikipediaDocument> queue){
		this.queue=queue;
	}


	public synchronized void add(WikipediaDocument wikiDoc,StringBuffer text){

		ArrayList intermediateArrayList=new ArrayList();
		intermediateArrayList.add(wikiDoc);
		intermediateArrayList.add(text);
		mapperList.add(intermediateArrayList);

	}

	public void run(){
		try{
			ArrayList processingArrayList=null;
			WikipediaDocument processingDoc=null;
			String processingText=null;
			while( mapperList.size()>0){
						processingArrayList=mapperList.poll();
						processingDoc=(WikipediaDocument)processingArrayList.get(0);
						processingText=(String)processingArrayList.get(1);
						updateWikipediaDocument(processingDoc,processingText);
						queue.add(processingDoc);
			}
			processingArrayList=null;
			processingDoc=null;
			processingText=null;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void updateWikipediaDocument(WikipediaDocument processingDoc,
			String processingText) {
		// TODO Auto-generated method stub

		processingText=WikipediaParser.parseListItem(processingText);
		processingText=WikipediaParser.parseTextFormatting(processingText);
		processingText=WikipediaParser.parseTagFormatting(processingText);
		processingText=WikipediaParser.parseTemplates(processingText);
		processingText=populateLinksAndCategories(processingDoc,processingText);
		processingText=processingText.replaceAll("[\"”&,]", " ");
		populateSectionList(processingDoc,processingText);	
	}
	static int i=0;
	public String populateLinksAndCategories(WikipediaDocument processingDoc,String processingText){
		Pattern pattern=Pattern.compile("[\\[]+.*?[\\]]+");
		Matcher matcher=pattern.matcher(processingText);

		String LinkArray[];
		int currentStart=-1;
		int currentEnd=-1;
		StringBuffer replacementBuffer=new StringBuffer();
		try{
			while(matcher.find()){
				currentStart = matcher.start();
				currentEnd = matcher.end();
				LinkArray=WikipediaParser.parseLinks(processingText.substring(currentStart,currentEnd));
				
				if(!"".equals(LinkArray[1])){
					if(!LinkArray[1].contains("Category")){
						processingDoc.addLink(LinkArray[1]);
						matcher.appendReplacement(replacementBuffer, Matcher.quoteReplacement(LinkArray[0].trim()));
					}else{
						matcher.appendReplacement(replacementBuffer,"");				
						processingDoc.addCategory(LinkArray[1].substring(8,LinkArray[1].length()).trim().replace("_", " "));
						
					}

				}else if(!"".equals(LinkArray[0])){
					matcher.appendReplacement(replacementBuffer, Matcher.quoteReplacement(LinkArray[0].trim()));
					
				}
				i++;
				
			}
			matcher.appendTail(replacementBuffer);
		}catch(Exception e){
			System.out.println("Exception occurred for "+processingDoc.toString());
			e.printStackTrace();
		}
		return replacementBuffer.toString();

	}
	public void populateSectionList(WikipediaDocument processingDoc,String processingText){
		Pattern pattern=Pattern.compile("([=]+[ ]*)([a-zA-z0-9 ]+)([ ]*[=]+)");
		Matcher matcher=pattern.matcher(processingText);
		String sectionTitle="";
		String sectionText="";
		int sectionTextEnd=0;
		int currentStart=0;
		int currentEnd=0;
		int i=0;
		boolean firstRun=true;
		if(matcher.find(currentEnd)==false){
			sectionText=processingText.substring(0,processingText.length()-1).trim();
			processingDoc.addSection("Default", sectionText);
			firstRun=false;
		}
		while(matcher.find(currentEnd)){
			currentStart = matcher.start();
			currentEnd = matcher.end();
			if(firstRun && processingText.substring(0,currentStart-1).trim()!=""){
				sectionText=processingText.substring(0,currentStart-1).trim();
				processingDoc.addSection("Default", sectionText);
				firstRun=false;
			}
			sectionTitle=WikipediaParser.parseSectionTitle(processingText.substring(currentStart,currentEnd));
			if(matcher.find(currentEnd)){
				sectionTextEnd= matcher.start()-1;			
				sectionText=(currentEnd+1<=sectionTextEnd)?processingText.substring(currentEnd+1,sectionTextEnd).trim():"";		

			}else{
				sectionText=processingText.substring(currentEnd+1,processingText.length()-1).trim();		

			}
			processingDoc.addSection(sectionTitle, sectionText);

	}	


	}
}







