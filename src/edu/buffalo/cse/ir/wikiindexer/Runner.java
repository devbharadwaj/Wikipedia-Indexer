/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.runner.Computer;
import org.junit.runner.JUnitCore;

import edu.buffalo.cse.ir.wikiindexer.IndexerConstants.RequiredConstant;
import edu.buffalo.cse.ir.wikiindexer.indexer.AuthorIndex;
import edu.buffalo.cse.ir.wikiindexer.indexer.CategoryIndex;
import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.indexer.IndexerException;
import edu.buffalo.cse.ir.wikiindexer.indexer.LinkIndex;
import edu.buffalo.cse.ir.wikiindexer.indexer.SharedDictionary;
import edu.buffalo.cse.ir.wikiindexer.parsers.Parser;
import edu.buffalo.cse.ir.wikiindexer.test.AllTests;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.Tokenizer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerFactory;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.DocumentTransformer;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.IndexableDocument;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaDocument;

/**
 * @author nikhillo
 * Entry class into the indexer code. Check the printUsage() method or
 * the provided documentation on how to invoke this class.
 */
public class Runner {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			printUsage();
			System.exit(1);
		} else {
			if (args[0] != null && args[0].length() > 0) {
				String filename = args[0];
				Properties properties = loadProperties(filename);
				if (properties == null) {
					System.err.println("Error while loading the Properties file. Please check the messages above and try again");
					System.exit(2);
				} else  {
					if (args[1] != null && args[1].length() == 2) {
						String mode = args[1].substring(1).toLowerCase();
						
						if ("t".equals(mode)) {
							runTests(filename);
						} else if ("i".equals(mode)) {
							runIndexer(properties);
						} else if ("b".equals(mode)) {
							runTests(filename);
							runIndexer(properties);
						} else {
							System.err.println("Invalid mode specified!");
							printUsage();
							System.exit(4);
						}	
					} else {
						System.err.println("Invalid or no mode specified!");
						printUsage();
						System.exit(5);
					}
				}
			} else {
				System.err.println("The provided properties filename is empty or could not be read");
				printUsage();
				System.exit(3);
			}	
	
		}
		
	}
	
	/**
	 * Method to print the correct usage to run this class.
	 */
	private static void printUsage() {
		System.err.println("The usage is: ");
		System.err.println("java edu.buffalo.cse.ir.wikiindexer.Runner <filename> <flag>");
		System.err.println("where - ");
		System.err.println("filename: Fully qualified file name from which to load the properties");
		System.err.println("flag: one amongst the following -- ");
		System.err.println("-t: Only execute tests");
		System.err.println("-i: Only run the indexer");
		System.err.println("-b: Run both, tests first then indexer");
		
	}
	
	/**
	 * Method to run the full indexer
	 * @param properties: The properties file to run with
	 */
	private static void runIndexer(Properties properties) {
		long timeStart=System.currentTimeMillis();
		System.out.println("Time Start by parser:"+new Date(timeStart));
		ConcurrentLinkedQueue<WikipediaDocument> queue = new ConcurrentLinkedQueue<WikipediaDocument>();
		ParserRunner prunner = new ParserRunner(properties, queue);
		new Thread(prunner).start();
		
		try {
			synchronized (queue) {
				while (queue.isEmpty()) {
					Thread.sleep(1500);
				}
			}
			long timeEnd=System.currentTimeMillis();
	
			timeStart=System.currentTimeMillis();
			tokenizeAndIndex(properties, queue);
			timeEnd=System.currentTimeMillis();
			System.out.println("Time End by Tokenizer:"+new Date(timeEnd));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void tokenizeAndIndex(Properties properties,
			ConcurrentLinkedQueue<WikipediaDocument> queue) throws InterruptedException {
		/*
		 * Pseudo-code:
		 * 		1. Create a thread executor
		 * 		2. For each runner, initialize the tokenizer as needed
		 * 		3. Keep calling and putting as required
		 */
		ExecutorService threadPool = Executors.newFixedThreadPool(Integer.valueOf(properties.get(IndexerConstants.NUM_TOKENIZER_THREADS).toString()));
		CompletionService<IndexableDocument> pool = new ExecutorCompletionService<IndexableDocument>(threadPool);
		
		WikipediaDocument doc;
		Map<INDEXFIELD, Tokenizer> tknizerMap;
		int numDocs = 0;
		while (true) {
			doc = queue.poll();
			
			if (doc == null) {
				Thread.sleep(1500);
			} else {
				if ("DUMMY".equals(doc.getTitle()) && "DUMMY".equals(doc.getAuthor())) {
					break; //all done
				} else {
					tknizerMap = initMap(properties);
					pool.submit(new DocumentTransformer(tknizerMap, doc));
					numDocs++;
				}
			}
		}
		
		IndexableDocument idoc;
		SharedDictionary docDict = new SharedDictionary(properties, INDEXFIELD.LINK);
		int currDocId;
		ThreadedIndexerRunner termRunner = new ThreadedIndexerRunner(properties);
		SingleIndexerRunner authIdxer = new SingleIndexerRunner(properties, INDEXFIELD.AUTHOR, INDEXFIELD.LINK, docDict, false);
		SingleIndexerRunner catIdxer = new SingleIndexerRunner(properties, INDEXFIELD.CATEGORY, INDEXFIELD.LINK, docDict, false);
		SingleIndexerRunner linkIdxer = new SingleIndexerRunner(properties, INDEXFIELD.LINK, INDEXFIELD.LINK, docDict, true);
		Map<String, Integer> tokenmap;
		
		for (int i = 0; i < numDocs; i++) {
			try {
				idoc = pool.take().get();
				
				if (idoc != null) {
					currDocId = docDict.lookup(idoc.getDocumentIdentifier());
			//		System.out.println("Idoc size:"+idoc.getStream(INDEXFIELD.TERM).getTokenMap().size());
			//		System.out.println(" currDocId"+currDocId);
					
					TokenStream stream;
					try {
						for (INDEXFIELD fld : INDEXFIELD.values()) {
							stream = idoc.getStream(fld);
							
							if (stream != null) {
								tokenmap = stream.getTokenMap();
								
								if (tokenmap != null) {
							
								switch (fld.toString()) {
									case "TERM":
							//			System.out.println("TermIndexer called");
									termRunner.addToIndex(tokenmap, currDocId);
										break;
									case "AUTHOR":								
										authIdxer.processTokenMap(currDocId, tokenmap);
										break;
									case "CATEGORY":
										catIdxer.processTokenMap(currDocId, tokenmap);
										break;
									case "LINK":
										linkIdxer.processTokenMap(currDocId, tokenmap);
										break;
									}
								}
							}
							
						}
					} catch (IndexerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		//Added for Testing
	/*	 Thread.sleep(5000);
		AuthorIndex authIndex=new AuthorIndex(null);
		CategoryIndex catIndex=new CategoryIndex(null);
		LinkIndex linKIndex=new LinkIndex(null);
		try {
			authIndex.readFromDisk();
			catIndex.readFromDisk();
			linKIndex.readFromDisk();
		} catch (IndexerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		//End 
		
		try {
			termRunner.cleanup();
			authIdxer.cleanup();
			catIdxer.cleanup();
			linkIdxer.cleanup();
			docDict.writeToDisk();
			docDict.cleanUp();
		} catch (IndexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (!termRunner.isFinished() && !authIdxer.isFinished() && !catIdxer.isFinished() && !linkIdxer.isFinished()) {
			//do nothing
		}
		
		threadPool.shutdown();
		
	}

	private static Map<INDEXFIELD, Tokenizer> initMap(Properties props) {
		HashMap<INDEXFIELD, Tokenizer> map = new HashMap<INDEXFIELD, Tokenizer>(INDEXFIELD.values().length);
		TokenizerFactory fact = TokenizerFactory.getInstance(props);
		for (INDEXFIELD fld : INDEXFIELD.values()) {
			map.put(fld, fact.getTokenizer(fld));
		}
		
		return map;
	}

	/**
	 * Method to execute all tests
	 * @param filename: Filename for the properties file
	 */
	private static void runTests(String filename) {
		System.setProperty("PROPSFILENAME", filename);
		JUnitCore core = new JUnitCore();
		core.run(new Computer(), AllTests.class);
		
	}
	
	/**
	 * Method to load the Properties object from the given file name
	 * @param filename: The filename from which to load Properties
	 * @return The loaded object
	 */
	public static Properties loadProperties(String filename) {

		try {
			Properties props = FileUtil.loadProperties(filename);
			
			if (validateProps(props)) {
				return props;
			} else {
				System.err.println("Some properties were either not loaded or recognized. Please refer to the manual for more details");
				return null;
			}
		} catch (FileNotFoundException e) {
			System.err.println("Unable to open or load the specified file: " + filename);
		} catch (IOException e) {
			System.err.println("Error while reading properties from the specified file: " + filename);
		}
		
		return null;
	}
	
	/**
	 * Method to validate that the properties object has been correctly loaded
	 * @param props: The Properties object to validate
	 * @return true if valid, false otherwise
	 */
	private static boolean validateProps(Properties props) {
		/* Validate size */
		if (props != null && props.entrySet().size() == IndexerConstants.NUM_PROPERTIES) {
			/* Get all required properties and ensure they have been set */
			Field[] flds = IndexerConstants.class.getDeclaredFields();
			boolean valid = true;
			Object key;
			
			for (Field f : flds) {
				if (f.isAnnotationPresent(RequiredConstant.class) ) {
					try {
						key = f.get(null);
						if (!props.containsKey(key) || props.get(key) == null) {
							System.err.println("The required property " + f.getName() + " is not set");
							valid = false;
						}
					} catch (IllegalArgumentException e) {
						
					} catch (IllegalAccessException e) {
						
					}
				}
			}
			
			return valid;
		}
		
		return false;
	}
	
	private static class ParserRunner implements Runnable {
		private Properties idxProps;
		private Collection<WikipediaDocument> coll;
		private Parser parser;
		
		private static WikipediaDocument recEnd;
		
		private ParserRunner(Properties props, Collection<WikipediaDocument> collection) {
			this.idxProps = props;
			this.coll = collection;
			 parser = new Parser(props);
			 try {
				recEnd =  new WikipediaDocument(-9999, "2199-12-31T00:00:00Z", "DUMMY", "DUMMY");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void run() {
			parser.parse(FileUtil.getDumpFileName(idxProps), coll);
			((ConcurrentLinkedQueue<WikipediaDocument>) coll).offer(recEnd); //end of record
		}
		
	}

}