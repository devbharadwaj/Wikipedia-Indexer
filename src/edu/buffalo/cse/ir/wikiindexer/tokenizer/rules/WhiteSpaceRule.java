package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.HashSet;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
@RuleClass(className=RULENAMES.WHITESPACE)

public class WhiteSpaceRule implements TokenizerRule{

	public void apply (TokenStream stream) throws TokenizerException{
		if (stream != null) {
			//	long startTime=System.nanoTime();
			while(stream.hasNext()){
				String token=stream.next();
				if(token.contains(" ")){
					String[] tokenSplit=token.trim().split("[\\s]+");
					stream.previous();
					stream.set(tokenSplit);		
					stream.next();
					//		}			
				}	
			}
			//	System.out.println("Total new stream size after whitespace rule:"+stream.stream);
			stream.reset();
			//		System.out.println(this+"SpechialChar Rule: End start"+(System.nanoTime()-startTime));
		}

	}
}