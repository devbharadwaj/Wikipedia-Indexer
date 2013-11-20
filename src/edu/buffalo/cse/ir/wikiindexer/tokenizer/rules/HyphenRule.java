package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.HYPHEN)
public class HyphenRule implements TokenizerRule
{
	public void apply (TokenStream stream) throws TokenizerException
	{
		if (stream!=null)
		{
			while (stream.hasNext())
			{
				String token = stream.next();
				if(token.contains("-")) {
					token = Pattern.compile("(^|[^0-9A-Z\\-])(-)+([^0-9A-Z\\-]|$)").matcher(token).replaceAll("$1" + " $3").trim();               
					if (token.equals("")||token.equals(" "))
					{
						stream.previous();
						stream.remove();
					}
					else
					{
						stream.previous();
						stream.set(token);
						stream.next();
					}

				}
			}
			stream.reset();
		}
	}


}