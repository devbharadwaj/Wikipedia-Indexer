package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;
import edu.buffalo.cse.ir.wikiindexer.wikipedia.WikipediaParser;

@RuleClass(className = RULENAMES.PUNCTUATION)
public class PunctuationRule implements TokenizerRule
{
	public void apply (TokenStream stream) throws TokenizerException
	{if(stream!=null){
		String token;
		//	System.out.println(this+"PunctuationRule Rule:  start"+System.nanoTime());
		
		boolean match=false;
		while (stream.hasNext())
		{	 
			token = stream.next();
			if(token.contains("?")&&!token.matches(".*[a-zA-Z]+\\?[a-zA-Z]+.*")){
				token = Pattern.compile("(\\?)+").matcher(token).replaceAll("").trim();
				match=true;
			}
			if(token.contains(".")&&!(token.matches("([0-9]{2,3}\\.){3}[0-9]{2,3}"))){				
				token = Pattern.compile("(\\.)+").matcher(token).replaceAll("").trim();
				match=true;
			}				
			if(token.contains("!")&&!token.matches("(![tT]rue)|(![fF]alse)")){
		
			token = Pattern.compile("(!)+").matcher(token).replaceAll("").trim();
			match=true;
			}

			if(match){
				if(!"".equals(token)){
				stream.previous();
				stream.set(token);
				stream.next();				
				}else{
					stream.previous();
					stream.remove();
				}
				match=false;
			}
		}
		stream.reset();
	}

	}
}
