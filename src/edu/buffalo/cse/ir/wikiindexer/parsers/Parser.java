/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.parsers;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikiAdditionalParser;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaParser;

/**
 * @author nikhillo
 *
 */
public class Parser {
	/* */
	private final Properties props;
	

	/**
	 * 
	 * @param idxConfig
	 * @param parser
	 */
	public Parser(Properties idxProps) {
		props = idxProps;
	}

	/* TODO: Implement this method */
	/** 
	 * @param filename
	 * @param docs
	 */
	public void parse(String filename, Collection<WikipediaDocument> docs) {
		if(filename!=null){
			WikiAdditionalParser wikiAddParser=new WikiAdditionalParser(docs);
		//	wikiAddParser.start();
			//wikiAddParser.setPriority(8);

			if(SAXParser(wikiAddParser.getMapperList(),filename))
				wikiAddParser.run();
			else 
				return;
			//SAX Parsing is complete. Set Stop flag to true
		//	wikiAddParser.stopFlag=true;

		/*	try {
				//Wait till Wiki parser completes
				wikiAddParser.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
*/
			//XML Parsing complete
			//Wiki parsing start		
			//Start WIki markup removal thread		
			//Wiki Markup removal
			//Create Wikipedia Document
			//In loop: add documents in collection
		}
	}

	/**
	 * Method to add the given document to the collection.
	 * PLEASE USE THIS METHOD TO POPULATE THE COLLECTION AS YOU PARSE DOCUMENTS
	 * For better performance, add the document to the collection only after
	 * you have completely populated it, i.e., parsing is complete for that document.
	 * @param doc: The WikipediaDocument to be added
	 * @param documents: The collection of WikipediaDocuments to be added to
	 */
	public synchronized void add(WikipediaDocument doc, Collection<WikipediaDocument> documents) {
		documents.add(doc);
	} 

	private boolean SAXParser(LinkedList<ArrayList<Object>> linkedList, String filename ){
		try {
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();			
			SAXHandler handler = new SAXHandler(linkedList);
			File file=new File(filename);
			if(file.exists()){
				saxParser.parse(new File(filename),handler);			
				System.out.println("Num of Documents:"+handler.countOfDocs);
				return true;
			}


		} catch (SAXException e) {
			System.out.println("SAXException" + e);
		} catch (IOException e) {
			System.out.println("IOException" + e);
		} catch (ParserConfigurationException e) {
			System.out.println("Parser Exception: " + e);
		}
		
		return false;
	}
	class SAXHandler extends DefaultHandler {

		private WikipediaDocument doc = null;

		private boolean btitle = false;
		private boolean btimestamp = false;
		private boolean bId;
		private boolean busername;
		private boolean bip;
		private boolean btext;
		public boolean brevision;

		private int Id;
		private String author;
		private String title;
		private String timestamp;

		private StringBuffer text = new StringBuffer();
		private int countOfDocs=0;
		private LinkedList<ArrayList<Object>> mapperList;


		public SAXHandler(LinkedList<ArrayList<Object>> linkedList) {
			// TODO Auto-generated constructor stub
			this.mapperList=linkedList;
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {

			if (qName.equalsIgnoreCase("id")) {
				if(!brevision){
					bId=true;
				}

			} else if (qName.equalsIgnoreCase("timestamp")) {
				btimestamp=true;
			}else if (qName.equalsIgnoreCase("username")) {
				busername=true;
			}else if (qName.equalsIgnoreCase("ip")) {
				bip=true;
			}else if (qName.equalsIgnoreCase("title")) {
				btitle=true;
			}else if (qName.equalsIgnoreCase("text")) {
				btext=true;
			}else if (qName.equalsIgnoreCase("revision")) {
				 brevision = true;
			}


		}
		public void endElement(String uri, String localName, String qName) throws SAXException {			

			if (qName.equalsIgnoreCase("page")) {
				try {
					//	System.out.println("inside end element: author" + author);
					doc = new WikipediaDocument(Id, timestamp, author, title);					
					ArrayList<Object> processingArrayList=new ArrayList<Object>();
					processingArrayList.add(doc);
					processingArrayList.add(text.toString());					
					mapperList.add(processingArrayList);
					countOfDocs++;
				//	System.out.println("countOfDocs:"+countOfDocs);
					text = new StringBuffer("");
					btext=false;
					brevision=false;

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
		}
		public void characters(char ch[], int start, int length) throws SAXException {
			if (bId) {
			//	if(!brevision){
					Id= Integer.parseInt(new String(ch, start, length));
			//		System.out.println("Id:"+Id);
					bId = false;
			//	}
			}else if (busername) {
				author=new String(ch, start, length);
				busername = false;
			}else if (bip) {
				author=new String(ch, start, length);
				bip = false;
			}else if (btitle) {
				title=new String(ch, start, length);
				btitle = false;
			}else if (btimestamp) {
				timestamp=new String(ch,start, length);
				btimestamp = false;
			}else if (btext) {
				text=text.append(ch,start,length);

			}

		}
	}

}
