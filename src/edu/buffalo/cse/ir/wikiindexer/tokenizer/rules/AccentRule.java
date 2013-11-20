package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;


import java.text.Normalizer;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.ACCENTS)
public class AccentRule implements TokenizerRule {
	
	public void apply(TokenStream stream) throws TokenizerException
	{
		if (stream != null)
		{
				while (stream.hasNext())
				{
						String token = stream.next();
						token = Normalizer.normalize(token, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");	
						if(!"".equals(token)){
						stream.previous();
						stream.set(token);
						stream.next();
						}else{
							stream.previous();
							stream.remove();
						}
			//		}
				}
				stream.reset();
		}
			
	}

}

