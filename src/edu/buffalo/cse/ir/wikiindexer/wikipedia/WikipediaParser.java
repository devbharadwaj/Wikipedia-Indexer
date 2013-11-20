/**
 * 
 */
package edu.buffalo.cse.ir.wikiindexer.wikipedia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * @author nikhillo
 * This class implements Wikipedia markup processing.
 * Wikipedia markup details are presented here: http://en.wikipedia.org/wiki/Help:Wiki_markup
 * It is expected that all methods marked "todo" will be implemented by students.
 * All methods are static as the class is not expected to maintain any state.
 */
public class WikipediaParser {
    /* TODO */
    /**
     * Method to parse section titles or headings.
     * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Sections
     * @param titleStr: The string to be parsed
     * @return The parsed string with the markup removed
     */
    public static String parseSectionTitle(String titleStr) {

        if(titleStr!=null){
            titleStr=Pattern.compile("([=]+[ ]*)([a-zA-z0-9 ]+)([ ]*[=]+)").matcher(titleStr).replaceAll("$2").trim();      
            return titleStr;
        }                
        return null;
    }

    /* TODO */
    /**
     * Method to parse list items (ordered, unordered and definition lists).
     * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Lists
     * @param itemText: The string to be parsed
     * @return The parsed string with markup removed
     */
    public static String parseListItem(String itemText) {

        if(itemText!=null){
            itemText=Pattern.compile("[\\*#:]*").matcher(itemText).replaceAll("").trim();
            return itemText;
        }else                     
            return null;
    }

    /* TODO */
    /**
     * Method to parse text formatting: bold and italics.
     * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Text_formatting first point
     * @param text: The text to be parsed
     * @return The parsed text with the markup removed
     */
    public static String parseTextFormatting(String boldText) {
        if(boldText!=null){
            boldText=Pattern.compile("[']{2,}").matcher(boldText).replaceAll("").trim();
            return boldText;
        }else                     
            return null;
    }

    /* TODO */
    /**
     * Method to parse *any* HTML style tags like: <xyz ...> </xyz>
     * For most cases, simply removing the tags should work.
     * @param text: The text to be parsed
     * @return The parsed text with the markup removed.
     */

    public static String parseTagFormatting(String htmlText) {
        if(htmlText!=null) {
           	htmlText=Pattern.compile("[ ]*<(.*?)>[ ]*").matcher(htmlText).replaceAll(" ").trim();
            htmlText=Pattern.compile("&lt;[/a-zA-Z0-9'= ]*&gt;").matcher(htmlText).replaceAll("");    
            return htmlText;
        }else
            return null;
    }

    /* TODO */
    /**
     * Method to parse wikipedia templates. These are *any* {{xyz}} tags
     * For most cases, simply removing the tags should work.
     * @param text: The text to be parsed
     * @return The parsed text with the markup removed
     */
    public static String parseTemplates(String textTemplate) {
        if(textTemplate!=null){
            int openingBraces=0;
            int closingBraces=0;
            StringBuffer sb=new StringBuffer(textTemplate);
            openingBraces=sb.lastIndexOf("{{");
            closingBraces=sb.indexOf("}}",openingBraces);
            while(openingBraces>-1 && closingBraces>-1){
                sb.replace(openingBraces, closingBraces+2, "");
                openingBraces=sb.lastIndexOf("{{");
                closingBraces=sb.indexOf("}}",openingBraces);

            }
            textTemplate=sb.toString();
             return textTemplate;
        }else return null;

    }    
    /* TODO */
    /**
     * Method to parse links and URLs.
     * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Links_and_URLs
     * @param text: The text to be parsed
     * @return An array containing two elements as follows - 
     *  The 0th element is the parsed text as visible to the user on the page
     *  The 1st element is the link url
     */    
    public static String[] parseLinks(String textLink) {

        String[] linkArray = {"",""};
                
        if (textLink == "" || textLink == null) return linkArray;
        Pattern pattern = Pattern.compile("(.*?)\\[+([^\\]]*)\\]+(.*)");
        Matcher matcher = pattern.matcher(textLink);
        while(matcher.find()) //master while till matches keep coming
        {            

            String link = matcher.group(2);
            
            /************************************OPTIMIZATION TEST*************
             * 
             
            
            String third1;
            if (!matcher.group(3).equals(""))
            {
                third1 = matcher.group(3).replace("<nowiki />","");
            }
            else
                third1 = matcher.group(3);
            Pattern nonOrLinksPattern1 = Pattern.compile("^[^\\|:]*$");
            Matcher nonOrLinks1 = nonOrLinksPattern1.matcher(link);
            if (nonOrLinks1.matches()) 
            {    
                linkArray[0] = matcher.group(1) + nonOrLinks1.group() + third1;
                String output = nonOrLinks1.group().replace(" ", "_");
                output = output.substring(0, 1).toUpperCase() + output.substring(1);
                linkArray[1] = output;
                return linkArray;
            }
            
            
            *********************************************************************/
            //HTTP match - only one with single []
            Pattern httpPattern = Pattern.compile("http[^\\s]*(.*?)$");
            Matcher httpMatcher = httpPattern.matcher(link);
            if (httpMatcher.matches())
            {                    
                String httpText = httpMatcher.group(1).trim();
                linkArray[0] = httpText;                                
            }
            else // FOR NON-HTTP LINKS
            {
                //Wikipedia: match
                //Consider | () #
                link = link.replace("[","");
                Pattern wikipediaPattern = Pattern.compile("^Wikipedia:(.*?)");
                Matcher wikipediaMatcher = wikipediaPattern.matcher(link);
                if (wikipediaMatcher.matches()) 
                {
                    String wikiLink = wikipediaMatcher.group(1);
                    //For Just |

                    Pattern forOrSymbol = Pattern.compile("^((?:(?!#|\\().)*)\\|$");
                    Matcher forOr = forOrSymbol.matcher(wikiLink);
                    if (forOr.matches())
                    {    
                        linkArray[0] = forOr.group(1);
                    }
                    //For ()
                    else
                    {
                        Pattern forBracketSymbol = Pattern.compile("([^(]*)\\(.*\\)\\|");
                        Matcher forBracket = forBracketSymbol.matcher(wikiLink);
                        if (forBracket.matches())
                        {    
                            linkArray[0] = forBracket.group(1).trim();
                        }
                        else
                        {
                            //For #
                            Pattern forHashSymbol = Pattern.compile("^((?:(?=#).)*.*)\\|$");
                            Matcher forHash = forHashSymbol.matcher(wikiLink);
                            if (forHash.matches())
                            {    
                                linkArray[0] = "Wikipedia:" + forHash.group(1);
                            }
                        }//end of # if
                    }//end of () matcher


                }//end of Wikipedia: matcher

                else // FOR WIKITIONARY
                { 


                    //Wiktionary matcher
                    Pattern wiktionaryPattern = Pattern.compile("^Wiktionary:(.*?)");
                    Matcher wiktionaryMatcher = wiktionaryPattern.matcher(link);
                    if (wiktionaryMatcher.matches()) 
                    {
                        String wiktionaryLink = wiktionaryMatcher.group(1);
                        // For single :
                        Pattern forSingleColonSymbol = Pattern.compile("^((?:(?!:).)*)$");
                        Matcher forSingleColon = forSingleColonSymbol.matcher(wiktionaryLink);
                        if (forSingleColon.matches()) 
                        {                    
                            linkArray[0] = "Wiktionary:" + forSingleColon.group();
                        }
                        else 
                        {
                            //For double ::
                            Pattern forDoubleColonSymbol = Pattern.compile("(.*?)\\|");
                            Matcher forDoubleColon = forDoubleColonSymbol.matcher(wiktionaryLink);
                            if (forDoubleColon.matches())
                            {
                                linkArray [0] = forDoubleColon.group(1);
                            }
                        }


                    }//end of Wiktionary matcher

                    /*Start of ****************************************
                     *     NON-HTTP,
                     *  NON-WIKIPEDIA, 
                     *  NON-WIKITIONARY links
                     * *************************************************
                     */
                    else 
                    {
                        //For CATEGORIES
                        Pattern catPattern = Pattern.compile("^(.)?Category:(.*?)");
                        Matcher catMatcher = catPattern.matcher(link);
                        if (catMatcher.matches())
                        {
                    //        System.out.println("CategoryMatch " + catMatcher.group(1) + catMatcher.group(2));
                            String check = catMatcher.group(1);
                            if (":".equals(check)) 
                            {
                                linkArray[0] = "Category:" + catMatcher.group(2);
                            }
                            else
                            {    
                                linkArray[0] = catMatcher.group(2);
                            }
                            //    System.out.println("Category returned: "+linkArray[0]);
                        }
                        /**************************************************************
                         * Start of
                         * NON HTTP
                         * NON Wikipedia
                         * NON Wikitionary
                         * NON Category links*******************************************
                         */
                        else
                        {
                            // FILE ELSE
                            Pattern filePattern = Pattern.compile("^File:.*\\|(.*?)$");
                            Matcher fileMatcher = filePattern.matcher(link);
                            if (fileMatcher.matches())
                            {    
                        //        System.out.println("File match" + fileMatcher.group(1));
                                linkArray[0] = fileMatcher.group(1);

                            }
                            // File without |
                            Pattern filePattern2 = Pattern.compile("^File:[^\\|]*$");
                            Matcher fileMatcher2 = filePattern2.matcher(link);
                            if (fileMatcher2.matches())
                            {
                                linkArray[0] = "";
                                linkArray[1] = "";
                            }
                            /***************************************************************
                             * start of 
                             * NON HTTP
                             * NON Wikipedia
                             * NON Wikitionary
                             * NON Category 
                             * NON FILE links
                             */

                            else
                            {
                                //MEDIA Links
                                Pattern mediaPattern = Pattern.compile("^media:.*\\|(.*?)$");
                                Matcher mediaMatcher = mediaPattern.matcher(link);
                                if (mediaMatcher.matches()) 
                                {
                                    linkArray[0] = mediaMatcher.group(1);
                                }
                                /***************************************************************
                                 * start of 
                                 * NON HTTP
                                 * NON Wikipedia
                                 * NON Wikitionary
                                 * NON Category 
                                 * NON FILE links*************************************************
                                 */
                                else
                                {
                                    //GENERAL LINKS
                                    //WITH | IN END
                                    Pattern generalLinkPattern = Pattern.compile("(.)?(.*?)\\|$");
                                    Matcher generalLinkMatcher = generalLinkPattern.matcher(link);
                                    if (generalLinkMatcher.matches())
                                    {
                                        String firschar = generalLinkMatcher.group(1).toUpperCase();
                                        String chunk = generalLinkMatcher.group(2).replace(" ","_");
                                        linkArray[1] = firschar + chunk;
                                        String genlink = generalLinkMatcher.group(1) + generalLinkMatcher.group(2);
                                        if (genlink.contains("("))
                                        {
                                            genlink = Pattern.compile("(.*?) \\(.*$").matcher(genlink).replaceAll("$1").trim();
                                        }
                                        else if (genlink.contains(","))
                                        {                
                                            genlink = Pattern.compile("(.*?), .*?$").matcher(genlink).replaceAll("$1").trim();
                                        }
                                        linkArray[0] = genlink;
                                    }
                                    /***************************************************************
                                     * start of 
                                     * NON HTTP
                                     * NON Wikipedia
                                     * NON Wikitionary
                                     * NON Category 
                                     * NON FILE links
                                     * NON | END links*************************************************
                                     */
                                    else
                                    {
                                        // | IN THE MIDDLE
                                        Pattern overlapLinkPattern = Pattern.compile("^(.+?)\\|(.+?)$");
                                        Matcher overlapLinkMatcher = overlapLinkPattern.matcher(link);
                                        Pattern filePattern4 = Pattern.compile("^File:.*$");
                                        Matcher fileMatcher4 = filePattern4.matcher(link);

                                        if (overlapLinkMatcher.matches() && !fileMatcher4.matches())
                                        {
                                            String overlap1 = overlapLinkMatcher.group(1);
                                            String overlap2 = overlapLinkMatcher.group(2);
                                            if (overlap1.contains(overlap2) || overlap2.contains(overlap1)) 
                                            {
                                                overlap1 = overlap1.substring(0, 1).toUpperCase() + overlap1.substring(1);
                                                linkArray[0] = matcher.group(1) + overlap2 + matcher.group(3);
                                                linkArray[1] = overlap1.replace(" ", "_");
                                            }
                                            else  //LONE STAR TEXAS
                                            {
                                                linkArray[0] = overlap2;
                                                linkArray[1] = overlap1;
                                            }
                                        }
                                        /***************************************************************
                                         * start of 
                                         * NON HTTP
                                         * NON Wikipedia
                                         * NON Wikitionary
                                         * NON Category 
                                         * NON FILE links
                                         * NON | END links
                                         * NON | MIDDLE*************************************************
                                         */

                                        else
                                        {    
                                            String third;
                                            if (!matcher.group(3).equals(""))
                                            {
                                                third = matcher.group(3).replace("<nowiki />","");
                                            }
                                            else
                                                third = matcher.group(3);
                                            Pattern nonOrLinksPattern = Pattern.compile("^[^\\|]*$");
                                            Matcher nonOrLinks = nonOrLinksPattern.matcher(link);
                                            if (nonOrLinks.matches()) 
                                            {    
                
                                                linkArray[0] = matcher.group(1) + nonOrLinks.group() + third;
                                                String output = nonOrLinks.group().replace(" ", "_");
                                                output = output.substring(0, 1).toUpperCase() + output.substring(1);
                                                linkArray[1] = output;
                                            }
                                        }//END of NON | 
                                    }//END of | in middle
                                }//END of | in end
                            }//END of Media Else

                        }//END of FILE ELSE
                    }//End of CATEGORY ELSE
                }//end of Wikipedia ELSE
            }//end of NON-WIKPEDIA LINKS (EXTERNAL)
        }//end of master WHILE
        return linkArray;
    }//end of function



}


