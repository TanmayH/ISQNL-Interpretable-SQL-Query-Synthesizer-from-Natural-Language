package com;

import java.util.ArrayList;

public class Distinct
{
    public String finalstring="";

    public Distinct(String sentence)
    {
        XMLParser xml=new XMLParser();    /*Creating xml parser object*/
        xml.input="Distinct";
        ArrayList<String> distinctkeywords=new ArrayList<>();       /*create an array list*/
        distinctkeywords.addAll(xml.xmlParser());                   /*adding elements to the arraylist that are synonymous to distinct */


        for(String str:distinctkeywords)                    /* for each loop in which str takes all the strings from the arraylist one by one*/
        {

            if(sentence.contains(" "+str+" "))              /*if the sentence contains the string str, DISTINCT is stored in finalstring */
            {
                finalstring="DISTINCT ";
                break;                                        /*break */
            }
        }

    }
}
