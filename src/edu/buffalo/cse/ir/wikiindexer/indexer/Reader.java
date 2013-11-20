package edu.buffalo.cse.ir.wikiindexer.indexer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.Runner;

public class Reader {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Properties properties =new Properties();
		FileInputStream inStream = null;
		try{
		inStream = new FileInputStream("files\\properties.config");
		properties.load(inStream);
		inStream.close();
		}catch(Exception e){
			
		}	
	/*	SharedDictionary sharedDict=new SharedDictionary(properties,INDEXFIELD.LINK);
		sharedDict.readFromDisk();
	*/	
		IndexReader termReader=new IndexReader(properties,INDEXFIELD.TERM);
	//	termReader.readFromDisk(sharedDict);
		
		IndexReader authorReader=new IndexReader(properties,INDEXFIELD.AUTHOR);
//		authorReader.readFromDisk(sharedDict);
		
		IndexReader categoryReader=new IndexReader(properties,INDEXFIELD.CATEGORY);
	//	categoryReader.readFromDisk(sharedDict);
		
		IndexReader linkReader=new IndexReader(properties,INDEXFIELD.LINK);
	//	linkReader.readFromDisk(sharedDict);
		
		
		/*System.out.println("termIndex size:"+termReader.getTotalKeyTerms());
		System.out.println("authorReader size:"+authorReader.getTotalKeyTerms());
		System.out.println("categoryReader size:"+categoryReader.getTotalKeyTerms());
		System.out.println("linkReader size:"+linkReader.getTotalKeyTerms());
		*/
		System.out.println("Size:"+linkReader.getTotalKeyTerms()+" Posting:"+linkReader.getPostings("Doc1589"));
		
		System.out.println("Term Index terms:"+termReader.getTopK(-1));
		
	}
}
