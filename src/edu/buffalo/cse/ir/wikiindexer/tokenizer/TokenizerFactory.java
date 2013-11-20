/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer;

import java.util.Properties;

import edu.buffalo.cse.ir.wikiindexer.indexer.INDEXFIELD;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.AccentRule;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.ApostropheRule;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.CapitalizationRule;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.DateRule;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.EnglishStemmer;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.HyphenRule;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.PunctuationRule;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.SpecialCharRule;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.StopwordRule;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.WhiteSpaceRule;

/**
 * Factory class to instantiate a Tokenizer instance
 * The expectation is that you need to decide which rules to apply for which field
 * Thus, given a field type, initialize the applicable rules and create the tokenizer
 * @author nikhillo
 *
 */
public class TokenizerFactory {
	//private instance, we just want one factory
	private static TokenizerFactory factory;

	//properties file, if you want to read soemthing for the tokenizers
	private static Properties props;
	private Tokenizer termTokenizer;
	private Tokenizer authorTokenizer;
	private Tokenizer categoryTokenizer;
	private Tokenizer linkTokenizer;


	
	/**
	 * Private constructor, singleton
	 */
	private TokenizerFactory() {
		//TODO: Implement this method
		try {
			//Add new SpecialCharRule(),
			this.termTokenizer=new Tokenizer(new AccentRule(),new WhiteSpaceRule(),new SpecialCharRule(),new ApostropheRule(),new PunctuationRule(),new HyphenRule(),new StopwordRule(),new EnglishStemmer());
			this.authorTokenizer=new Tokenizer(new AccentRule(),new WhiteSpaceRule(),new ApostropheRule());
			this.categoryTokenizer=new Tokenizer(new AccentRule(),new WhiteSpaceRule(),new ApostropheRule(),new SpecialCharRule(),new PunctuationRule());
			this.linkTokenizer=new Tokenizer(new AccentRule(),new SpecialCharRule(),new PunctuationRule());				
		} catch (TokenizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * MEthod to get an instance of the factory class
	 * @return The factory instance
	 */
	public static TokenizerFactory getInstance(Properties idxProps) {
		if (factory == null) {
			factory = new TokenizerFactory();
			props = idxProps;	
		}

		return factory;
	}

	/**
	 * Method to get a fully initialized tokenizer for a given field type
	 * @param field: The field for which to instantiate tokenizer
	 * @return The fully initialized tokenizer
	 */
	public Tokenizer getTokenizer(INDEXFIELD field) {
		//TODO: Implement this method
		/*
		 * For example, for field F1 I want to apply rules R1, R3 and R5
		 * For F2, the rules are R1, R2, R3, R4 and R5 both in order
		 * So the pseudo-code will be like:
		 * if (field == F1)
		 * 		return new Tokenizer(new R1(), new R3(), new R5())
		 * else if (field == F2)
		 * 		return new TOkenizer(new R1(), new R2(), new R3(), new R4(), new R5())
		 * ... etc
		 */
		if(field==INDEXFIELD.TERM){
		//	return new Tokenizer(new DateRule());//new SpecialCharRule(),new PunctuationRule(),new WhiteSpaceRule(), new Stopwords());
			return factory.termTokenizer;
		}if(field==INDEXFIELD.AUTHOR){	
			return factory.authorTokenizer;		
		}if(field==INDEXFIELD.CATEGORY){
			return factory.categoryTokenizer;		
		}if(field==INDEXFIELD.LINK){			
			return factory.linkTokenizer;
			
		}
		// Need to add mapping for other IndexField

		return null;
	}
}
