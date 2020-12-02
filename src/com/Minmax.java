package com;

import java.util.ArrayList;
import java.util.HashSet;

/*
    The class performs generation of min or max clauses for the Semi structured SQL query passed.
    MAX returns the maximum value from a specific column.
    MIN returns the minimum value from a specific column.
*/
public class Minmax
{
    /*The columns that have been processed or for whom the min max operation is defined */
    public HashSet<String> usedcols=new HashSet<>();

    public String finalstring="";
    /*The input sentence passed. */
    String sentence="";
    /*The list of columns present in the sentence. */
    ArrayList<String> columnnameslist=new ArrayList<>();
    /*A list of synonymous min and max words used to make comparisons with the sentence passed*/
    ArrayList<String> maxwords=new ArrayList<>();
    ArrayList<String> minwords=new ArrayList<>();
    XMLParser xml=new XMLParser();



    public Minmax(String sentenceinput, ArrayList<String> columnnamesinput)
    {
        this.sentence=sentenceinput;
        this.columnnameslist=columnnamesinput;
        minmaxprocessor();
    }

    public  void minmaxprocessor()
    {/*
    Identifies if the sentence passed contains any min or max words. If found insert the word into an array that is delimited
    by min/max words.
   */

     /*
        Add all words synonymous to min to the ArrayList provided.
      */
        xml.input="minimum";
        minwords.addAll(xml.xmlParser());

        xml.input="maximum";
        maxwords.addAll(xml.xmlParser());




        for(String min:minwords)
        {
            /*If the sentence contains a word that is present in the list 'minwords' add it to the second list
             * in minkeyinserter method. The spacing test on either side helps prevent other words that may have
             * 'min' in them to be mapped. */
            if(sentence.contains(" "+min+" ")||sentence.contains(min+" "))
            {
                /*Note that as a differentiator, min words are passed with true in 'min'*/
                minkeyinserter(min,true);
            }
        }


        for(String max:maxwords)
        {

            /*If the sentence contains a word that is present in the list 'maxwords' add it to the second list
             * in minkeyinserter method. The spacing test on either side helps prevent other words that may have
             * 'min' in them to be mapped. */
            
            if(sentence.contains(" "+max+" ")||sentence.contains(max+" "))
            {
                /*Note that as a differentiator, max words are passed with false in 'min'*/
                
                minkeyinserter(max,false);
            }

        }




    }

    public  void minkeyinserter(String minmaxkeyword,boolean min)
    {
        String tokenlist=sentence;
        /*tokensplit holds the sentence as individual snippets delimited by the presence of 'max' or 'min' */
        String[] tokensplit=sentence.split(minmaxkeyword);
        boolean foundcolumn=false;
        /*A minimum of 2 elements needed for the clause to run. If no max or min found, tokensplit.length will be 1 */
        if(tokensplit.length>=2)
        {

            
            

            /*Columns are typically on the right side of the delimiter*/
            String rightside=tokensplit[1];
            /* Using " " to demarcate words*/
            String[] rightsplit=rightside.split(" ");


            int i=0;
            /*Ignore spaces and null */
            while(i<=rightsplit.length-1&&(rightsplit[i].matches("")||rightsplit[i].matches(" ")))
            {
                i++;
                continue;

            }
            /*If a column is found and the column is not "" */
            if((i<=(rightsplit.length-1))&&columnnameslist.contains(rightsplit[i])&&!rightsplit[i].matches(""))
            {
                /*If the finalstring is null, comma seperate the columns that will arrive.*/
                if(!finalstring.matches(""))
                {
                    finalstring+=" , ";
                }

                /*If min is true, the sentence has a min keyword so identify the column using rightsplit
                 * and attach it to the final string*/
                if(min)
                {
                    finalstring += " MIN( " + rightsplit[i] + " ) ";

                }
                /*If min is false, the sentence has a max keyword so identify the column using rightsplit
                 * and attach it to the final string*/
                else
                {
                    finalstring += " MAX( " + rightsplit[i] + " ) ";
                }
                usedcols.add(rightsplit[1]);
                foundcolumn=true;

            }

        }/*No column found means that the column might be to the left.*/
        if(!foundcolumn)
        {

            String leftside=tokensplit[0];
            String[] leftsplit=leftside.split(" ");

            int i=leftsplit.length-1;
            /*Search for a column on the left side*/
            while(i>=0&&!columnnameslist.contains(leftsplit[i]))
            {
                i--;
            }
            /*i>=0 implies that column exists somewhere in the sentence*/
            if(i>=0)
            {
                if (!leftsplit[i].matches(""))
                {
                    if(!finalstring.matches(""))
                    {
                        finalstring+=" , ";
                    }

                    if(min)
                    {      finalstring += " MIN( " + leftsplit[i] + " ) ";

                    }
                    else
                    {
                        finalstring += " MAX( " + leftsplit[i] + " ) ";
                    }
                    usedcols.add(leftsplit[i]);
                }

            }
        }
    }


 /*Please use the following code to test min max class */
    public  static  void main(String args[])
    {

        ArrayList<String> columns=new ArrayList<>();
        columns.add("price");
        columns.add("cost");

        String sentence =" show me the highest  product with minimum price and whose cost price is maximum ";

        Minmax minmax=new Minmax(sentence,columns);

        


    }


}