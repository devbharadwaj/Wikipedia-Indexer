package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className=RULENAMES.CAPITALIZATION)
public class CapitalizationRule implements TokenizerRule{

	@Override
	public void apply(TokenStream stream) throws TokenizerException {
		// TODO Auto-generated method stub


		if(stream!=null){
			String token;
			boolean textConvertedToLowerCase=false;
			while(stream.hasNext()){
				token=stream.next();

				String[] tokenSplit=token.split(" ");
				for(int i=0;i<tokenSplit.length;i++){
					//System.out.println("A");
					if(tokenSplit[i].matches("[A-Z0-9]*")){
						continue;
					}else if(tokenSplit[i].matches("([A-Z])([a-z])+")){
						if(i==0){
							if(textConvertedToLowerCase){
								continue;
							}else{
								tokenSplit[i]=tokenSplit[i].toLowerCase();
								textConvertedToLowerCase= true;
							}
						}
						}								
					}
			

				token=tokenSplit[0];
				if(tokenSplit.length>1){
					for(int i=1;i<tokenSplit.length;i++){
						token=token +" "+ tokenSplit[i];
						System.out.println("i:"+i+" tokenSplit[i]:"+tokenSplit[i]);
					}
				}

				stream.previous();
				stream.set(token);
				stream.next();


			}
			stream.reset();

		}


	}

}
