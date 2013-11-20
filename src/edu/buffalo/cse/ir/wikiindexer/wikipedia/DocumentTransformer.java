
/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.Tokenizer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;

/**
 * A Callable document transformer that converts the given WikipediaDocument object
 * into an IndexableDocument object using the given Tokenizer
 * @author nikhillo
 *
 */
public class DocumentTransformer implements Callable<IndexableDocument> {
	/**
	 * Default constructor, DO NOT change
	 * @param tknizerMap: A map mapping a fully initialized tokenizer to a given field type
	 * @param doc: The WikipediaDocument to be processed
	 */
	Map<INDEXFIELD, Tokenizer> tknizerMap;
	WikipediaDocument doc;

	public DocumentTransformer(Map<INDEXFIELD, Tokenizer> tknizerMap, WikipediaDocument doc) {
		//TODO: Implement this method
		this.tknizerMap=tknizerMap;
		this.doc=doc;
	}

	/**
	 * Method to trigger the transformation
	 * @throws TokenizerException Inc ase any tokenization error occurs
	 */
	public IndexableDocument call() throws TokenizerException {
		// TODO Implement this method

		IndexableDocument indexDocument=new IndexableDocument();	
		/*-----------------------------------------------------Code for populating Term Stream and adding to Indexable Document--------------------------- */
		//for Term
		String title=doc.getTitle();
		Integer Id=doc.getId();
		TokenStream termStream=new TokenStream(Id.toString());	
		termStream.append(title);
		Iterator<WikipediaDocument.Section> sectionIterator = doc.getSections().iterator();

		WikipediaDocument.Section section=null;
		int i=0;
		while(sectionIterator.hasNext()){

			section=sectionIterator.next();			
			termStream.append(section.getTitle());
			termStream.append(section.getText());
		//	System.out.println("Iterating over Section List.Section:"+section.getText()+" i="+i+" Added");	
			i++;
		}		
		//Apply Tokenizer to this Raw Stream
		if(tknizerMap.containsKey(INDEXFIELD.TERM)){
			Tokenizer termTokenizer=tknizerMap.get(INDEXFIELD.TERM); 
			//Applying Tokenizer
			termTokenizer.tokenize(termStream);
			//Term Stream is now tokenized
		}
		//Add the current tokenized category stream to IndexField
		indexDocument.addField(INDEXFIELD.TERM, termStream);
		termStream=null;
		sectionIterator=null;
		//TokenStream for Categories has been created
		/*------------------------------------------------------Code End--------------------------------------------------------------------------------------*/


		/*-----------------------------------------------------Code for populating Author Stream and adding to Indexable Document--------------------------- */
		//for Author
		String author=doc.getAuthor();
		TokenStream authorStream=new TokenStream(author);
		Tokenizer authorTokenizer=null;
		//Apply Tokenizer to this Raw Stream
		if(tknizerMap.containsKey(INDEXFIELD.AUTHOR)){
			authorTokenizer=tknizerMap.get(INDEXFIELD.AUTHOR); 
			//Applying Tokenizer
			authorTokenizer.tokenize(authorStream);
			//Author Stream is now tokenized
		}
		//Add the current tokenized author stream to IndexField
		indexDocument.addField(INDEXFIELD.AUTHOR, authorStream);
		authorStream=null;
		authorTokenizer=null;
		//TokenStream for Author has been created
		/*------------------------------------------------------Code End--------------------------------------------------------------------------------------*/		

		/*-----------------------------------------------------Code for populating Category Stream and adding to Indexable Document--------------------------- */
		//for Category
		Iterator<String> categoryIterator = doc.getCategories().iterator();
		TokenStream categoryStream=null;
		if(categoryIterator.hasNext()){
			categoryStream=new TokenStream(categoryIterator.next());
		}
		i=0;
	//	System.out.println("----------------------------------------");
		while(categoryIterator.hasNext()){
			String category=categoryIterator.next();
		//	System.out.println("Category:"+category+" i="+i+" Added to category Stream.");
			categoryStream.append(category);
			i++;
		}

	//	System.out.println("----------------------------------------");

		//Apply Tokenizer to this Raw Stream
		if(tknizerMap.containsKey(INDEXFIELD.CATEGORY)){
			Tokenizer categoryTokenizer=tknizerMap.get(INDEXFIELD.CATEGORY); 
			//Applying Tokenizer
			categoryTokenizer.tokenize(categoryStream);
			//Category Stream is now tokenized
		}
		//Add the current tokenized category stream to IndexField
		indexDocument.addField(INDEXFIELD.CATEGORY, categoryStream);
		categoryStream=null;
		categoryIterator=null;
		//TokenStream for Categories has been created
		/*------------------------------------------------------Code End--------------------------------------------------------------------------------------*/


		/*-----------------------------------------------------Code for populating Links Stream and adding to Indexable Document--------------------------- */
		//for Links
		Iterator<String> linksIterator = doc.getLinks().iterator();
		TokenStream linkStream=null;
		if(linksIterator.hasNext()){
			linkStream=new TokenStream(linksIterator.next());
		}
		i=0;
		while(linksIterator.hasNext()){
			String links=linksIterator.next();
		//	System.out.println("Link:"+links+" i:"+i+" Added to link Stream");
			linkStream.append(links);
		}

		//Apply Tokenizer to this Raw Stream
		if(tknizerMap.containsKey(INDEXFIELD.LINK)){
			Tokenizer linkTokenizer=tknizerMap.get(INDEXFIELD.LINK); 
			//Applying Tokenizer
			linkTokenizer.tokenize(linkStream);
			//Category Stream is now tokenized
		}
		//Add the current tokenized category stream to IndexField
		indexDocument.addField(INDEXFIELD.LINK, linkStream);
		linksIterator=null;
		linkStream=null;


		//TokenStream for Categories has been created
		/*------------------------------------------------------Code End--------------------------------------------------------------------------------------*/

	//	System.out.println("Document Transformation completed. IndexDocument stream size:"+count++);
		return indexDocument;
	}
static int count=0;
}
