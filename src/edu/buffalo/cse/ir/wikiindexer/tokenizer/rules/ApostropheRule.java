package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.APOSTROPHE)
public class ApostropheRule implements TokenizerRule{
	static String[][] aposStringArray={
		{"ain't","am","not"},
		{"aren't","are","not"},
		{"can't","cannot",""},
		{"can't've","cannot","have"},
		{"'cause","because",""},
		{"could've","could","have"},
		{"couldn't","could","not"},
		{"couldn't've","could","not"},
		{"didn't","did","not"},
		{"doesn't","does","not"},
		{"don't","do","not"},
		{"'em","them",""},
		{"hadn't","had","not"},
		{"hadn't've","had","not"},
		{"hasn't","has","not"},
		{"haven't","have","not"},
		{"he'd","he","had"},
		{"he'd've","he","would"},
		{"he'll","he","will"},
		{"he'll've","he","will"},
		{"he's","he","is"},
		{"how'd","how","did"},
		{"how'd'y","how","do"},
		{"how'll","how","will"},
		{"how's","how","is"},
		{"i'd","I","had"},
		{"i'd've","I","would"},
		{"i'll","I","will"},
		{"i'll've","I","will"},
		{"i'm","I","am"},
		{"i've","I","have"},
		{"isn't","is","not"},
		{"it'd","it","had"},
		{"it'd've","it","would"},
		{"it'll","it","will"},
		{"it'll've","it","will"},
		{"it's","it","is"},
		{"let's","let","us"},
		{"ma'am","madam",""},
		{"mayn't","may","not"},
		{"might've","might","have"},
		{"mightn't","might","not"},
		{"mightn't've","might","not"},
		{"must've","must","have"},
		{"mustn't","must","not"},
		{"mustn't've","must","not"},
		{"needn't","need","not"},
		{"needn't've","need","not"},
		{"o'clock","of","the"},
		{"oughtn't","ought","not"},
		{"oughtn't've","ought","not"},
		{"shan't","shall","not"},
		{"sha'n't","shall","not"},
		{"shan't've","shall","not"},
		{"she'd","she","had"},
		{"she'd've","she","would"},
		{"she'll","she","will"},
		{"she'll've","she","will"},
		{"she's","she","is"},
		{"should've","should","have"},
		{"shouldn't","should","not"},
		{"shouldn't've","should","not"},
		{"so've","so","have"},
		{"so's","so","is"},
		{"that'd","that","had"},
		{"that'd've","that","would"},
		{"that's","that","is"},
		{"there'd","there","would"},
		{"there'd've","there","would"},
		{"there's","there","is"},
		{"they'd","they","would"},
		{"they'd've","they","would"},
		{"they'll","they","will"},
		{"they'll've","they","will"},
		{"they're","they","are"},
		{"they've","they","have"},
		{"to've","to","have"},
		{"wasn't","was","not"},
		{"we'd","we","had"},
		{"we'd've","we","would"},
		{"we'll","we","will"},
		{"we'll've","we","will"},
		{"we're","we","are"},
		{"we've","we","have"},
		{"weren't","were","not"},
		{"what'll","what","will"},
		{"what'll've","what","will"},
		{"what're","what","are"},
		{"what's","what","is"},
		{"what've","what","have"},
		{"when's","when","is"},
		{"when've","when","have"},
		{"where'd","where","did"},
		{"where's","where","is"},
		{"where've","where","have"},
		{"who'll","who","will"},
		{"who'll've","who","will"},
		{"who's","who","is"},
		{"who've","who","have"},
		{"why's","why","is"},
		{"why've","why","have"},
		{"will've","will","have"},
		{"won't","will","not"},
		{"won't've","will","not"},
		{"would've","would","have"},
		{"wouldn't","would","not"},
		{"wouldn't've","would","not"},
		{"y'all","you","all"},
		{"y'all'd","you","all"},
		{"y'all'd've","you","all"},
		{"y'all're","you","all"},
		{"y'all've","you","all"},
		{"you'd","you","had"},
		{"you'd've","you","would"},
		{"you'll","you","will"},
		{"you'll've","you","will"},
		{"you're","you","are"},
		{"you've","you","have"},
		{"ain't","Am","not"},
		{"Aren't","Are","not"},
		{"Can't","Cannot",""},
		{"Can't've","Cannot","have"},
		{"'cause","because",""},
		{"Could've","Could","have"},
		{"Couldn't","Could","not"},
		{"Couldn't've","Could","not"},
		{"Didn't","Did","not"},
		{"Doesn't","Does","not"},
		{"Don't","Do","not"},
		{"'em","them",""},
		{"Hadn't","Had","not"},
		{"Hadn't've","Had","not"},
		{"Hasn't","Has","not"},
		{"Haven't","have","not"},
		{"He'd","He","had"},
		{"He'd've","He","would"},
		{"He'll","He","will"},
		{"He'll've","He","will"},
		{"He's","He","is"},
		{"How'd","How","did"},
		{"How'd'y","How","do"},
		{"How'll","How","will"},
		{"How's","How","is"},
		{"I'd","I","had"},
		{"I'd've","I","would"},
		{"I'll","I","will"},
		{"I'll've","I","will"},
		{"I'm","I","am"},
		{"I've","I","have"},
		{"Isn't","Is","not"},
		{"it'd","it","had"},
		{"It'd've","It","would"},
		{"It'll","It","will"},
		{"It'll've","It","will"},
		{"It's","It","is"},
		{"Let's","Let","us"},
		{"Ma'am","Madam",""},
		{"Mayn't","May","not"},
		{"Might've","Might","have"},
		{"Mightn't","Might","not"},
		{"Mightn't've","Might","not"},
		{"Must've","Must","have"},
		{"Mustn't","Must","not"},
		{"Mustn't've","Must","not"},
		{"Needn't","Need","not"},
		{"Needn't've","Need","not"},
		{"O'clock","Of","the"},
		{"Oughtn't","Ought","not"},
		{"Oughtn't've","Ought","not"},
		{"Shan't","Shall","not"},
		{"Sha'n't","Shall","not"},
		{"Shan't've","Shall","not"},
		{"She'd","She","had"},
		{"She'd've","She","would"},
		{"She'll","She","will"},
		{"She'll've","She","will"},
		{"She's","She","is"},
		{"Should've","Should","have"},
		{"Shouldn't","Should","not"},
		{"Shouldn't've","should","not"},
		{"So've","So","have"},
		{"So's","So","is"},
		{"That'd","That","had"},
		{"That'd've","That","would"},
		{"That's","That","is"},
		{"There'd","There","would"},
		{"There'd've","There","would"},
		{"There's","There","is"},
		{"They'd","They","would"},
		{"They'd've","They","would"},
		{"They'll","They","will"},
		{"They'll've","They","will"},
		{"They're","They","are"},
		{"They've","They","have"},
		{"To've","To","have"},
		{"Wasn't","Was","not"},
		{"We'd","We","had"},
		{"we'd've","We","would"},
		{"We'll","We","will"},
		{"We'll've","We","will"},
		{"We're","We","are"},
		{"We've","We","have"},
		{"Weren't","Were","not"},
		{"What'll","What","will"},
		{"What'll've","What","will"},
		{"What're","What","are"},
		{"What's","What","is"},
		{"What've","What","have"},
		{"When's","When","is"},
		{"When've","When","have"},
		{"Where'd","Where","did"},
		{"Where's","Where","is"},
		{"Where've","Where","have"},
		{"Who'll","Who","will"},
		{"Who'll've","Who","will"},
		{"Who's","Who","is"},
		{"Who've","Who","have"},
		{"Why's","Why","is"},
		{"Why've","Why","have"},
		{"Will've","Will","have"},
		{"Won't","Will","not"},
		{"Won't've","Will","not"},
		{"Would've","Would","have"},
		{"Wouldn't","Would","not"},
		{"Wouldn't've","Would","not"},
		{"Y'all","You","all"},
		{"Y'all'd","You","all"},
		{"Y'all'd've","You","all"},
		{"Y'all're","You","all"},
		{"Y'all've","You","all"},
		{"You'd","You","had"},
		{"You'd've","You","would"},
		{"You'll","You","will"},
		{"You'll've","You","will"},
		{"You're","You","are"},
		{"You've","You","have"}};

	static HashMap<String,String[]> lookupMap;
	static{

		lookupMap=new HashMap<String,String[]>(100);
		for(int i=0;i<aposStringArray.length;i++){
			lookupMap.put(aposStringArray[i][0],new String[]{aposStringArray[i][1],aposStringArray[i][2]});
		}
		aposStringArray=null;
	}
	//static long timeCounter=System.currentTimeMillis();
	public void apply(TokenStream stream) throws TokenizerException
	{
		if (stream !=null)
		{

			while(stream.hasNext())
			{

				String token=stream.next();
				if(token.contains("'")||token.contains("�")){
					token=token.replaceAll("�", "'");
					if(lookupMap.containsKey(token)){
						//there is a match					
						String[] replaceArr=lookupMap.get(token);
						//System.out.println("Inside lookup:"+lowerCase);
						stream.previous();
						stream.set(replaceArr);
						stream.next();
					}else{
				//		System.out.println("Inside else1:"+token);
						token = Pattern.compile("(')s").matcher(token).replaceAll("");
						token = token.replaceAll("'", "");
						
						if(!"".equals(token)){
							stream.previous();
							stream.set(token);
							stream.next();
				//			System.out.println("Inside else2:"+token);
						}else{
							stream.previous();
							stream.remove();
						}
					}
				}
			}
			stream.reset();

		}
	}

}
