package com;
import java.util.ArrayList;
import java.util.HashSet;

/*The class performs generation of avg clauses for the Semi structured SQL query passed.*/
public class Average
{
    /*The columns that have been processed or for whom the avg operation is defined */
    public HashSet<String> usedcols=new HashSet<>();

    public String finalstring="";

    /*The input sentence passed.*/

    String sentence="";
    /*The list of columns present in the sentence. */
    ArrayList<String> columnnameslist=new ArrayList<>();
    /*A list of synonymous avg words used to make comparisons with the sentence passed*/
    ArrayList<String> averagewords=new ArrayList<>();

    XMLParser xml=new XMLParser();



    public Average(String sentenceinput, ArrayList<String> columnnamesinput)
    {
        this.sentence=sentenceinput;
        this.columnnameslist=columnnamesinput;
        avgprocessor();
    }

    public  void avgprocessor()
    {
        /*
         Identifies if the sentence passed contains any avg words. If found insert the word into an array that is delimited
         by avg words.
        */



     /* Add all words synonymous to avg to the ArrayList provided. */
        xml.input="Average";
        averagewords.addAll(xml.xmlParser());



        for(String avg:averagewords)
        {
            /*If the sentence contains a word that is present in the list 'avgwords' add it to the second list
             * in avgkeyinserter method. The spacing test on either side helps prevent other words that may have
             * 'avg' in them to be mapped. */
            if(sentence.contains(" "+avg+" ")||sentence.contains(avg+" "))
            {
                /*Note that as a differentiator, avg words are passed with true in 'avg'*/
                avgkeyinserter(avg);
            }
        }


    }

    public  void avgkeyinserter(String avgkeyword)
    {
        String tokenlist=sentence;
        /*tokensplit holds the sentence as individual snippets delimited by the presence of 'avg' */
        String[] tokensplit=sentence.split(avgkeyword);
        boolean foundcolumn=false;
        /*A minimum of 2 elements needed for the clause to run. If avg found, tokensplit.length will be 1 */
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


                finalstring += " AVG( " + rightsplit[i] + " ) ";

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
                    finalstring += " AVG( " + leftsplit[i] + " ) ";

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
        String sentence ="average price of all products";
        Average avgmod=new Average(sentence,columns);
        System.out.println(avgmod.finalstring);

    }


}