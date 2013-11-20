package edu.buffalo.cse.ir.wikiindexer.tokenizer.rules;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenStream;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.TokenizerException;
import edu.buffalo.cse.ir.wikiindexer.tokenizer.rules.TokenizerRule.RULENAMES;

@RuleClass(className = RULENAMES.DATES)
public class DateRule implements TokenizerRule 
{
    public void apply (TokenStream stream) throws TokenizerException
    {
        if (stream!=null)
        {
            while (stream.hasNext())
        {
            String token = stream.next();
            String year = "1900";
            String month = "01";
            String day = "01";
            String hours = "00";
            String minutes = "00";
            String seconds = "00";
            String rabbits = null;
        
            Pattern utcPattern = Pattern.compile("(.*?)([0-9]{2}:[0-9]{2}:[0-9]{2}) UTC on (Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday), (\\d+) (January|February|March|April|May|June|July|August|September|October|November|December) ([12][0-9]{3})+(.*?)");
            Matcher utc = utcPattern.matcher(token);
            if (utc.matches())
            {
                
                month = utc.group(5);
                if (month.equals("January")) month = "01";
                else if        (month.equals("February")) month = "02";
                else if         (month.equals("March")) month = "03";
                else if          (month.equals("April")) month ="04";
                else if           (month.equals("May")) month ="05";
                else if            (month.equals("June")) month ="06";
                else if             (month.equals("July")) month ="07";
                else if              (month.equals("August")) month ="08";
                else if               (month.equals("September")) month ="09";
                else if                (month.equals("October")) month = "10";
                else if                 (month.equals("November")) month = "11";
                else if               (month.equals("December")) month = "12";
                else                     month = "01";
            
                if (!utc.group(6).equals(null))
                    year = utc.group(6);
                    
                
                day = String.format("%02d", Integer.parseInt(utc.group(4)));
                
                rabbits = utc.group(1) + year + month + day + " " + utc.group(2) + utc.group(7);
                token = rabbits;
            }
            
            Pattern normDatePattern = Pattern.compile("(.*?)([1-31]*) (January|February|March|April|May|June|July|August|September|October|November|December) ([12][0-9]{3})+(.*?)");
            Matcher normDate = normDatePattern.matcher(token);
            if (normDate.matches())
            {
                
                month = normDate.group(3);
                if (month.equals("January")) month = "01";
                else if        (month.equals("February")) month = "02";
                else if         (month.equals("March")) month = "03";
                else if          (month.equals("April")) month ="04";
                else if           (month.equals("May")) month ="05";
                else if            (month.equals("June")) month ="06";
                else if             (month.equals("July")) month ="07";
                else if              (month.equals("August")) month ="08";
                else if               (month.equals("September")) month ="09";
                else if                (month.equals("October")) month = "10";
                else if                 (month.equals("November")) month = "11";
                else if               (month.equals("December")) month = "12";
                else                     month = "01";
            
                if (!normDate.group(4).equals(null))
                year = normDate.group(4);
                else
                    year = "1900";
                if (!normDate.group(2).equals(null))
                {
                    
                    if (normDate.group(2).equals("1")||
                            normDate.group(2).equals("2")||
                             normDate.group(2).equals("3") ||
                              normDate.group(2).equals("4") ||
                               normDate.group(2).equals("5") ||
                                normDate.group(2).equals("6") ||
                                 normDate.group(2).equals("7") ||
                                  normDate.group(2).equals("8") ||
                                   normDate.group(2).equals("9"))
                    {
                        day = "0" + normDate.group(2);
                    }
                    else
                        day = normDate.group(2);

    
                    rabbits = normDate.group(1) + year + month + day + normDate.group(5);
                    token = rabbits;
    
                }
            }
              //2nd TEST CASE
                year = "1900";
                month = "01";
                day = "01";
            
                Pattern secondDatePattern = Pattern.compile("(.*?)(January|February|March|April|May|June|July|August|September|October|November|December) (\\d+), ([12][0-9]{3})+(.*?)");
                Matcher secondDate = secondDatePattern.matcher(token);
                if (secondDate.matches())
                {
                    
                    month = secondDate.group(2);
                    if (month.equals("January")) month = "01";
                    else if        (month.equals("February")) month = "02";
                    else if         (month.equals("March")) month = "03";
                    else if          (month.equals("April")) month ="04";
                    else if           (month.equals("May")) month ="05";
                    else if            (month.equals("June")) month ="06";
                    else if             (month.equals("July")) month ="07";
                    else if              (month.equals("August")) month ="08";
                    else if               (month.equals("September")) month ="09";
                    else if                (month.equals("October")) month = "10";
                    else if                 (month.equals("November")) month = "11";
                    else if               (month.equals("December")) month = "12";
                    else                     month = "01";
                    
                    if (!secondDate.group(4).equals(null))
                        year = secondDate.group(4);
                    else
                        year = "1900";
                    
                    if (!secondDate.group(3).equals(null))
                    {
                        
                        if (secondDate.group(3).equals("1")||
                                secondDate.group(3).equals("2")||
                                 secondDate.group(3).equals("3") ||
                                  secondDate.group(3).equals("4") ||
                                   secondDate.group(3).equals("5") ||
                                    secondDate.group(3).equals("6") ||
                                     secondDate.group(3).equals("7") ||
                                      secondDate.group(3).equals("8") ||
                                       secondDate.group(3).equals("9"))
                        {
                            day = "0" + secondDate.group(3);
                        }
                        else
                            day = secondDate.group(3);
                
                        rabbits = secondDate.group(1) + year + month + day + secondDate.group(5);
                        token = rabbits;
                    
                    }// not null
                }// end 2nd test
                //BC AD 4th TEST
                    year = "1900";
                    month = "01";
                    day = "01";
                    Pattern adbcPattern = Pattern.compile("(.*?)(\\d+) (BC|AD)(.*?)");
                    Matcher adbc = adbcPattern.matcher(token);
                    if (adbc.matches())
                    {
                        year = String.format("%04d", Integer.parseInt(adbc.group(2)));
                        if (adbc.group(3).equals("BC"))
                        {
                            year = "-" + year;
                        }
                        rabbits = adbc.group(1) + year + month + day + adbc.group(4);
                        
                        token = rabbits;
                    }
                    
                //TIME 5th TEST
                    Pattern timePattern = Pattern.compile("(.*?)(\\d+):(\\d{2}) *(am|AM|pm|PM)(.*?)");
                    Matcher timeMatch = timePattern.matcher(token);
                    if (timeMatch.matches())
                    {
                
                        int hh = Integer.parseInt(timeMatch.group(2));
                        if (timeMatch.group(4).equals("PM") || timeMatch.group(4).equals("pm") || timeMatch.group(4).equals(" pm") || timeMatch.group(4).equals(" PM"))
                        {
                            hh = hh + 12;
                        }
                        hours = String.format("%02d",hh);
                        minutes = timeMatch.group(3);
                        rabbits = timeMatch.group(1) + hours + ":" + minutes + ":" + seconds + timeMatch.group(5);
                        
                        token = rabbits;
                
                    }
                  // ONlY month
                    year = "1900";
                    month = "01";
                    day = "01";
                    Pattern monthPattern = Pattern.compile("(.*?)(January|February|March|April|May|June|July|August|September|October|November|December) ([0-9]{1,2})(.*?)");
                    Matcher monthMatcher = monthPattern.matcher(token);
                    if (monthMatcher.matches())
                    {
                    
                        month = monthMatcher.group(2);
                        if (month.equals("January")) month = "01";
                        else if        (month.equals("February")) month = "02";
                        else if         (month.equals("March")) month = "03";
                        else if          (month.equals("April")) month ="04";
                        else if           (month.equals("May")) month ="05";
                        else if            (month.equals("June")) month ="06";
                        else if             (month.equals("July")) month ="07";
                        else if              (month.equals("August")) month ="08";
                        else if               (month.equals("September")) month ="09";
                        else if                (month.equals("October")) month = "10";
                        else if                 (month.equals("November")) month = "11";
                        else if               (month.equals("December")) month = "12";
                        else                     month = "01";
                        
                        
                        day = String.format("%02d", Integer.parseInt(monthMatcher.group(3)));
                    
                        rabbits = monthMatcher.group(1) + year + month + day + monthMatcher.group(4);
                        token = rabbits;
                    }
                    
                // Only Year
                    year = "1900";
                    month = "01";
                    day = "01";
        
                    token = Pattern.compile(" (\\d{4}) ").matcher(token).replaceAll(" $1" + "0101 ");
                    
                // Special Char case 2011*12
                    
                    token = Pattern.compile("\\b(\\d{2})(\\d{2})(\\D)(\\d{2})\\b").matcher(token).replaceAll("$1$2" + "0101" + "$3" + "$1$4" + "0101");
    /*                
                    Pattern yearPattern = Pattern.compile(" (\\d{4}) ");
                    Matcher yearMatcher = yearPattern.matcher(token);
                    while (yearMatcher.find())
                    {    
                            System.out.println("year only");
                            year = String.format("%04d", Integer.parseInt(yearMatcher.group(1)));
                            token = token.replaceFirst(year, year+"0101");
                            
                    }
        */    
            stream.previous();
            stream.set(token);
            stream.next();
    }//master while loop
    }//stream null if
    }//apply method
}