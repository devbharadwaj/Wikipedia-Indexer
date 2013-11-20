package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.NUMBERS)
public class NumberRule implements TokenizerRule
{
	public void apply (TokenStream stream) throws TokenizerException
	{if(stream!=null){
		while (stream.hasNext())
		{
			String token = stream.next();
			token = Pattern.compile("[0-9]+[\\.,]*[0-9]+").matcher(token).replaceAll("").trim();
			token = Pattern.compile("  ").matcher(token).replaceAll(" ").trim();
			stream.previous();
			stream.set(token);
			stream.next();
		}
		stream.reset();
	}
	}
}
