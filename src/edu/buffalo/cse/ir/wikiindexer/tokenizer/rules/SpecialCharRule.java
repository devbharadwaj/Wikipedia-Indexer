package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.ldap.StartTlsRequest;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.SPECIALCHARS)
public class SpecialCharRule implements TokenizerRule
{
	public void apply (TokenStream stream) throws TokenizerException
	{
		if(stream!=null){
			String token;
			while (stream.hasNext())
			{
				token=stream.next();
				if(token.matches("[a-zA-Z0-9]*([-\\+][a-zA-Z0-9]+)+")){
					String[] splitString=token.split("[-\\+]+");
					stream.previous();
					stream.set(splitString);
					stream.next();
				}else if(token.matches(".*[\\^\\*]+.*")){
					String[] splitString=token.split("[\\^\\*]+");
					stream.previous();
					stream.set(splitString);
					stream.next();
				}else if(token.matches("[0-9]+[\\.[0-9]+]?\\%")){
					token=token.replaceAll("\\%", "");
					stream.previous();
					stream.set(token);
					stream.next();
				}else if(token.matches("\\$[0-9]+\\.[0-9]+")){
					token=token.replaceAll("\\$", "");
					stream.previous();
					stream.set(token);
					stream.next();
				}else if(token.matches("#[0-9]+-[0-9]+")){
					token=token.replaceAll("#", "");
					stream.previous();
					stream.set(token);
					stream.next();
				}else if(token.matches("[\\w]+@[\\w]+\\.[a-z]{3}")){
					String[] splitString=token.split("@");
					stream.previous();
					stream.set(splitString);
					stream.next();
				}else if(token.matches(".*[~\\(\\):;,=&\\|\\{\\}>\"”<_/\\\\]+.*")){
					//handles Case 1..Also adding few other characters which are not required. {}
					token = token.replaceAll("[~\\(\\):;,=&\\|>\"”<_/\\\\]*","");
					if(token.equals("")){
						stream.previous();
						stream.remove();
					}else{
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
