package com;

import java.util.ArrayList;
import java.util.HashSet;

public class OrderByClause
{

    XMLParser xml=new XMLParser();
    public String finalstring="";
    String sentence="";
    ArrayList<String> ascendingkeywords=new ArrayList<>();
    ArrayList<String> descendingkeywords=new ArrayList<>();
    ArrayList<String> columnnameslist=new ArrayList<>();
    public HashSet<String> columnsprocessed=new HashSet<>();


    public OrderByClause(String sentence,ArrayList<String> columnnames)
    {

        xml.input="ascending";

        ascendingkeywords.addAll(xml.xmlParser());


        xml.input="descending";

        descendingkeywords.addAll(xml.xmlParser());
        this.sentence=sentence;
        this.columnnameslist=columnnames;

        for(String asc:ascendingkeywords)
        {
            //          
            if(sentence.contains(asc))
            {

                //            
                ascdsckeywordinsert(asc,true);

            }

        }

        for(String dsc:descendingkeywords)
        {
            if(sentence.contains(dsc))
            {


                ascdsckeywordinsert(dsc,false);

            }

        }

        if(finalstring.matches(""))
        {
            specialcase();
        }

        finalstring=finalstring.trim();


    }




    public void specialcase() {
        String specialcase = sentence.toLowerCase();

        ArrayList<String> orderbykeywords = new ArrayList<>();

        xml.input = "orderby";
        orderbykeywords.addAll(xml.xmlParser());

        if(finalstring.matches(""))
        {
            outer: for (String str : orderbykeywords)
            {
                if (specialcase.contains(str))
                {
                    String[] sentencesplit=specialcase.split(str);

                    if(sentencesplit.length>=2)
                    {

                        String[] bytokens=sentencesplit[1].split("by");

                        if(bytokens.length>=2)
                        {

                            String subsetbysentence= bytokens[1];

                            String[] subsetbytoken=subsetbysentence.split(" ");

                            for(String subsetofsubsetbytoken:subsetbytoken)
                            {
                                if(columnnameslist.contains(subsetofsubsetbytoken))
                                {
                                    if(!subsetofsubsetbytoken.matches(""))
                                    {
                                        finalstring += " ORDER BY  "+subsetofsubsetbytoken+" ASC ";
                                    }

                                }
                            }

                        }
                    }
                }

            }
        }
    }

    public void ascdsckeywordinsert(String keyword,boolean ascending)
    {



        String tokenlist=sentence;
        String[] tokensplit=sentence.split(keyword);
        boolean foundcolumn=false;

        //   



        if(tokensplit.length>=2)
        {

            //    
            //    


            String rightside=tokensplit[1];
            rightside=rightside.toLowerCase();
            String[] rightsplit=rightside.split(" ");


            int i=0;
            while(i<=rightsplit.length-1&&(rightsplit[i].matches("")||rightsplit[i].matches(" ")||rightsplit[i].matches("of")))
            {
                i++;
                continue;

            }
            if((i<=(rightsplit.length-1))&&columnnameslist.contains(rightsplit[i])&&!rightsplit[i].matches("")&&(!columnsprocessed.contains(rightsplit[i])))
            {
                if(!finalstring.matches(""))
                {
                    finalstring+=" , ";
                }


                if(ascending)
                {
                    finalstring += " ORDER BY " + rightsplit[i] + " ASC ";
                }
                else
                {
                    finalstring += " ORDER BY  " + rightsplit[i] + " DSC ";
                }
                columnsprocessed.add(rightsplit[i]);
                foundcolumn=true;

            }

        }



        if(!foundcolumn)
        {

            String leftside=tokensplit[0];
            String[] leftsplit=leftside.split(" ");

            int i=leftsplit.length-1;






            while(i>=0&&!columnnameslist.contains(leftsplit[i]))
            {
                i--;


            }
            if(i>=0)
            {
                if (!leftsplit[i].matches("")&&(!columnsprocessed.contains(leftsplit[i])))
                {
                    if(!finalstring.matches(""))
                    {
                        finalstring+=" , ";
                    }

                    if(ascending)
                    {      finalstring += " ORDER BY  " + leftsplit[i] + " ASC ";

                    }
                    else
                    {
                        finalstring += " ORDER BY " + leftsplit[i] + " DSC ";
                    }

                    columnsprocessed.add(leftsplit[i]);

                }

            }
        }


    }

    public  static  void main(String args[])
    {

        ArrayList<String> columns=new ArrayList<>();
        columns.add("price");
        columns.add("cost");

        String sentence ="order the names of the customers by price";
        OrderByClause orderByClause=new OrderByClause(sentence,columns);
        

    }









}