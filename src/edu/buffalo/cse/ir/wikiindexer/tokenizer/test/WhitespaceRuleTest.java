/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.tokenizer.test;

import java.util.Properties;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import edu.buffalo.cse.ir.wikiindexer.IndexerConstants;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;

/**
 * @author nikhillo
 *
 */
@RunWith(Parameterized.class)
public class WhitespaceRuleTest extends TokenizerRuleTest {

	public WhitespaceRuleTest(Properties props) {
		super(props, IndexerConstants.WHITESPACERULE);
	}
	
	@Test
	public void testRule() {
	
			try {
				assertArrayEquals(new Object[]{"this","is","a","test" }, 
						runtest("this is a test"));
				assertArrayEquals(new Object[]{"this","is","a","test" }, 
						runtest("this    is     a      test"));
				assertArrayEquals(new Object[]{"this","is","a","test" }, 
						runtest("    this is a test       "));
				assertArrayEquals(new Object[]{"this","is","a","test" }, 
						runtest("this "
								+ "is \r "
								+ "a \n test"));
				assertArrayEquals(new Object[]{"thisisatest" }, 
						runtest("thisisatest"));
			} catch (TokenizerException e) {
				
			}
		}
	

}
