package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;


import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className=RULENAMES.DELIM)
public class DelimiterRule implements TokenizerRule{

	String delimiter;
	
	public DelimiterRule(String delimiter)
	{
		this.delimiter = delimiter;
	}
	
    public void apply (TokenStream stream) throws TokenizerException{
        if (stream != null) {
            while(stream.hasNext()){
                String token=stream.next();
                    String[] tokenSplit=token.split(this.delimiter);
                    stream.previous();
                    stream.set(tokenSplit);       
                    stream.next();
            }   
            stream.reset();
        }       
    }
}